package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.packets.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class EntitySeaSerpentArrow extends AbstractArrow {

    public EntitySeaSerpentArrow(EntityType<? extends AbstractArrow> t, Level worldIn) {
        super(t, worldIn);
        this.setBaseDamage(3F);
    }

    public EntitySeaSerpentArrow(EntityType<? extends AbstractArrow> t, Level worldIn, double x, double y,
                                 double z) {
        this(t, worldIn);
        this.setPos(x, y, z);
        this.setBaseDamage(3F);
    }

    public EntitySeaSerpentArrow(SpawnEntity spawnEntity, Level world) {
        this(IafEntityRegistry.SEA_SERPENT_ARROW.get(), world);
    }

    public EntitySeaSerpentArrow(EntityType t, Level worldIn, LivingEntity shooter) {
        this(t, worldIn, shooter, ItemStack.EMPTY);
    }

    public EntitySeaSerpentArrow(EntityType t, Level worldIn, LivingEntity shooter, ItemStack weapon) {
        super(t, shooter, worldIn, new ItemStack(IafItemRegistry.SEA_SERPENT_ARROW.get()), IafArrows.firedFrom(weapon));
        this.setBaseDamage(3F);
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
            this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + xRatio + this.random.nextFloat() * this.getBbWidth() * 1.0F - this.getBbWidth() - d0 * 10.0D, this.getY() + this.random.nextFloat() * this.getBbHeight() - d1 * 10.0D, this.getZ() + zRatio + this.random.nextFloat() * this.getBbWidth() * 1.0F - this.getBbWidth() - d2 * 10.0D, d0, d1, d2);
            this.level().addParticle(ParticleTypes.SPLASH, this.getX() + xRatio + this.random.nextFloat() * this.getBbWidth() * 1.0F - this.getBbWidth() - d0 * 10.0D, this.getY() + this.random.nextFloat() * this.getBbHeight() - d1 * 10.0D, this.getZ() + zRatio + this.random.nextFloat() * this.getBbWidth() * 1.0F - this.getBbWidth() - d2 * 10.0D, d0, d1, d2);

        }
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.SEA_SERPENT_ARROW.get());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return getPickupItem();
    }
}
