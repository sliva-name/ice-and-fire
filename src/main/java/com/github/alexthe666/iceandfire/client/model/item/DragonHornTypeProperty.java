package com.github.alexthe666.iceandfire.client.model.item;

import com.github.alexthe666.iceandfire.item.ItemDragonHorn;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * 26.1 replacement for the 1.18 {@code iceorfire} item property: {@code 0} empty,
 * {@code 0.25} fire, {@code 0.5} ice, {@code 0.75} lightning. Used by
 * {@code range_dispatch} in {@code assets/iceandfire/items/dragon_horn.json}.
 */
public record DragonHornTypeProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<DragonHornTypeProperty> MAP_CODEC = MapCodec.unit(new DragonHornTypeProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return ItemDragonHorn.getDragonType(stack) * 0.25F;
    }

    @Override
    public MapCodec<DragonHornTypeProperty> type() {
        return MAP_CODEC;
    }
}
