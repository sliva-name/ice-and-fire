package com.github.alexthe666.iceandfire.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemGenericFood extends Item {
    private final int healAmount;
    private final float saturation;

    public ItemGenericFood(int amount, float saturation, boolean isWolfFood, boolean eatFast, boolean alwaysEdible) {
        super(IafItemRegistry.defaultBuilder().food(
            createFood(amount, saturation, alwaysEdible),
            createConsumable(eatFast, null)));
        this.healAmount = amount;
        this.saturation = saturation;
    }

    public ItemGenericFood(int amount, float saturation, boolean isWolfFood, boolean eatFast, boolean alwaysEdible, int stackSize) {
        super(IafItemRegistry.defaultBuilder().food(
            createFood(amount, saturation, alwaysEdible),
            createConsumable(eatFast, null)).stacksTo(stackSize));
        this.healAmount = amount;
        this.saturation = saturation;
    }

    public static FoodProperties createFood(int amount, float saturation, boolean alwaysEdible) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
            .nutrition(amount)
            .saturationModifier(saturation);
        if (alwaysEdible) {
            builder.alwaysEdible();
        }
        return builder.build();
    }

    /**
     * 26.1 moved eat duration and potion effects off {@link FoodProperties}
     * onto {@link Consumable}. Vanilla fast food (dried kelp) is 0.8s.
     */
    public static Consumable createConsumable(boolean eatFast, MobEffectInstance potion) {
        Consumable.Builder builder = Consumables.defaultFood();
        if (eatFast) {
            builder.consumeSeconds(0.8F);
        }
        if (potion != null) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(potion));
        }
        return builder.build();
    }

    /** @deprecated 26.1 dropped meat/fast/effect on {@link FoodProperties.Builder}. */
    @Deprecated
    public static FoodProperties createFood(int amount, float saturation, boolean isWolfFood, boolean eatFast, boolean alwaysEdible, MobEffectInstance potion) {
        return createFood(amount, saturation, alwaysEdible);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity LivingEntity) {
        this.onFoodEaten(stack, worldIn, LivingEntity);
        return super.finishUsingItem(stack, worldIn, LivingEntity);
    }

    public void onFoodEaten(ItemStack stack, Level worldIn, LivingEntity livingEntity) {
    }
}
