package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.IafOwners;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class EntityMyrmexSwarmer extends EntityMyrmexRoyal {

    private static final EntityDataAccessor<String> SUMMONER_ID = SynchedEntityData.defineId(EntityMyrmexSwarmer.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> TICKS_ALIVE = SynchedEntityData.defineId(EntityMyrmexSwarmer.class, EntityDataSerializers.INT);

    public EntityMyrmexSwarmer(EntityType type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new EntityMyrmexRoyal.FlyMoveHelper(this);
        this.navigation = createNavigator(worldIn, AdvancedPathNavigate.MovementType.FLYING);
        switchNavigator(false);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 5)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            //ATTACK
            .add(Attributes.ATTACK_DAMAGE, 2)
            //FOLLOW RANGE
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            //ARMOR
            .add(Attributes.ARMOR, 0D);
    }

    @Override
    protected int getBaseExperienceReward(@NotNull ServerLevel level) {
        return 0;
    }

    @Override
    protected void switchNavigator(boolean onLand) {
    }

    @Override
    protected double attackDistance() {
        return 25;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MyrmexAIFollowSummoner(this, 1.0D, 10.0F, 5.0F));
        this.goalSelector.addGoal(2, new AIFlyAtTarget());
        this.goalSelector.addGoal(3, new AIFlyRandom());
        this.goalSelector.addGoal(4, new EntityAIAttackMeleeNoCooldown(this, 1.0D, true));
        this.goalSelector.addGoal(5, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new MyrmexAISummonerHurtByTarget(this));
        this.targetSelector.addGoal(3, new MyrmexAISummonerHurtTarget(this));
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (entityIn instanceof EntityMyrmexSwarmer) {
            super.doPush(entityIn);
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SUMMONER_ID, "");
        builder.define(TICKS_ALIVE, 0);
    }

    @Nullable
    public LivingEntity getSummoner() {
        try {
            UUID uuid = this.getSummonerUUID();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean iafIsAlliedTo(@NotNull Entity entityIn) {
        if (entityIn == null) {
            return false;
        }
        if (this.getSummonerUUID() == null || entityIn instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID() == null) {
            return false;
        }
        if (entityIn instanceof TamableAnimal) {
            UUID ownerID = IafOwners.getUUID((TamableAnimal) entityIn);
            return ownerID != null && ownerID.equals(this.getSummonerUUID());
        }
        return entityIn.getUUID().equals(this.getSummonerUUID()) || entityIn instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID() != null && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID().equals(this.getSummonerUUID());
    }

    public void setSummonerID(@Nullable UUID uuid) {
        this.entityData.set(SUMMONER_ID, uuid == null ? "" : uuid.toString());
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (this.getSummonerUUID() == null) {
            compound.putString("SummonerUUID", "");
        } else {
            compound.putString("SummonerUUID", this.getSummonerUUID().toString());
        }
        compound.putInt("SummonTicks", this.getTicksAlive());

    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        String s = "";
        if (compound.read("SummonerUUID", UUIDUtil.CODEC).isPresent()) {
            s = compound.getStringOr("SummonerUUID", "");
        }
        if (!s.isEmpty()) {
            try {
                this.setSummonerID(UUID.fromString(s));
            } catch (Throwable var4) {
            }
        }
        this.setTicksAlive(compound.getIntOr("SummonTicks", 0));
    }

    public void setSummonedBy(Player player) {
        this.setSummonerID(player.getUUID());
    }

    @Nullable
    public UUID getSummonerUUID() {
        String stored = this.entityData.get(SUMMONER_ID);
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(stored);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public int getTicksAlive() {
        return this.entityData.get(TICKS_ALIVE).intValue();
    }

    public void setTicksAlive(int ticks) {
        this.entityData.set(TICKS_ALIVE, ticks);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        setFlying(true);
        boolean flying = this.isFlying() && !this.onGround();
        setTicksAlive(getTicksAlive() + 1);
        if (flying) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08D, 0));
            if (this.moveControl.getWantedY() > this.getY()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.08D, 0));
            }
        }
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.2D, 0));
        }
        if (this.getTarget() != null) {
            this.moveControl.setWantedPosition(this.getTarget().getX(), this.getTarget().getBoundingBox().minY, this.getTarget().getZ(), 1.0F);
            if (this.getAttackBounds().intersects(this.getTarget().getBoundingBox())) {
                this.setAnimation(random.nextBoolean() ? ANIMATION_BITE : ANIMATION_STING);
            }
        }
        if (this.getTicksAlive() > 1800 && this.level() instanceof ServerLevel serverLevel) {
            this.kill(serverLevel);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < attackDistance()) {
                this.getTarget().hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getTarget() != null && this.getAnimationTick() == 6) {
            this.playStingSound();
            double dist = this.distanceToSqr(this.getTarget());
            if (dist < attackDistance()) {
                this.getTarget().hurt(this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                // After calling hurt the target can become null due to forge hooks
                if (this.getTarget() != null)
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.POISON, 70, 1));
            }
        }
    }

    @Override
    public int getGrowthStage() {
        return 2;
    }

    @Override
    protected boolean shouldDropLoot(@NotNull ServerLevel level) {
        return false;
    }

    @Override
    public float getModelScale() {
        return 0.25F;
    }

    @Override
    public boolean shouldHaveNormalAI() {
        return false;
    }

    @Override
    public int getCasteImportance() {
        return 0;
    }

    @Override
    public boolean isBreedingSeason() {
        return false;
    }

    public boolean shouldAttackEntity(LivingEntity attacker, LivingEntity LivingEntity) {
        return !isAlliedTo(attacker);
    }
}
