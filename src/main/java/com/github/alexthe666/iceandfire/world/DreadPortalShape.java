package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class DreadPortalShape {
    public static final int MIN_WIDTH = 2;
    public static final int MIN_HEIGHT = 3;
    public static final int MAX_WIDTH = 21;
    public static final int MAX_HEIGHT = 21;

    private DreadPortalShape() {
    }

    public record Found(Direction.Axis axis, int minAlong, int minY, int fixed, int width, int height) {
        public BlockPos interior(int along, int y) {
            if (axis == Direction.Axis.X) {
                return new BlockPos(minAlong + along, minY + y, fixed);
            }
            return new BlockPos(fixed, minY + y, minAlong + along);
        }

        public void forEachInterior(Consumer<BlockPos> consumer) {
            for (int along = 0; along < width; along++) {
                for (int y = 0; y < height; y++) {
                    consumer.accept(interior(along, y));
                }
            }
        }

        public boolean isLit(LevelAccessor level) {
            for (int along = 0; along < width; along++) {
                for (int y = 0; y < height; y++) {
                    if (!level.getBlockState(interior(along, y)).is(IafBlockRegistry.DREAD_PORTAL.get())) {
                        return false;
                    }
                }
            }
            return true;
        }

        public void light(LevelAccessor level) {
            BlockState portal = IafBlockRegistry.DREAD_PORTAL.get().defaultBlockState();
            forEachInterior(pos -> level.setBlock(pos, portal, Block.UPDATE_CLIENTS));
        }
    }

    public static boolean isFrame(BlockState state) {
        Block block = state.getBlock();
        return block == IafBlockRegistry.DREAD_STONE.get()
            || block == IafBlockRegistry.DREAD_STONE_BRICKS.get()
            || block == IafBlockRegistry.DREAD_STONE_BRICKS_CHISELED.get()
            || block == IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.get()
            || block == IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.get()
            || block == IafBlockRegistry.DREAD_STONE_TILE.get()
            || block == IafBlockRegistry.DREAD_STONE_FACE.get()
            || block == IafBlockRegistry.DREADWOOD_PLANKS_LOCK.get();
    }

    public static boolean isKeyhole(BlockState state) {
        return state.is(IafBlockRegistry.DREADWOOD_PLANKS_LOCK.get());
    }

    public static boolean isInterior(BlockState state) {
        return state.isAir() || state.is(IafBlockRegistry.DREAD_PORTAL.get());
    }

    @Nullable
    public static Found findFromKeyhole(LevelAccessor level, BlockPos keyhole) {
        for (Direction dir : Direction.values()) {
            BlockPos inside = keyhole.relative(dir);
            if (!isInterior(level.getBlockState(inside))) {
                continue;
            }
            if (dir.getAxis() == Direction.Axis.Y) {
                Found x = measure(level, inside, Direction.Axis.X);
                if (isValid(level, x, keyhole)) {
                    return x;
                }
                Found z = measure(level, inside, Direction.Axis.Z);
                if (isValid(level, z, keyhole)) {
                    return z;
                }
            } else {
                Found found = measure(level, inside, dir.getAxis());
                if (isValid(level, found, keyhole)) {
                    return found;
                }
            }
        }
        return null;
    }

    @Nullable
    public static Found findFromInterior(LevelAccessor level, BlockPos interior) {
        if (!isInterior(level.getBlockState(interior))) {
            return null;
        }
        Found x = measure(level, interior, Direction.Axis.X);
        if (isValid(level, x, null)) {
            return x;
        }
        Found z = measure(level, interior, Direction.Axis.Z);
        if (isValid(level, z, null)) {
            return z;
        }
        return null;
    }

    private static boolean isValid(LevelAccessor level, @Nullable Found found, @Nullable BlockPos requiredKeyhole) {
        return found != null && checkFrame(level, found, requiredKeyhole);
    }

    @Nullable
    private static Found measure(LevelAccessor level, BlockPos start, Direction.Axis axis) {
        int fixed = axis == Direction.Axis.X ? start.getZ() : start.getX();
        int startAlong = axis == Direction.Axis.X ? start.getX() : start.getZ();
        int minAlong = startAlong;
        while (isInterior(get(level, axis, minAlong - 1, start.getY(), fixed))) {
            minAlong--;
            if (startAlong - minAlong > MAX_WIDTH) {
                return null;
            }
        }
        int maxAlong = startAlong;
        while (isInterior(get(level, axis, maxAlong + 1, start.getY(), fixed))) {
            maxAlong++;
            if (maxAlong - startAlong > MAX_WIDTH) {
                return null;
            }
        }
        int width = maxAlong - minAlong + 1;
        if (width < MIN_WIDTH || width > MAX_WIDTH) {
            return null;
        }
        int minY = start.getY();
        while (rowInterior(level, axis, minAlong, width, minY - 1, fixed)) {
            minY--;
            if (start.getY() - minY > MAX_HEIGHT) {
                return null;
            }
        }
        int maxY = start.getY();
        while (rowInterior(level, axis, minAlong, width, maxY + 1, fixed)) {
            maxY++;
            if (maxY - start.getY() > MAX_HEIGHT) {
                return null;
            }
        }
        int height = maxY - minY + 1;
        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
            return null;
        }
        for (int along = 0; along < width; along++) {
            for (int y = minY; y <= maxY; y++) {
                if (!isInterior(get(level, axis, minAlong + along, y, fixed))) {
                    return null;
                }
            }
        }
        return new Found(axis, minAlong, minY, fixed, width, height);
    }

    private static boolean rowInterior(LevelAccessor level, Direction.Axis axis, int minAlong, int width, int y, int fixed) {
        for (int i = 0; i < width; i++) {
            if (!isInterior(get(level, axis, minAlong + i, y, fixed))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkFrame(LevelAccessor level, Found found, @Nullable BlockPos requiredKeyhole) {
        boolean sawRequired = requiredKeyhole == null;
        for (int along = -1; along <= found.width; along++) {
            BlockPos bottom = pos(found.axis, found.minAlong + along, found.minY - 1, found.fixed);
            BlockPos top = pos(found.axis, found.minAlong + along, found.minY + found.height, found.fixed);
            if (!isFrame(level.getBlockState(bottom)) || !isFrame(level.getBlockState(top))) {
                return false;
            }
            sawRequired |= bottom.equals(requiredKeyhole) || top.equals(requiredKeyhole);
        }
        for (int y = 0; y < found.height; y++) {
            BlockPos left = pos(found.axis, found.minAlong - 1, found.minY + y, found.fixed);
            BlockPos right = pos(found.axis, found.minAlong + found.width, found.minY + y, found.fixed);
            if (!isFrame(level.getBlockState(left)) || !isFrame(level.getBlockState(right))) {
                return false;
            }
            sawRequired |= left.equals(requiredKeyhole) || right.equals(requiredKeyhole);
        }
        return sawRequired;
    }

    private static BlockState get(LevelAccessor level, Direction.Axis axis, int along, int y, int fixed) {
        return level.getBlockState(pos(axis, along, y, fixed));
    }

    private static BlockPos pos(Direction.Axis axis, int along, int y, int fixed) {
        return axis == Direction.Axis.X ? new BlockPos(along, y, fixed) : new BlockPos(fixed, y, along);
    }

    public static boolean canFitFrame(LevelAccessor level, BlockPos interiorBottomLeft, Direction.Axis axis, @Nullable ChunkPos chunk) {
        int minAlong = axis == Direction.Axis.X ? interiorBottomLeft.getX() : interiorBottomLeft.getZ();
        int minY = interiorBottomLeft.getY();
        int fixed = axis == Direction.Axis.X ? interiorBottomLeft.getZ() : interiorBottomLeft.getX();
        for (int along = -1; along <= MIN_WIDTH; along++) {
            for (int y = -1; y <= MIN_HEIGHT; y++) {
                BlockPos pos = pos(axis, minAlong + along, minY + y, fixed);
                if (chunk != null && !chunk.equals(ChunkPos.containing(pos))) {
                    return false;
                }
                boolean interior = along >= 0 && along < MIN_WIDTH && y >= 0 && y < MIN_HEIGHT;
                if (interior && !level.getBlockState(pos).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean placeUnlitFrame(LevelAccessor level, BlockPos interiorBottomLeft, Direction.Axis axis) {
        return placeFrame(level, interiorBottomLeft, axis, false, true);
    }

    @Nullable
    public static BlockPos placeLitFrame(LevelAccessor level, BlockPos interiorBottomLeft, Direction.Axis axis) {
        if (!placeFrame(level, interiorBottomLeft, axis, true, false)) {
            return null;
        }
        return interiorBottomLeft.immutable();
    }

    private static boolean placeFrame(LevelAccessor level, BlockPos interiorBottomLeft, Direction.Axis axis, boolean lit, boolean withKeyhole) {
        int minAlong = axis == Direction.Axis.X ? interiorBottomLeft.getX() : interiorBottomLeft.getZ();
        int minY = interiorBottomLeft.getY();
        int fixed = axis == Direction.Axis.X ? interiorBottomLeft.getZ() : interiorBottomLeft.getX();
        BlockState lock = IafBlockRegistry.DREADWOOD_PLANKS_LOCK.get().defaultBlockState();
        BlockState portal = IafBlockRegistry.DREAD_PORTAL.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        int seed = interiorBottomLeft.getX() * 31 + interiorBottomLeft.getZ();
        for (int along = -1; along <= MIN_WIDTH; along++) {
            for (int y = -1; y <= MIN_HEIGHT; y++) {
                BlockPos pos = pos(axis, minAlong + along, minY + y, fixed);
                boolean interior = along >= 0 && along < MIN_WIDTH && y >= 0 && y < MIN_HEIGHT;
                boolean keyhole = withKeyhole && along == MIN_WIDTH && y == 0;
                if (interior) {
                    level.setBlock(pos, lit ? portal : air, Block.UPDATE_CLIENTS);
                } else if (keyhole) {
                    level.setBlock(pos, lock, Block.UPDATE_CLIENTS);
                } else {
                    level.setBlock(pos, ruinedStone(seed + along * 17 + y * 13), Block.UPDATE_CLIENTS);
                }
            }
        }
        return true;
    }

    public static void decorateRuin(LevelAccessor level, BlockPos interiorBottomLeft, Direction.Axis axis, @Nullable ChunkPos chunk, RandomSource random) {
        int minAlong = axis == Direction.Axis.X ? interiorBottomLeft.getX() : interiorBottomLeft.getZ();
        int minY = interiorBottomLeft.getY();
        int fixed = axis == Direction.Axis.X ? interiorBottomLeft.getZ() : interiorBottomLeft.getX();
        int[][] offsets = {{-2, -1}, {MIN_WIDTH + 1, -1}, {-2, MIN_HEIGHT}, {MIN_WIDTH + 1, MIN_HEIGHT}, {-3, 0}, {MIN_WIDTH + 2, 1}, {0, -2}, {1, MIN_HEIGHT + 1}};
        for (int[] offset : offsets) {
            BlockPos pos = pos(axis, minAlong + offset[0], minY + offset[1], fixed);
            if (chunk != null && !chunk.equals(ChunkPos.containing(pos))) {
                continue;
            }
            if (level.getBlockState(pos).isAir()) {
                if (random.nextBoolean()) {
                    level.setBlock(pos, IafBlockRegistry.DRAGON_ICE_SPIKES.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                } else if (offset[1] <= 0) {
                    level.setBlock(pos, random.nextBoolean()
                        ? IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.get().defaultBlockState()
                        : IafBlockRegistry.FROZEN_COBBLESTONE.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }
        for (int along = -2; along <= MIN_WIDTH + 1; along++) {
            BlockPos floor = pos(axis, minAlong + along, minY - 1, fixed + (random.nextBoolean() ? 1 : -1));
            if (chunk != null && !chunk.equals(ChunkPos.containing(floor))) {
                continue;
            }
            if (level.getBlockState(floor).canOcclude()) {
                BlockPos above = floor.above();
                if (level.getBlockState(above).isAir() && random.nextInt(3) == 0) {
                    level.setBlock(above, IafBlockRegistry.DRAGON_ICE_SPIKES.get().defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private static BlockState ruinedStone(int seed) {
        return switch (Math.floorMod(seed, 5)) {
            case 0 -> IafBlockRegistry.DREAD_STONE_BRICKS.get().defaultBlockState();
            case 1 -> IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.get().defaultBlockState();
            case 2 -> IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.get().defaultBlockState();
            case 3 -> IafBlockRegistry.DREAD_STONE_TILE.get().defaultBlockState();
            default -> IafBlockRegistry.DREAD_STONE.get().defaultBlockState();
        };
    }
}
