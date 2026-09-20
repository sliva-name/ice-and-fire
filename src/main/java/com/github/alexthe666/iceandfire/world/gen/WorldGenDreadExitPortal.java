package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.world.DreadPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorldGenDreadExitPortal {
    private static final int PLATFORM = 8;

    @Nullable
    public static BlockPos findNearbyPortal(ServerLevel level, BlockPos around, int radius) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = around.getX() - radius; x <= around.getX() + radius; x++) {
            for (int z = around.getZ() - radius; z <= around.getZ() + radius; z++) {
                for (int y = around.getY() - 8; y <= around.getY() + 8; y++) {
                    cursor.set(x, y, z);
                    if (level.getBlockState(cursor).is(IafBlockRegistry.DREAD_PORTAL.get())) {
                        return cursor.immutable();
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos place(ServerLevel level, BlockPos dest) {
        BlockState stone = IafBlockRegistry.DREAD_STONE.get().defaultBlockState();
        BlockPos origin = dest.immutable();
        for (int x = -PLATFORM; x <= PLATFORM; x++) {
            for (int z = -PLATFORM; z <= PLATFORM; z++) {
                BlockPos floor = origin.offset(x, -1, z);
                level.setBlock(floor, stone, Block.UPDATE_CLIENTS);
                for (int y = 0; y < 12; y++) {
                    BlockPos air = origin.offset(x, y, z);
                    if (!level.getBlockState(air).isAir() && !level.getBlockState(air).is(IafBlockRegistry.DREAD_PORTAL.get())) {
                        level.setBlock(air, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
        BlockPos portal = DreadPortalShape.placeLitFrame(level, origin, Direction.Axis.X);
        if (portal != null) {
            DreadPortalShape.decorateRuin(level, origin, Direction.Axis.X, null, level.getRandom());
        }
        return portal;
    }
}
