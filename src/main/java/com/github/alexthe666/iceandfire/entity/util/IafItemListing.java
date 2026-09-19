package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.trading.MerchantOffer;

import javax.annotation.Nullable;

/**
 * 1.18 {@code VillagerTrades.ItemListing} is gone; 26.1 villager trades are data-driven
 * {@code VillagerTrade} objects. Myrmex still builds offers in code, so this keeps the
 * same getOffer(trader, random) contract.
 */
@FunctionalInterface
public interface IafItemListing {
    @Nullable
    MerchantOffer getOffer(Entity trader, RandomSource random);
}
