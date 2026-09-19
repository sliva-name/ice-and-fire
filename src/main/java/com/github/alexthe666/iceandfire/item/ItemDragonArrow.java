package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonArrow;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemDragonArrow extends ArrowItem {
    public ItemDragonArrow() {
        super(IafItemRegistry.defaultBuilder());
    }

    @Override
    public @NotNull AbstractArrow createArrow(@NotNull Level worldIn, @NotNull ItemStack stack, @NotNull LivingEntity shooter, @NotNull ItemStack weapon) {
        return new EntityDragonArrow(IafEntityRegistry.DRAGON_ARROW.get(), shooter, worldIn, weapon);
    }

    @Override
    public boolean isInfinite(@NotNull ItemStack stack, @NotNull ItemStack bow, @NotNull LivingEntity player) {
        return com.github.alexthe666.iceandfire.entity.util.IafEnchantments.has(bow, player, net.minecraft.world.item.enchantment.Enchantments.INFINITY) && this.getClass() == ItemDragonArrow.class;
    }
}
