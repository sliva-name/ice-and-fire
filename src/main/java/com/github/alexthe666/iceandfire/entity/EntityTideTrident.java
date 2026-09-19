package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityTideTrident extends ThrownTrident {

    private static final int ADDITIONALPIERCING = 2;
    private int entitiesHit = 0;

    public EntityTideTrident(EntityType<? extends ThrownTrident> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityTideTrident(Level worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(IafEntityRegistry.TIDE_TRIDENT.get(), worldIn);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1F, thrower.getZ());
        this.setOwner(thrower);
        this.setPickupItemStack(thrownStackIn);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(IafItemRegistry.TIDE_TRIDENT.get());
    }

    @Override
    protected EntityHitResult findHitEntity(Vec3 start, Vec3 end) {
        return entitiesHit >= getMaxPiercing() ? null : super.findHitEntity(start, end);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        float f = 12.0F;
        Entity owner = this.getOwner();
        DamageSource damagesource = this.damageSources().trident(this, owner == null ? this : owner);
        if (this.level() instanceof ServerLevel serverLevel) {
            f = EnchantmentHelper.modifyDamage(serverLevel, this.getWeaponItem(), entity, damagesource, f);
        }
        entitiesHit++;
        if (entity.hurtOrSimulate(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (this.level() instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSourceOnBreak(
                    serverLevel, entity, damagesource, this.getWeaponItem(), item -> {});
            }
            if (entity instanceof LivingEntity living) {
                this.doKnockback(living, damagesource);
                this.doPostHurtEffects(living);
            }
        }
        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
    }

    private int getMaxPiercing() {
        return ADDITIONALPIERCING + getPierceLevel();
    }
}
