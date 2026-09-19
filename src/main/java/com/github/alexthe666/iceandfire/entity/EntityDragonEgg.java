package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import com.github.alexthe666.iceandfire.entity.util.IafDrops;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityDragonEgg extends LivingEntity implements IBlacklistedFromStatues, IDeadMob {

    protected static final EntityDataAccessor<String> OWNER_UNIQUE_ID = SynchedEntityData.defineId(EntityDragonEgg.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DRAGON_TYPE = SynchedEntityData.defineId(EntityDragonEgg.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DRAGON_AGE = SynchedEntityData.defineId(EntityDragonEgg.class, EntityDataSerializers.INT);

    public EntityDragonEgg(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 10.0D)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0D);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Color", (byte) this.getEggType().ordinal());
        tag.putInt("DragonAge", this.getDragonAge());
        try {
            if (this.getOwnerId() == null) {
                tag.putString("OwnerUUID", "");
            } else {
                tag.putString("OwnerUUID", this.getOwnerId().toString());
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput tag) {
        super.readAdditionalSaveData(tag);
        this.setEggType(EnumDragonEgg.values()[tag.getIntOr("Color", 0)]);
        this.setDragonAge(tag.getIntOr("DragonAge", 0));
        String s = tag.getString("OwnerUUID").orElse(null);
        if (s == null) {
            String s1 = tag.getStringOr("Owner", "");
            UUID converedUUID = this.level() instanceof ServerLevel server
                ? OldUsersConverter.convertMobOwnerIfNecessary(server.getServer(), s1)
                : null;
            s = converedUUID == null ? s1 : converedUUID.toString();
        }
        if (!s.isEmpty()) {
            this.setOwnerId(UUID.fromString(s));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DRAGON_TYPE, 0);
        builder.define(DRAGON_AGE, 0);
        builder.define(OWNER_UNIQUE_ID, "");
    }

    @Nullable
    public UUID getOwnerId() {
        String stored = this.entityData.get(OWNER_UNIQUE_ID);
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(stored);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public void setOwnerId(@Nullable UUID owner) {
        this.entityData.set(OWNER_UNIQUE_ID, owner == null ? "" : owner.toString());
    }

    public EnumDragonEgg getEggType() {
        return EnumDragonEgg.values()[this.getEntityData().get(DRAGON_TYPE).intValue()];
    }

    public void setEggType(EnumDragonEgg newtype) {
        this.getEntityData().set(DRAGON_TYPE, newtype.ordinal());
    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource i) {
        return i.getEntity() != null && super.isInvulnerableTo(level, i);
    }

    public int getDragonAge() {
        return this.getEntityData().get(DRAGON_AGE).intValue();
    }

    public void setDragonAge(int i) {
        this.getEntityData().set(DRAGON_AGE, i);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.setAirSupply(200);
            getEggType().dragonType.updateEggCondition(this);
        }
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource var1, float var2) {
        if (!this.level().isClientSide() && !var1.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY) && !isRemoved()) {
            IafDrops.spawn(this, this.getItem().getItem(), 1);
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    private ItemStack getItem() {
        return switch (getEggType().ordinal()) {
            default -> new ItemStack(IafItemRegistry.DRAGONEGG_RED.get());
            case 1 -> new ItemStack(IafItemRegistry.DRAGONEGG_GREEN.get());
            case 2 -> new ItemStack(IafItemRegistry.DRAGONEGG_BRONZE.get());
            case 3 -> new ItemStack(IafItemRegistry.DRAGONEGG_GRAY.get());
            case 4 -> new ItemStack(IafItemRegistry.DRAGONEGG_BLUE.get());
            case 5 -> new ItemStack(IafItemRegistry.DRAGONEGG_WHITE.get());
            case 6 -> new ItemStack(IafItemRegistry.DRAGONEGG_SAPPHIRE.get());
            case 7 -> new ItemStack(IafItemRegistry.DRAGONEGG_SILVER.get());
            case 8 -> new ItemStack(IafItemRegistry.DRAGONEGG_ELECTRIC.get());
            case 9 -> new ItemStack(IafItemRegistry.DRAGONEGG_AMYTHEST.get());
            case 10 -> new ItemStack(IafItemRegistry.DRAGONEGG_COPPER.get());
            case 11 -> new ItemStack(IafItemRegistry.DRAGONEGG_BLACK.get());
        };
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(Player player) {
        this.setOwnerId(player.getUUID());
    }

    @Override
    public boolean isMobDead() {
        return true;
    }
}
