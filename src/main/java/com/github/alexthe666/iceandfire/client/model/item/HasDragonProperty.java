package com.github.alexthe666.iceandfire.client.model.item;

import com.github.alexthe666.iceandfire.item.ItemSummoningCrystal;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * 26.1 replacement for the 1.18 {@code has_dragon} summoning crystal item property.
 * Used by {@code condition} in {@code assets/iceandfire/items/summoning_crystal_*.json}.
 */
public record HasDragonProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<HasDragonProperty> MAP_CODEC = MapCodec.unit(new HasDragonProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
        return ItemSummoningCrystal.hasDragon(stack);
    }

    @Override
    public MapCodec<HasDragonProperty> type() {
        return MAP_CODEC;
    }
}
