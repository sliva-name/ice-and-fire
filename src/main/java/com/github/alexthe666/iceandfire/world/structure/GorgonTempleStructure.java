package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GorgonTempleStructure extends Structure {
    public static final MapCodec<GorgonTempleStructure> CODEC = simpleCodec(GorgonTempleStructure::new);

    public GorgonTempleStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        return IafJigsawStructures.generate(context, IafJigsawStructures.GORGON);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafWorldRegistry.GORGON_TEMPLE.get();
    }

    @Override
    public void afterPlace(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos pos, @NotNull PiecesContainer container) {
        container.calculateBoundingBox();
    }
}
