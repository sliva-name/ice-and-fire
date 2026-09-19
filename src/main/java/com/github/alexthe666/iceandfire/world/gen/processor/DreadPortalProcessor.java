package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DreadPortalProcessor extends StructureProcessor {

    private final float integrity = 1.0F;

    public DreadPortalProcessor(BlockPos position, StructurePlaceSettings settings, Biome biome) {
    }

    public static BlockState getRandomCrackedBlock(@Nullable BlockState prev, RandomSource random) {
        float rand = random.nextFloat();
        if (rand < 0.3) {
            return IafBlockRegistry.DREAD_STONE_BRICKS.get().defaultBlockState();
        } else if (rand < 0.6) {
            return IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.get().defaultBlockState();
        } else {
            return IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.get().defaultBlockState();
        }
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader worldReader, @NotNull BlockPos pos, @NotNull BlockPos pos2, StructureTemplate.@NotNull StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, @NotNull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        RandomSource random = settings.getRandom(infoIn2.pos());
        if (random.nextFloat() <= integrity) {
            if (infoIn2.state().getBlock() == Blocks.DIAMOND_BLOCK) {
                return new StructureTemplate.StructureBlockInfo(infoIn2.pos(), IafBlockRegistry.DREAD_PORTAL.get().defaultBlockState(), null);
            }
            if (infoIn2.state().getBlock() == IafBlockRegistry.DREAD_STONE_BRICKS.get()) {
                BlockState state = getRandomCrackedBlock(null, random);
                return new StructureTemplate.StructureBlockInfo(infoIn2.pos(), state, null);
            }
        }
        return infoIn2;
    }

    @Override
    protected @NotNull StructureProcessorType getType() {
        return StructureProcessorType.BLOCK_ROT;
    }
}
