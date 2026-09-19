package com.github.alexthe666.iceandfire.api;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class FoodUtils {

    public static int getFoodPoints(Entity entity) {
        int foodPoints = Math.round(entity.getBbWidth() * entity.getBbHeight() * 10);
        if (entity instanceof AgeableMob) {
            return foodPoints;
        }
        if (entity instanceof Player) {
            return 15;
        }
        return 0;
    }

    public static int getFoodPoints(ItemStack item, boolean meatOnly, boolean includeFish) {
        FoodProperties food = food(item);
        if (food == null) {
            return 0;
        }
        int points = food.nutrition() * 10;
        if (!meatOnly) {
            return points;
        }
        if (isMeat(item)) {
            return points;
        }
        if (includeFish && item.is(Items.COD)) {
            return points;
        }
        return 0;
    }

    /** 1.18 {@code Item#isEdible()} / non-null {@code getFoodProperties()}. */
    public static boolean isEdible(ItemStack stack) {
        return food(stack) != null;
    }

    /** 1.18 {@code FoodProperties#isMeat()} — same meat bucket as {@link ItemTags#MEAT}. */
    public static boolean isMeat(ItemStack stack) {
        return stack.is(ItemTags.MEAT);
    }

    public static boolean isSeeds(ItemStack stack) {
        return stack.is(Tags.Items.SEEDS);
    }

    private static FoodProperties food(ItemStack stack) {
        return stack == null || stack.isEmpty() ? null : stack.get(DataComponents.FOOD);
    }
}
