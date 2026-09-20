package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.world.DreadLandsRulers;
import com.github.alexthe666.iceandfire.world.IafDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityDreadQueen extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {
    public static final int MAX_MINIONS = 6;
    public static final int MAX_MINIONS_GROUNDED = 8;
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    public static Animation ANIMATION_SUMMON = Animation.create(15);
    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getUUID(), this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);
    private int animationTick;
    private Animation currentAnimation;
    private int minionCooldown;

    public EntityDreadQueen(EntityType<? extends EntityDreadMob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true) {
            @Override
            public boolean canUse() {
                return !EntityDreadQueen.this.isPassenger() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !EntityDreadQueen.this.isPassenger() && EntityDreadQueen.this.getTarget() == null && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !EntityDreadQueen.this.isPassenger() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !EntityDreadQueen.this.isPassenger() && super.canUse();
            }
        });
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, IDreadMob.class, EntityIceDragon.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (entity, serverLevel) -> DragonUtils.canHostilesTarget(entity)));
        this.targetSelector.addGoal(3, new DreadAITargetNonDread(this, LivingEntity.class, false, (entity, serverLevel) -> DragonUtils.canHostilesTarget(entity)));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, IafConfig.dreadQueenMaxHealth)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.ATTACK_DAMAGE, 5.0D)
            .add(Attributes.FOLLOW_RANGE, 256.0D)
            .add(Attributes.ARMOR, 30.0D);
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.getVehicle() instanceof EntityDragonBase dragon) {
            float yaw = dragon.getYRot();
            this.setYRot(yaw);
            this.yRotO = dragon.yRotO;
            this.setYHeadRot(yaw);
            this.yHeadRotO = dragon.yRotO;
            this.setYBodyRot(dragon.yBodyRot);
            this.yBodyRotO = dragon.yBodyRotO;
            this.setXRot(0.0F);
            this.xRotO = 0.0F;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = this.level().getBlockState(this.blockPosition().below());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, belowBlock), this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), this.getBoundingBox().minY, this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth(), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D);
                }
            }
            this.setDeltaMovement(0, this.getDeltaMovement().y, this.getDeltaMovement().z);
        }
        if (this.level().isClientSide() && this.getAnimation() == ANIMATION_SUMMON) {
            float f = this.yBodyRot * 0.017453292F + Mth.cos(this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            IceAndFire.PROXY.spawnParticle(EnumParticles.Dread_Torch, this.getX() + (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, 0, 0, 0);
            IceAndFire.PROXY.spawnParticle(EnumParticles.Dread_Torch, this.getX() - (double) f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double) f2 * 0.6D, 0, 0, 0);
        }
        if (this.minionCooldown > 0) {
            this.minionCooldown--;
        }
        if (!this.level().isClientSide()) {
            this.keepMountedOnBlackFrost();
            this.shareTargetWithMount();
        }
        Animation animation = this.getAnimation();
        int minionCap = this.isPassenger() ? MAX_MINIONS : MAX_MINIONS_GROUNDED;
        if (!this.level().isClientSide() && this.getTarget() != null && this.getTarget().isAlive()
            && this.minionCooldown == 0
            && (animation == null || animation == IAnimatedEntity.NO_ANIMATION)
            && this.countLivingMinions() < minionCap) {
            this.summonMinions(this.getTarget());
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void keepMountedOnBlackFrost() {
        if (this.isPassenger() || this.isDeadOrDying()) {
            return;
        }
        EntityIceDragon dragon = this.level().getEntitiesOfClass(EntityIceDragon.class, this.getBoundingBox().inflate(16.0D),
            mount -> mount.isAlive() && mount.isBlackFrost() && !mount.isModelDead() && !mount.isVehicle())
            .stream().findFirst().orElse(null);
        if (dragon != null) {
            this.startRiding(dragon, true, true);
        }
    }

    private void shareTargetWithMount() {
        if (this.getVehicle() instanceof EntityIceDragon dragon && dragon.isBlackFrost()) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                dragon.setTarget(target);
            } else if (dragon.getTarget() != null && dragon.getTarget().isAlive()) {
                this.setTarget(dragon.getTarget());
            }
        }
    }

    private void summonMinions(LivingEntity target) {
        int living = this.countLivingMinions();
        int cap = this.isPassenger() ? MAX_MINIONS : MAX_MINIONS_GROUNDED;
        int batch = this.isPassenger() ? (this.random.nextBoolean() ? 1 : 2) : (this.getHealth() < this.getMaxHealth() * 0.4F ? 3 : 2);
        int toSpawn = Math.min(batch, cap - living);
        if (toSpawn <= 0) {
            return;
        }
        this.setAnimation(ANIMATION_SUMMON);
        this.playSound(IafSoundRegistry.DREAD_LICH_SUMMON, this.getSoundVolume(), this.getVoicePitch());
        for (int i = 0; i < toSpawn; i++) {
            boolean knight = this.random.nextBoolean();
            int x = Mth.floor(target.getX()) - 4 + this.random.nextInt(9);
            int z = Mth.floor(target.getZ()) - 4 + this.random.nextInt(9);
            spawnMinion(this.level(), this, target, x + 0.5D, heightFromXZ(this.level(), this.getY(), x, z), z + 0.5D, knight);
        }
        int cooldown = this.isPassenger() ? 140 : 70;
        if (this.getHealth() < this.getMaxHealth() * 0.4F) {
            cooldown = 45;
        }
        this.minionCooldown = cooldown;
    }

    private int countLivingMinions() {
        return this.level().getEntitiesOfClass(EntityDreadMob.class, this.getBoundingBox().inflate(64.0D),
            mob -> mob != this && mob.isAlive() && this.getUUID().equals(mob.getCommanderId())).size();
    }

    public static boolean spawnMinion(Level level, LivingEntity commander, @Nullable LivingEntity target, double x, double y, double z, boolean knight) {
        if (level.isClientSide()) {
            return false;
        }
        EntityDreadMob minion = knight
            ? new EntityDreadKnight(IafEntityRegistry.DREAD_KNIGHT.get(), level)
            : new EntityDreadThrall(IafEntityRegistry.DREAD_THRALL.get(), level);
        minion.snapTo(x, y, z, commander.getYRot(), commander.getXRot());
        if (target != null) {
            minion.setTarget(target);
        }
        if (level instanceof ServerLevelAccessor server) {
            minion.finalizeSpawn(server, server.getCurrentDifficultyAt(BlockPos.containing(x, y, z)), EntitySpawnReason.MOB_SUMMONED, null);
        }
        minion.setCommanderId(commander.getUUID());
        return level.addFreshEntity(minion);
    }

    public static double heightFromXZ(Level level, double aroundY, int x, int z) {
        BlockPos pos = BlockPos.containing(x, aroundY + 7, z);
        while (level.isEmptyBlock(pos) && pos.getY() > level.getMinY() + 2) {
            pos = pos.below();
        }
        return pos.getY() + 1.0D;
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        this.minionCooldown = compound.getIntOr("MinionCooldown", 0);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("MinionCooldown", this.minionCooldown);
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        if (source.getEntity() instanceof EntityIceDragon dragon && dragon.isBlackFrost()) {
            return false;
        }
        if (this.getVehicle() instanceof EntityIceDragon) {
            if (source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)
                || source.is(net.minecraft.world.damagesource.DamageTypes.FALL)
                || source.is(net.minecraft.world.damagesource.DamageTypes.FLY_INTO_WALL)
                || source.is(net.minecraft.world.damagesource.DamageTypes.CRAMMING)) {
                return false;
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        if (this.level() instanceof ServerLevel server && IafDimensions.isDreadLands(server)) {
            DreadLandsRulers.markQueenDefeated(server);
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setAnimation(ANIMATION_SPAWN);
        this.populateDefaultEquipmentSlots(this.random, difficultyIn);
        return data;
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NotNull net.minecraft.util.RandomSource random, @NotNull DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(IafItemRegistry.DREAD_QUEEN_SWORD.get()));
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(IafItemRegistry.DREAD_QUEEN_STAFF.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPAWN, ANIMATION_SUMMON};
    }

    @Override
    public Entity getCommander() {
        return null;
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }
}
