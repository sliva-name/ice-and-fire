package com.github.alexthe666.iceandfire.recipe;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

/**
 * 1.18 AbstractProjectileDispenseBehavior: spawn via {@link #getProjectile} then shoot along facing.
 */
public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
    @Override
    public @NotNull ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        Level level = source.level();
        Position position = DispenserBlock.getDispensePosition(source);
        Direction direction = source.state().getValue(DispenserBlock.FACING);
        Projectile projectile = this.getProjectile(level, position, stack);
        projectile.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), this.getPower(), this.getUncertainty());
        level.addFreshEntity(projectile);
        stack.shrink(1);
        return stack;
    }

    protected abstract Projectile getProjectile(Level worldIn, Position position, ItemStack stackIn);

    protected float getUncertainty() {
        return 6.0F;
    }

    protected float getPower() {
        return 1.1F;
    }
}
