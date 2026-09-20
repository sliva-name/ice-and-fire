package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public final class IafDimensions {
    public static final ResourceKey<Level> DREAD_LANDS = ResourceKey.create(
        Registries.DIMENSION, Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dread_lands"));
    public static final ResourceKey<DimensionType> DREAD_LANDS_TYPE = ResourceKey.create(
        Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dread_lands"));

    private IafDimensions() {
    }

    public static boolean isDreadLands(Level level) {
        return level != null && DREAD_LANDS.equals(level.dimension());
    }
}
