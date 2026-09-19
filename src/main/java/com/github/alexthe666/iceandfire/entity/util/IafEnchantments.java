package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 1.18 helpers such as {@code getDepthStrider} / {@code getRiptide} /
 * {@code hasVanishingCurse} resolve through registry holders in 26.1.
 */
public final class IafEnchantments {
    private IafEnchantments() {
    }

    public static int level(LivingEntity entity, ResourceKey<Enchantment> key) {
        return entity.registryAccess().lookup(Registries.ENCHANTMENT)
            .flatMap(reg -> reg.get(key))
            .map(holder -> EnchantmentHelper.getEnchantmentLevel(holder, entity))
            .orElse(0);
    }

    public static int level(ItemStack stack, LivingEntity context, ResourceKey<Enchantment> key) {
        return context.registryAccess().lookup(Registries.ENCHANTMENT)
            .flatMap(reg -> reg.get(key))
            .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack))
            .orElse(0);
    }

    public static boolean has(ItemStack stack, LivingEntity context, ResourceKey<Enchantment> key) {
        return level(stack, context, key) > 0;
    }
}
