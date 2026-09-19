package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.entity.util.IafDrops;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityMobSkull extends Animal implements IBlacklistedFromStatues, IDeadMob {

    private static final EntityDataAccessor<Float> SKULL_DIRECTION = SynchedEntityData.defineId(EntityMobSkull.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SKULL_ENUM = SynchedEntityData.defineId(EntityMobSkull.class, EntityDataSerializers.INT);

    public EntityMobSkull(EntityType t, Level worldIn) {
        super(t, worldIn);
        // 1.18 noCulling is renderer affectedByCulling() == false
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 10.0D)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource i) {
        return i.getEntity() != null;
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    public boolean isOnWall() {
        return this.level().isEmptyBlock(this.blockPosition().below());
    }

    public void onUpdate() {
        this.yBodyRotO = 0;
        this.yHeadRotO = 0;
        this.yBodyRot = 0;
        this.yHeadRot = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKULL_DIRECTION, 0F);
        builder.define(SKULL_ENUM, 0);
    }

    public float getYaw() {
        return this.getEntityData().get(SKULL_DIRECTION).floatValue();
    }

    public void setYaw(float var1) {
        this.getEntityData().set(SKULL_DIRECTION, var1);
    }

    private int getEnumOrdinal() {
        return this.getEntityData().get(SKULL_ENUM).intValue();
    }

    private void setEnumOrdinal(int var1) {
        this.getEntityData().set(SKULL_ENUM, var1);
    }

    public EnumSkullType getSkullType() {
        return EnumSkullType.values()[Mth.clamp(getEnumOrdinal(), 0, EnumSkullType.values().length - 1)];
    }

    public void setSkullType(EnumSkullType skullType) {
        setEnumOrdinal(skullType.ordinal());
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource var1, float var2) {
        this.turnIntoItem();
        return super.hurtServer(level, var1, var2);
    }

    public void turnIntoItem() {
        if (isRemoved())
            return;
        this.remove(RemovalReason.DISCARDED);
        ItemStack stack = new ItemStack(getSkullType().skull_item.get(), 1);
        if (!this.level().isClientSide())
            IafDrops.spawn(this, stack, 0.0F);
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            this.setYaw(player.getYRot());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        this.setYaw(compound.getFloatOr("SkullYaw", 0.0F));
        this.setEnumOrdinal(compound.getIntOr("SkullType", 0));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        compound.putFloat("SkullYaw", this.getYaw());
        compound.putInt("SkullType", this.getEnumOrdinal());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverWorld, @NotNull AgeableMob ageable) {
        return null;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
