package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class EntityStoneStatue extends LivingEntity implements IBlacklistedFromStatues {

    private static final EntityDataAccessor<String> TRAPPED_ENTITY_TYPE = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TRAPPED_ENTITY_DATA = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_WIDTH = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_HEIGHT = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_SCALE = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CRACK_AMOUNT = SynchedEntityData.defineId(EntityStoneStatue.class, EntityDataSerializers.INT);
    private EntityDimensions stoneStatueSize = EntityDimensions.fixed(0.5F, 0.5F);

    public EntityStoneStatue(EntityType<? extends LivingEntity> t, Level worldIn) {
        super(t, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 20)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.0D)
            //ATTACK
            .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public static EntityStoneStatue buildStatueEntity(LivingEntity parent) {
        EntityStoneStatue statue = IafEntityRegistry.STONE_STATUE.get().create(parent.level(), net.minecraft.world.entity.EntitySpawnReason.CONVERSION);
        CompoundTag entityTag = new CompoundTag();
        try {
            if (!(parent instanceof Player)) {
                entityTag = com.github.alexthe666.iceandfire.entity.util.IafEntityNbt.saveWithoutId(parent);
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.debug("Encountered issue creating stone statue from {}", parent);
        }
        if (statue == null) {
            statue = new EntityStoneStatue(IafEntityRegistry.STONE_STATUE.get(), parent.level());
        }
        statue.setTrappedTag(entityTag);
        var key = ForgeRegistries.ENTITY_TYPES.getKey(parent.getType());
        statue.setTrappedEntityTypeString(key != null ? key.toString() : "minecraft:pig");
        statue.setTrappedEntityWidth(parent.getBbWidth());
        statue.setTrappedHeight(parent.getBbHeight());
        statue.setTrappedScale(parent.getAgeScale());

        return statue;
    }

    @Override
    public void push(@NotNull Entity entityIn) {
    }

    @Override
    public void baseTick() {

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        builder.define(TRAPPED_ENTITY_DATA, "{}");
        builder.define(TRAPPED_ENTITY_WIDTH, 0.5F);
        builder.define(TRAPPED_ENTITY_HEIGHT, 0.5F);
        builder.define(TRAPPED_ENTITY_SCALE, 1F);
        builder.define(CRACK_AMOUNT, 0);
    }

    public EntityType getTrappedEntityType() {
        String str = getTrappedEntityTypeString();
        return BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(str)).orElse(EntityTypes.PIG);
    }

    public String getTrappedEntityTypeString() {
        return this.entityData.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.entityData.set(TRAPPED_ENTITY_TYPE, string);
    }

    public CompoundTag getTrappedTag() {
        try {
            return net.minecraft.nbt.TagParser.parseCompoundFully(this.entityData.get(TRAPPED_ENTITY_DATA));
        } catch (Exception e) {
            return new CompoundTag();
        }
    }

    public void setTrappedTag(CompoundTag tag) {
        this.entityData.set(TRAPPED_ENTITY_DATA, tag.toString());
    }

    public float getTrappedWidth() {
        return this.entityData.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float size) {
        this.entityData.set(TRAPPED_ENTITY_WIDTH, size);
    }

    public float getTrappedHeight() {
        return this.entityData.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float size) {
        this.entityData.set(TRAPPED_ENTITY_HEIGHT, size);
    }

    public float getTrappedScale() {
        return this.entityData.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float size) {
        this.entityData.set(TRAPPED_ENTITY_SCALE, size);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CrackAmount", this.getCrackAmount());
        tag.putFloat("StatueWidth", this.getTrappedWidth());
        tag.putFloat("StatueHeight", this.getTrappedHeight());
        tag.putFloat("StatueScale", this.getTrappedScale());
        tag.putString("StatueEntityType", this.getTrappedEntityTypeString());
        tag.store("StatueEntityTag", CompoundTag.CODEC, this.getTrappedTag());
    }

    @Override
    public float getAgeScale() {
        return this.getTrappedScale();
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput tag) {
        super.readAdditionalSaveData(tag);
        this.setCrackAmount(tag.getByteOr("CrackAmount", (byte) 0));
        this.setTrappedEntityWidth(tag.getFloatOr("StatueWidth", 0.0F));
        this.setTrappedHeight(tag.getFloatOr("StatueHeight", 0.0F));
        this.setTrappedScale(tag.getFloatOr("StatueScale", 0.0F));
        this.setTrappedEntityTypeString(tag.getStringOr("StatueEntityType", ""));
        tag.read("StatueEntityTag", CompoundTag.CODEC).ifPresent(this::setTrappedTag);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        return source.is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    protected EntityDimensions getDefaultDimensions(@NotNull Pose poseIn) {
        return stoneStatueSize;
    }

    @Override
    public void tick() {
        super.tick();
        this.setYRot(this.yBodyRot);
        this.yHeadRot = this.getYRot();
        if (Math.abs(this.getBbWidth() - getTrappedWidth()) > 0.01 || Math.abs(this.getBbHeight() - getTrappedHeight()) > 0.01) {
            double prevX = this.getX();
            double prevZ = this.getZ();
            this.stoneStatueSize = EntityDimensions.scalable(getTrappedWidth(), getTrappedHeight());
            refreshDimensions();
            this.setPos(prevX, this.getY(), prevZ);
        }
    }

    @Override
    public void kill(@NotNull ServerLevel level) {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public int getCrackAmount() {
        return this.entityData.get(CRACK_AMOUNT);
    }

    public void setCrackAmount(int crackAmount) {
        this.entityData.set(CRACK_AMOUNT, crackAmount);
    }


    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }
}
