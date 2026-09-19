package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.Optional;

/**
 * 1.18 MerchantOffer took ItemStacks as costs. 26.1 costs are {@link ItemCost}.
 * Counts and results stay the same.
 */
public final class IafOffers {
    private IafOffers() {
    }

    public static MerchantOffer of(ItemStack costA, ItemStack result, int maxUses, int xp, float multiplier) {
        return new MerchantOffer(cost(costA), result, maxUses, xp, multiplier);
    }

    public static MerchantOffer of(ItemStack costA, ItemStack costB, ItemStack result, int maxUses, int xp, float multiplier) {
        return new MerchantOffer(cost(costA), Optional.of(cost(costB)), result, maxUses, xp, multiplier);
    }

    private static ItemCost cost(ItemStack stack) {
        return new ItemCost(stack.getItem(), stack.getCount());
    }
}
