package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.packets.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class EntityHydraArrow extends AbstractArrow {

    private double iafBaseDamage = 5.0D;

    public EntityHydraArrow(EntityType<? extends AbstractArrow> t, Level worldIn) {
        super(t, worldIn);
        this.setBaseDamage(5F);
    }

    public EntityHydraArrow(EntityType<? extends AbstractArrow> t, Level worldIn, double x, double y, double z) {
        this(t, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(5F);
    }

    public EntityHydraArrow(SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.HYDRA_ARROW.get(), worldIn);
    }

    public EntityHydraArrow(EntityType t, Level worldIn, LivingEntity shooter) {
        this(t, worldIn, shooter, ItemStack.EMPTY);
    }

    public EntityHydraArrow(EntityType t, Level worldIn, LivingEntity shooter, ItemStack weapon) {
        super(t, shooter, worldIn, new ItemStack(IafItemRegistry.HYDRA_ARROW.get()), IafArrows.firedFrom(weapon));
        this.setBaseDamage(5F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide() && !this.isInGround()) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            double xRatio = this.getDeltaMovement().x * this.getBbHeight();
            double zRatio = this.getDeltaMovement().z * this.getBbHeight();
            IceAndFire.PROXY.spawnParticle(EnumParticles.Hydra, this.getX() + xRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d0 * 10.0D, this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()) - d1 * 10.0D, this.getZ() + zRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d2 * 10.0D, 0.1D, 1.0D, 0.1D);
            IceAndFire.PROXY.spawnParticle(EnumParticles.Hydra, this.getX() + xRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d0 * 10.0D, this.getY() + (double) (this.random.nextFloat() * this.getBbHeight()) - d1 * 10.0D, this.getZ() + zRatio + (double) (this.random.nextFloat() * this.getBbWidth() * 1.0F) - (double) this.getBbWidth() - d2 * 10.0D, 0.1D, 1.0D, 0.1D);
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

    @Override
    protected void doPostHurtEffects(@NotNull LivingEntity living) {
        if (living instanceof Player) {
            this.damageShield((Player) living, (float) this.iafBaseDamage);
        }
        living.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        Entity shootingEntity = this.getOwner();
        if (shootingEntity instanceof LivingEntity) {
            ((LivingEntity) shootingEntity).heal((float) this.iafBaseDamage);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.HYDRA_ARROW.get());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return getPickupItem();
    }
}
