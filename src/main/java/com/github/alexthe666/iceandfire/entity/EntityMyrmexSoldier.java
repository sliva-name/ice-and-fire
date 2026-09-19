package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import com.github.alexthe666.iceandfire.entity.util.IafDrops;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.google.common.base.Predicate;
import net.minecraft.resources.Identifier;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityMyrmexSoldier extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final Identifier DESERT_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_soldier_desert");
    public static final Identifier JUNGLE_LOOT = Identifier.fromNamespaceAndPath("iceandfire", "entities/myrmex_soldier_jungle");
    private static final Identifier TEXTURE_DESERT = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_desert_soldier.png");
    private static final Identifier TEXTURE_JUNGLE = Identifier.parse("iceandfire:textures/models/myrmex/myrmex_jungle_soldier.png");
    public EntityMyrmexBase guardingEntity = null;

    public EntityMyrmexSoldier(EntityType<EntityMyrmexSoldier> t, Level worldIn) {
        super(t, worldIn);
    }

    @Override
    protected IafItemListing[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_SOLDIER.get(1) : MyrmexTrades.DESERT_SOLDIER.get(1);
    }

    @Override
    protected IafItemListing[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_SOLDIER.get(2) : MyrmexTrades.DESERT_SOLDIER.get(2);
    }

    @Override
    protected void dropFromLootTable(@NotNull ServerLevel level, @NotNull DamageSource source, boolean causedByPlayer) {
        this.dropFromLootTable(level, source, causedByPlayer, com.github.alexthe666.iceandfire.entity.util.IafLoot.table(isJungle() ? JUNGLE_LOOT : DESERT_LOOT));
    }

    @Override
    protected int getBaseExperienceReward(@NotNull ServerLevel level) {
        return 5;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        /*if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAnimationTick() == 0) {
            this.playStingSound();
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 200, 2));
            }
        }*/
        if (this.guardingEntity != null) {
            this.guardingEntity.isBeingGuarded = true;
            this.isEnteringHive = this.guardingEntity.isEnteringHive;
            if (!this.guardingEntity.isAlive()) {
                this.guardingEntity.isBeingGuarded = false;
                this.guardingEntity = null;
            }
        }

    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(1, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MyrmexAIEscortEntity(this, 1.0D));
        this.goalSelector.addGoal(2, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAILeaveHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIMoveThroughHive(this, 1.0D));
        this.goalSelector.addGoal(6, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new MyrmexAIFindGaurdingEntity(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, (entity, serverLevel) ->
            entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexSoldier.this, entity) && DragonUtils.isAlive(entity) && !(entity instanceof Enemy)));
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
            //HEALTH
            .add(Attributes.MAX_HEALTH, 40)
            //SPEED
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            //ATTACK
            .add(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 2D)
            //FOLLOW RANGE
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            //ARMOR
            .add(Attributes.ARMOR, 6.0D);
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
        return 0.8F;
    }

    @Override
    public int getCasteImportance() {
        return 1;
    }

    @Override
    public boolean shouldLeaveHive() {
        return false;
    }

    @Override
    public boolean shouldEnterHive() {
        return guardingEntity == null || !guardingEntity.canSeeSky() || guardingEntity.shouldEnterHive();
    }

    @Override
    public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
            float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            this.setLastHurtMob(entityIn);
            boolean flag = entityIn.hurtOrSimulate(this.damageSources().mobAttack(this), f);
            if (this.getAnimation() == ANIMATION_STING && flag) {
                this.playStingSound();
                if (entityIn instanceof LivingEntity) {
                    ((LivingEntity) entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
                    this.setTarget((LivingEntity) entityIn);
                }
            } else {
                this.playBiteSound();
            }
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
    public boolean needsGaurding() {
        return false;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING};
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
        return this.getLevel().isClientSide();
    }
}
