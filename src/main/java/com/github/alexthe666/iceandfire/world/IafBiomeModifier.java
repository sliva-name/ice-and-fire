package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

/**
 * 26.1 injection point for the 1.18 {@link IafWorldRegistry#addFeatures}
 * and {@link IafEntityRegistry#addSpawners} biome tests.
 */
public final class IafBiomeModifier implements BiomeModifier {
    public static final IafBiomeModifier INSTANCE = new IafBiomeModifier();
    public static final MapCodec<IafBiomeModifier> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            IafWorldRegistry.addFeatures(biome, builder.getGenerationSettings());
            IafEntityRegistry.addSpawners(biome, builder.getMobSpawnSettings());
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
