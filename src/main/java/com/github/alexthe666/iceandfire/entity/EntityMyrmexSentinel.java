package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.google.common.base.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import com.github.alexthe666.iceandfire.entity.util.IafItemListing;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityMyrmexSentinel extends EntityMyrmexBase {

    public static final Animation ANIMATION_GRAB = Animation.create(15);
    public static final Animation ANIMATION_NIBBLE = Animation.create(10);
    public static final Animation ANIMATION_STING = Animation.create(25);
    public static final Animation ANIMATION_SLASH = Animation.create(25);
    public static final Identifier DESERT_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_sentinel_desert");
    public static final Identifier JUNGLE_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_sentinel_jungle");
    private static final Identifier TEXTURE_DESERT = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_desert_sentinel.png");
    private static final Identifier TEXTURE_JUNGLE = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_jungle_sentinel.png");
    private static final Identifier TEXTURE_DESERT_HIDDEN = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_desert_sentinel_hidden.png");
    private static final Identifier TEXTURE_JUNGLE_HIDDEN = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_jungle_sentinel_hidden.png");
    private static final EntityDataAccessor<Boolean> HIDING = SynchedEntityData.defineId(EntityMyrmexSentinel.class, EntityDataSerializers.BOOLEAN);
    public float holdingProgress;
    public float hidingProgress;
    public int visibleTicks = 0;
    public int daylightTicks = 0;

    public EntityMyrmexSentinel(EntityType t, Level worldIn) {
        super(t, worldIn);
    }

    @Override
    protected IafItemListing[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_SENTINEL.get(1) : MyrmexTrades.DESERT_SENTINEL.get(1);
    }

    @Override
    protected IafItemListing[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_SENTINEL.get(2) : MyrmexTrades.DESERT_SENTINEL.get(2);
    }

    @Override
    protected void dropFromLootTable(@NotNull ServerLevel level, @NotNull DamageSource source, boolean causedByPlayer) {
        this.dropFromLootTable(level, source, causedByPlayer, com.github.alexthe666.iceandfire.entity.util.IafLoot.table(isJungle() ? JUNGLE_LOOT : DESERT_LOOT));
    }

    @Override
    protected int getBaseExperienceReward(@NotNull ServerLevel level) {
        return 8;
    }

    public Entity getHeldEntity() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        LivingEntity attackTarget = this.getTarget();
        if (visibleTicks > 0) {
            visibleTicks--;
        } else {
            visibleTicks = 0;
        }
        if (attackTarget != null) {
            visibleTicks = 100;
        }
        if (this.canSeeSky()) {
            this.daylightTicks++;
        } else {
            this.daylightTicks = 0;
        }
        boolean holding = getHeldEntity() != null;
        boolean hiding = isHiding() && !this.hasCustomer();
        if ((holding || this.isOnResin() || attackTarget != null) || visibleTicks > 0) {
            this.setHiding(false);
        }
        if (holding && holdingProgress < 20.0F) {
            holdingProgress += 1.0F;
        } else if (!holding && holdingProgress > 0.0F) {
            holdingProgress -= 1.0F;
        }
        if (hiding) {
            this.setYRot(this.yRotO);
        }
        if (hiding && hidingProgress < 20.0F) {
            hidingProgress += 1.0F;
        } else if (!hiding && hidingProgress > 0.0F) {
            hidingProgress -= 1.0F;
        }
        if (this.getHeldEntity() != null) {
            this.setAnimation(ANIMATION_NIBBLE);
            if (this.getAnimationTick() == 5) {
                this.playBiteSound();
                this.getHeldEntity().hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 6));
            }
        }
        if (this.getAnimation() == ANIMATION_GRAB && attackTarget != null && this.getAnimationTick() == 7) {
            this.playStingSound();
            if (this.getAttackBounds().intersects(attackTarget.getBoundingBox())) {
                attackTarget.hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 2));
                //Make sure it doesn't grab a dead dragon
                if (attackTarget instanceof EntityDragonBase) {
                    if (!((EntityDragonBase) attackTarget).isMobDead()) {
                        attackTarget.startRiding(this);
                    }
                } else {
                    attackTarget.startRiding(this);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_SLASH && attackTarget != null && this.getAnimationTick() % 5 == 0 && this.getAnimationTick() <= 20) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(attackTarget.getBoundingBox())) {
                attackTarget.hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()) / 4);
            }
        }
        if (this.getAnimation() == ANIMATION_STING && (this.getAnimationTick() == 0 || this.getAnimationTick() == 10)) {
            this.playStingSound();
        }
        if (this.getAnimation() == ANIMATION_STING && attackTarget != null && (this.getAnimationTick() == 6 || this.getAnimationTick() == 16)) {
            double dist = this.distanceToSqr(attackTarget);
            if (dist < 18) {
                attackTarget.hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                attackTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 3));
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAIFindHidingSpot(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(1, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(3, new MyrmexAILeaveHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 4, true, true, (entity, serverLevel) ->
            entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexSentinel.this, entity) && DragonUtils.isAlive(entity) && !(entity instanceof Enemy)));
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HIDING, Boolean.FALSE);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 60D)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            //ATTACK
            .add(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 3D)
            //FOLLOW RANGE
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            //ARMOR
            .add(Attributes.ARMOR, 12.0D);
    }

    @Override
    public AttributeSupplier.Builder getConfigurableAttributes() {
        return bakeAttributes();
    }

    @Override
    public Identifier getAdultTexture() {
        if (isHiding()) {
            return isJungle() ? TEXTURE_JUNGLE_HIDDEN : TEXTURE_DESERT_HIDDEN;

        } else {
            return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
        }
    }

    @Override
    public float getModelScale() {
        return 0.8F;
    }

    @Override
    public int getCasteImportance() {
        return 2;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Hiding", this.isHiding());
        tag.putInt("DaylightTicks", daylightTicks);
    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        this.setHiding(tag.getBooleanOr("Hiding", false));
        this.daylightTicks = tag.getIntOr("DaylightTicks", 0);
    }

    @Override
    public boolean shouldLeaveHive() {
        return true;
    }

    @Override
    public boolean shouldEnterHive() {
        return false;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (this.hasPassenger(passenger)) {
            yBodyRot = getYRot();
            float radius = 1.25F;
            float extraY = 0.35F;
            if (this.getAnimation() == ANIMATION_GRAB) {
                int modTick = Mth.clamp(this.getAnimationTick(), 0, 10);
                radius = 3.25F - modTick * 0.2F;
                extraY = modTick * 0.035F;
            }
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            if (passenger.getBbHeight() >= 1.75F) {
                extraY = passenger.getBbHeight() - 2F;
            }
            passenger.setPos(this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ);
        }
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        if (amount >= 1.0D && !this.getPassengers().isEmpty() && random.nextInt(2) == 0) {
            for (Entity entity : this.getPassengers()) {
                entity.stopRiding();
            }
        }
        visibleTicks = 300;
        this.setHiding(false);
        return super.hurtServer(level, source, amount);
    }

    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_SLASH && this.getAnimation() != ANIMATION_GRAB && this.getHeldEntity() == null) {
            if (this.getRandom().nextInt(2) == 0 && entityIn.getBbWidth() < 2F) {
                this.setAnimation(ANIMATION_GRAB);
            } else {
                this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_STING : ANIMATION_SLASH);
            }
            visibleTicks = 300;
            return true;
        }
        return false;
    }

    @Override
    public boolean needsGaurding() {
        return false;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_SLASH, ANIMATION_STING, ANIMATION_GRAB, ANIMATION_NIBBLE};
    }

    @Override
    public boolean canMove() {
        return super.canMove() && this.getHeldEntity() == null && !isHiding();
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }


    public boolean isHiding() {
        return this.entityData.get(HIDING).booleanValue();
    }

    public void setHiding(boolean hiding) {
        this.entityData.set(HIDING, hiding);
    }

    @Override
    public int getVillagerXp() {
        return 4;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public boolean isClientSide() {
        return this.getLevel().isClientSide();
    }
}
