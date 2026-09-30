package com.github.alexthe666.iceandfire.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;

/**
 * Feature placement may only read and write the chunk being generated plus one chunk around it. Blocks outside that
 * zone are dropped by the region and every attempt is logged as an error, so large features clip themselves to it.
 */
public final class WorldGenSafety {
    private WorldGenSafety() {
    }

    /** True if a cube of {@code margin} blocks around {@code pos} stays inside the region's safe zone. */
    public static boolean isSafe(LevelAccessor level, BlockPos pos, int margin) {
        if (!(level instanceof WorldGenRegion region)) {
            return true;
        }
        return region.isWithinWriteZone(pos.offset(-margin, 0, -margin)) && region.isWithinWriteZone(pos.offset(margin, 0, margin));
    }
}
