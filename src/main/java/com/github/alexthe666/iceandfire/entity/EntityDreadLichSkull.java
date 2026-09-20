package com.github.alexthe666.iceandfire.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.packets.SpawnEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityDreadLichSkull extends AbstractArrow {

    private static final int LIFETIME_TICKS = 100;
    private double iafBaseDamage = 6.0D;

    public EntityDreadLichSkull(EntityType<? extends AbstractArrow> type, Level worldIn) {
        super(type, worldIn);
        this.setBaseDamage(6F);
    }

    public EntityDreadLichSkull(EntityType<? extends AbstractArrow> type, Level worldIn, double x, double y,
                                double z) {
        this(type, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(6F);
    }

    public EntityDreadLichSkull(EntityType<? extends AbstractArrow> type, Level worldIn, LivingEntity shooter,
                                double x, double y, double z) {
        super(type, shooter, worldIn, ItemStack.EMPTY, IafArrows.firedFrom(null));
        this.setBaseDamage(6);
        this.iafBaseDamage = 6;
    }

    public EntityDreadLichSkull(EntityType<? extends AbstractArrow> type, Level worldIn, LivingEntity shooter,
                                double dmg) {
        super(type, shooter, worldIn, ItemStack.EMPTY, IafArrows.firedFrom(null));
        this.setBaseDamage(dmg);
        this.iafBaseDamage = dmg;
    }

    public EntityDreadLichSkull(SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.DREAD_LICH_SKULL.get(), worldIn);
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    private void vanish() {
        if (!this.isRemoved()) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void tick() {
        if (this.tickCount > LIFETIME_TICKS) {
            this.vanish();
            return;
        }
        float sqrt = Mth.sqrt((float) (this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z));
        Entity shootingEntity = this.getOwner();
        if (shootingEntity != null && shootingEntity instanceof Mob && ((Mob) shootingEntity).getTarget() != null) {
            LivingEntity target = ((Mob) shootingEntity).getTarget();
            double minusX = target.getX() - this.getX();
            double minusY = target.getY() - this.getY();
            double minusZ = target.getZ() - this.getZ();
            double speed = 0.15D;
            this.setDeltaMovement(this.getDeltaMovement().add(minusX * speed * 0.1D, minusY * speed * 0.1D, minusZ * speed * 0.1D));
        }
        if (shootingEntity instanceof Player) {
            LivingEntity target = ((Player) shootingEntity).getKillCredit();
            if (target == null || !target.isAlive()) {
                double d0 = 10;
                List<Entity> list = this.level().getEntities(shootingEntity, (new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0D, this.getY() + 1.0D, this.getZ() + 1.0D)).inflate(d0, 10.0D, d0), EntitySelector.ENTITY_STILL_ALIVE);
                LivingEntity closest = null;
                if (!list.isEmpty()) {
                    for (Entity e : list) {
                        if (e instanceof LivingEntity && !e.getUUID().equals(shootingEntity.getUUID()) && e instanceof Enemy) {
                            if (closest == null || closest.distanceTo(shootingEntity) > e.distanceTo(shootingEntity)) {
                                closest = (LivingEntity) e;
                            }
                        }
                    }
                }
                target = closest;
            }
            if (target != null && target.isAlive()) {
                double minusX = target.getX() - this.getX();
                double minusY = target.getY() + target.getEyeHeight() - this.getY();
                double minusZ = target.getZ() - this.getZ();
                double speed = 0.25D * Math.min(this.distanceTo(target), 10D) / 10D;
                this.setDeltaMovement(this.getDeltaMovement().add((Math.signum(minusX) * 0.5D - this.getDeltaMovement().x) * 0.10000000149011612D, (Math.signum(minusY) * 0.5D - this.getDeltaMovement().y) * 0.10000000149011612D, (Math.signum(minusZ) * 0.5D - this.getDeltaMovement().z) * 0.10000000149011612D));
                this.setYRot((float) (Mth.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z) * (180D / Math.PI)));
                this.setXRot((float) (Mth.atan2(this.getDeltaMovement().y, sqrt) * (180D / Math.PI)));
            }
        }
        double d0 = 0;
        double d1 = 0.01D;
        double d2 = 0D;
        double x = this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth();
        double y = this.getY() + this.random.nextFloat() * this.getBbHeight() - this.getBbHeight();
        double z = this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth();
        float f = (this.getBbWidth() + this.getBbHeight() + this.getBbWidth()) * 0.333F + 0.5F;
        if (particleDistSq(x, y, z) < f * f) {
            IceAndFire.PROXY.spawnParticle(EnumParticles.Dread_Torch, x, y + 0.5D, z, d0, d1, d2);
        }
        super.tick();
        if (!this.isRemoved() && this.tickCount > 2 && (this.horizontalCollision || this.verticalCollision || this.isInGround())) {
            this.vanish();
        }
    }

    public double particleDistSq(double toX, double toY, double toZ) {
        double d0 = getX() - toX;
        double d1 = getY() - toY;
        double d2 = getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Override
    public void playSound(@NotNull SoundEvent soundIn, float volume, float pitch) {
        if (!this.isSilent() && soundIn != SoundEvents.ARROW_HIT && soundIn != SoundEvents.ARROW_HIT_PLAYER) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), soundIn, this.getSoundSource(), volume, pitch);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        this.vanish();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytraceResultIn) {
        if (raytraceResultIn.getType() == HitResult.Type.ENTITY) {
            Entity entity = raytraceResultIn.getEntity();
            Entity shootingEntity = this.getOwner();
            if (entity != null && shootingEntity != null && entity.isAlliedTo(shootingEntity)) {
                return;
            }
        }
        super.onHitEntity(raytraceResultIn);
        this.vanish();
    }

    @Override
    protected void doPostHurtEffects(@NotNull LivingEntity living) {
        super.doPostHurtEffects(living);
        Entity shootingEntity = this.getOwner();
        if (living != null && (shootingEntity == null || !living.is(shootingEntity))) {
            if (living instanceof Player) {
                this.damageShield((Player) living, (float) this.iafBaseDamage);
            }
        }
    }

    protected void damageShield(Player player, float damage) {
        if (damage >= 3.0F && player.getUseItem().has(net.minecraft.core.component.DataComponents.BLOCKS_ATTACKS)) {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, player.getUsedItemHand());

            if (player.getUseItem().isEmpty()) {
                InteractionHand Hand = player.getUsedItemHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, Hand);

                if (Hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                player.stopUsingItem();
                this.playSound(SoundEvents.SHIELD_BREAK.value(), 0.8F, 0.8F + this.level().getRandom().nextFloat() * 0.4F);
            }
        }
    }

    public int getBrightnessForRender() {
        return 15728880;
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}
