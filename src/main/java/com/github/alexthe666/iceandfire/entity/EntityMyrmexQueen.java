package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.entity.util.IafDrops;

import com.github.alexthe666.iceandfire.block.IafMaterials;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.base.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityMyrmexQueen extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final Animation ANIMATION_EGG = Animation.create(20);
    public static final Animation ANIMATION_DIGNEST = Animation.create(45);
    public static final Identifier DESERT_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_queen_desert");
    public static final Identifier JUNGLE_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_queen_jungle");
    private static final Identifier TEXTURE_DESERT = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_desert_queen.png");
    private static final Identifier TEXTURE_JUNGLE = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_jungle_queen.png");
    private static final EntityDataAccessor<Boolean> HASMADEHOME = SynchedEntityData.defineId(EntityMyrmexQueen.class, EntityDataSerializers.BOOLEAN);
    private int eggTicks = 0;

    public EntityMyrmexQueen(EntityType<EntityMyrmexQueen> t, Level worldIn) {
        super(t, worldIn);
    }

    @Override
    protected void dropFromLootTable(@NotNull ServerLevel level, @NotNull DamageSource source, boolean causedByPlayer) {
        this.dropFromLootTable(level, source, causedByPlayer, com.github.alexthe666.iceandfire.entity.util.IafLoot.table(isJungle() ? JUNGLE_LOOT : DESERT_LOOT));
    }

    @Override
    protected int getBaseExperienceReward(@NotNull ServerLevel level) {
        return 20;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HASMADEHOME, Boolean.TRUE);
    }

    @Override
    protected IafItemListing[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_QUEEN.get(1) : MyrmexTrades.DESERT_QUEEN.get(1);
    }

    @Override
    protected IafItemListing[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_QUEEN.get(2) : MyrmexTrades.DESERT_QUEEN.get(2);
    }

    @Override
    public void setCustomName(Component name) {
        if (this.getHive() != null) {
            if (!this.getHive().colonyName.equals(name.getContents())) {
                this.getHive().colonyName = name.getString();
            }
        }
        super.setCustomName(name);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EggTicks", eggTicks);
        tag.putBoolean("MadeHome", this.hasMadeHome());

    }

    @Override
    public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);
        this.eggTicks = tag.getIntOr("EggTicks", 0);
        this.setMadeHome(tag.getBooleanOr("MadeHome", false));
    }

    public boolean hasMadeHome() {
        return this.entityData.get(HASMADEHOME).booleanValue();
    }

    public void setMadeHome(boolean madeHome) {
        this.entityData.set(HASMADEHOME, madeHome);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAnimation() == ANIMATION_DIGNEST) {
            spawnGroundEffects(3);
        }
        if (this.getHive() != null) {
            this.getHive().tick(0, level());
        }

        if (hasMadeHome() && this.getGrowthStage() >= 2 && !this.canSeeSky()) {
            eggTicks++;
        } else if (this.canSeeSky()) {
            this.setAnimation(ANIMATION_DIGNEST);
            if (this.getAnimationTick() == 42) {
                int down = Math.max(15, this.blockPosition().getY() - 20 + this.getRandom().nextInt(10));
                BlockPos genPos = BlockPos.containing(this.getX(), down, this.getZ());
                if (!GenericGriefEvent.BUS.post(new GenericGriefEvent(this, genPos.getX(), genPos.getY(), genPos.getZ()))) {
                    WorldGenMyrmexHive hiveGen = new WorldGenMyrmexHive(true, this.isJungle(), NoneFeatureConfiguration.CODEC);
                    if (!this.level().isClientSide() && level() instanceof ServerLevel) {
                        hiveGen.placeSmallGen((ServerLevel) level(), this.getRandom(), genPos);
                    }
                    this.setMadeHome(true);
                    this.snapTo(genPos.getX(), down, genPos.getZ(), 0, 0);
                    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30));
                    this.setHive(hiveGen.hive);
                    for (int i = 0; i < 3; i++) {
                        EntityMyrmexWorker worker = new EntityMyrmexWorker(IafEntityRegistry.MYRMEX_WORKER.get(),
                            level());
                        worker.copyPosition(this);
                        worker.setHive(this.getHive());
                        worker.setJungleVariant(this.isJungle());
                        if (!this.level().isClientSide()) {
                            this.level().addFreshEntity(worker);
                        }
                    }
                    return;
                }
            }
        }
        if (!this.level().isClientSide() && eggTicks > IafConfig.myrmexPregnantTicks && this.getHive() == null || !this.level().isClientSide() && this.getHive() != null && this.getHive().repopulate() && eggTicks > IafConfig.myrmexPregnantTicks) {
            float radius = -5.25F;
            float angle = (0.01745329251F * this.yBodyRot);
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos eggPos = BlockPos.containing(this.getX() + extraX, this.getY() + 0.75F, this.getZ() + extraZ);
            if (this.level().isEmptyBlock(eggPos)) {
                this.setAnimation(ANIMATION_EGG);
                if (this.getAnimationTick() == 10) {
                    EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG.get(), this.level());
                    egg.setJungle(this.isJungle());
                    int caste = getRandomCaste(level(), this.getRandom(), getHive() == null || getHive().reproduces);
                    egg.setMyrmexCaste(caste);
                    egg.snapTo(this.getX() + extraX, this.getY() + 0.75F, this.getZ() + extraZ, 0, 0);
                    if (getHive() != null) {
                        egg.hiveUUID = this.getHive().hiveUUID;
                    }
                    if (!this.level().isClientSide()) {
                        this.level().addFreshEntity(egg);
                    }
                    eggTicks = 0;
                }

            }


        }
        if (this.getAnimation() == ANIMATION_BITE && this.getTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(this.getTarget().getBoundingBox())) {
                if (this.level() instanceof ServerLevel server) {
                    this.getTarget().hurtServer(server, this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                }
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAnimationTick() == 0) {
            this.playStingSound();
        }
        if (this.getAnimation() == ANIMATION_STING && this.getTarget() != null && this.getAnimationTick() == 6) {
            if (this.getAttackBounds().intersects(this.getTarget().getBoundingBox())) {
                LivingEntity attackTarget = this.getTarget();
                if (this.level() instanceof ServerLevel server) {
                    attackTarget.hurtServer(server, this.damageSources().mobAttack(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                }
                attackTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
                attackTarget.hurtMarked = true;
                float f = Mth.sqrt((float) (0.5 * 0.5 + 0.5 * 0.5));
                attackTarget.hurtMarked = true;
                attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().multiply(0.5D, 1, 0.5D));
                attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().add(-0.5 / f * 4, 1, -0.5 / f * 4));

                if (attackTarget.onGround()) {
                    attackTarget.setDeltaMovement(attackTarget.getDeltaMovement().add(0, 0.4, 0));
                }
            }
        }

    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, @NotNull DamageSource source) {
        return super.isInvulnerableTo(level, source) || this.getAnimation() == ANIMATION_DIGNEST;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(1, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(3, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAIWanderHiveCenter(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexQueenAIWander(this, 1D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, (entity, serverLevel) -> entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexQueen.this, entity) && DragonUtils.isAlive(entity) && !(entity instanceof Enemy)));

    }

    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public boolean isInHive() {
        if (getHive() != null) {
            for (BlockPos pos : getHive().getAllRooms()) {
                if (isCloseEnoughToTarget(MyrmexHive.getGroundedPos(getLevel(), pos), 300))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldMoveThroughHive() {
        return false;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 120D)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.2D)
            //ATTACK
            .add(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 3.5D)
            //FOLLOW RANGE
            .add(Attributes.FOLLOW_RANGE, 128.0D)
            //ARMOR
            .add(Attributes.ARMOR, 15.0D);
    }

    @Override
    public AttributeSupplier.Builder getConfigurableAttributes() {
        return bakeAttributes();
    }

    @Override
    public Identifier getAdultTexture() {
        return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
    }

    @Override
    public float getModelScale() {
        return 1.75F;
    }

    @Override
    public int getCasteImportance() {
        return 3;
    }

    @Override
    public boolean shouldLeaveHive() {
        return false;
    }

    @Override
    public boolean shouldEnterHive() {
        return true;
    }

    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
            if (!this.level().isClientSide() && this.getRandom().nextInt(3) == 0 && this.getItemInHand(InteractionHand.MAIN_HAND) != ItemStack.EMPTY) {
                IafDrops.spawn(this, this.getItemInHand(InteractionHand.MAIN_HAND), 0);
                this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
            if (!this.getPassengers().isEmpty()) {
                for (Entity entity : this.getPassengers()) {
                    entity.stopRiding();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canMove() {
        return super.canMove() && this.hasMadeHome();
    }

    public void spawnGroundEffects(float size) {
        for (int i = 0; i < size * 3; i++) {
            for (int i1 = 0; i1 < 10; i1++) {
                double motionX = getRandom().nextGaussian() * 0.07D;
                double motionY = getRandom().nextGaussian() * 0.07D;
                double motionZ = getRandom().nextGaussian() * 0.07D;
                float radius = size * random.nextFloat();
                float angle = (0.01745329251F * this.yBodyRot) * 3.14F * random.nextFloat();
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * Mth.cos(angle);

                BlockState BlockState = this.level().getBlockState(BlockPos.containing(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) - 1, Mth.floor(this.getZ() + extraZ)));
                if (!IafMaterials.isAir(BlockState)) {
                    if (this.level().isClientSide()) {
                        this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockState), true, true, this.getX() + extraX, this.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING, ANIMATION_EGG, ANIMATION_DIGNEST};
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}
