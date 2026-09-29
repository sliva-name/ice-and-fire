package com.github.alexthe666.iceandfire.event;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenUtils {
    /**
     * Features may only touch the chunk being generated plus one chunk around it. Roosts are wider
     * than that, so anything past this margin is skipped instead of reading unfinished terrain.
     */
    public static boolean nearGeneratingChunk(BlockPos origin, BlockPos pos) {
        int margin = 4;
        int minX = (origin.getX() >> 4 << 4) - 16 + margin;
        int minZ = (origin.getZ() >> 4 << 4) - 16 + margin;
        return pos.getX() >= minX && pos.getX() < minX + 48 - 2 * margin
            && pos.getZ() >= minZ && pos.getZ() < minZ + 48 - 2 * margin;
    }


    private static boolean canHeightSkipBlock(BlockPos pos, LevelAccessor world) {
        BlockState state = world.getBlockState(pos);
        return state.is(BlockTags.LOGS) || !state.getFluidState().isEmpty();
    }

    public static BlockPos degradeSurface(LevelAccessor world, BlockPos surface) {
        while ((!world.getBlockState(surface).canOcclude() || canHeightSkipBlock(surface, world)) && surface.getY() > 1) {
            surface = surface.below();
        }
        return surface;
    }
}
