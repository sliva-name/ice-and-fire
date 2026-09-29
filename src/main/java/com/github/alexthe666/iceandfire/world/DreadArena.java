package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * The Dread Queen's citadel: a walled dread-stone courtyard raised above the terrain, so the fight never
 * happens in a hole. The portal sits on a dais at the south end, the throne on the north end, and two
 * gates lead out over stairs that run down to the ground.
 *
 * Layout (+z is south), all offsets from the floor centre C, where C.y is the floor block:
 * <pre>
 *            throne dais (0,-23)
 *      *                         *
 *   gate W        courtyard        gate E
 *      *                         *
 *            portal dais (0,+25)
 * </pre>
 */
public final class DreadArena {
    public static final int RADIUS = 30;
    private static final int SKIRT = 36;
    private static final int PORTAL_Z = 25;
    private static final int THRONE_Z = -23;
    private static final int FLAGS = Block.UPDATE_CLIENTS;

    private DreadArena() {
    }

    public record Site(BlockPos center, boolean built) {
        public BlockPos portal() {
            return portalOf(center);
        }

        public BlockPos throne() {
            return throneOf(center);
        }
    }

    /** Bottom-left interior block of the lit frame, the block the player arrives in. */
    public static BlockPos portalOf(BlockPos center) {
        return center.offset(-1, 2, PORTAL_Z);
    }

    /** Where the queen stands: on the upper tier of the throne dais. */
    public static BlockPos throneOf(BlockPos center) {
        return center.offset(0, 3, THRONE_Z);
    }

    public static BlockPos centerFromPortal(BlockPos portal) {
        return portal.offset(1, -2, -PORTAL_Z);
    }

    /** Finds the citadel of this dimension, building it the first time a player comes. */
    public static Site ensure(ServerLevel dread) {
        DreadLandsData data = DreadLandsData.get(dread);
        BlockPos known = data == null ? null : data.getArena();
        if (known != null) {
            return new Site(known, false);
        }
        BlockPos center = chooseSite(dread);
        long start = System.currentTimeMillis();
        build(dread, center);
        if (data != null) {
            data.setArena(center);
        }
        IceAndFire.LOGGER.info("Built the Dread Queen's citadel at {} in {} ms", center, System.currentTimeMillis() - start);
        return new Site(center, true);
    }

    // ---------- site ----------

    private static int top(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
    }

    /**
     * Picks the flattest candidate and sets the floor near the top of the surrounding ground, so the
     * courtyard is filled up to and never dug down into a hill.
     */
    /** Heightmap reads on unloaded chunks answer with the world floor, so generate them first. */
    private static void preload(ServerLevel level, int x, int z, int reach) {
        for (int cx = (x - reach) >> 4; cx <= (x + reach) >> 4; cx++) {
            for (int cz = (z - reach) >> 4; cz <= (z + reach) >> 4; cz++) {
                level.getChunk(cx, cz);
            }
        }
    }

    private static BlockPos chooseSite(ServerLevel level) {
        int[][] candidates = {{0, 0}, {56, 0}, {-56, 0}, {0, 56}, {0, -56}};
        int probe = RADIUS + 16;
        BlockPos best = null;
        double bestScore = Double.MAX_VALUE;
        for (int[] c : candidates) {
            preload(level, c[0], c[1], probe);
            int[] heights = new int[600];
            int n = 0;
            for (int dx = -probe; dx <= probe; dx += 6) {
                for (int dz = -probe; dz <= probe; dz += 6) {
                    if (dx * dx + dz * dz <= probe * probe) {
                        heights[n++] = top(level, c[0] + dx, c[1] + dz);
                    }
                }
            }
            int[] sorted = Arrays.copyOf(heights, n);
            Arrays.sort(sorted);
            int floor = Math.max(level.getSeaLevel() + 3, sorted[n * 96 / 100] + 1);
            double fill = 0;
            double cut = 0;
            for (int h : sorted) {
                fill += Math.max(0, floor - h);
                cut += Math.max(0, h - floor);
            }
            double score = fill / n * 0.4 + 3.0 * cut / n + Math.abs(floor - 100) * 0.02;
            if (score < bestScore) {
                bestScore = score;
                best = new BlockPos(c[0], Math.min(floor, level.getMaxY() - 90), c[1]);
            }
        }
        return best;
    }

    // ---------- build ----------

    private static void build(ServerLevel level, BlockPos c) {
        preload(level, c.getX(), c.getZ(), RADIUS + SKIRT);
        RandomSource random = RandomSource.create(c.asLong());
        shapeGround(level, c, random);
        floor(level, c, random);
        wall(level, c, random);
        gates(level, c);
        pillars(level, c);
        portalDais(level, c);
        throneDais(level, c, random);
    }

    private static BlockState bricks() {
        return IafBlockRegistry.DREAD_STONE_BRICKS.get().defaultBlockState();
    }

    private static BlockState weathered(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 9) {
            return IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.get().defaultBlockState();
        }
        if (roll < 14) {
            return IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.get().defaultBlockState();
        }
        return bricks();
    }

    private static BlockState tile() {
        return IafBlockRegistry.DREAD_STONE_TILE.get().defaultBlockState();
    }

    private static BlockState chiseled() {
        return IafBlockRegistry.DREAD_STONE_BRICKS_CHISELED.get().defaultBlockState();
    }

    private static BlockState stone() {
        return IafBlockRegistry.DREAD_STONE.get().defaultBlockState();
    }

    private static BlockState stairs(Direction ascends) {
        return IafBlockRegistry.DREAD_STONE_BRICKS_STAIRS.get().defaultBlockState().setValue(StairBlock.FACING, ascends);
    }

    private static void set(ServerLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, FLAGS);
    }

    /** Raises the ground to the floor level and buries anything that stands in the way. */
    private static void shapeGround(ServerLevel level, BlockPos c, RandomSource random) {
        int reach = RADIUS + SKIRT;
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                double r = Math.hypot(dx, dz);
                if (r > reach) {
                    continue;
                }
                int x = c.getX() + dx;
                int z = c.getZ() + dz;
                int ground = top(level, x, z);
                int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                if (r <= RADIUS + 0.5) {
                    for (int y = ground + 1; y < c.getY(); y++) {
                        pos.set(x, y, z);
                        level.setBlock(pos, stone(), FLAGS);
                    }
                    for (int y = c.getY() + 1; y <= Math.max(surface, c.getY()) + 2; y++) {
                        pos.set(x, y, z);
                        if (!level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, air, FLAGS);
                        }
                    }
                    if (ground > c.getY()) {
                        for (int y = c.getY() + 1; y <= ground; y++) {
                            pos.set(x, y, z);
                            level.setBlock(pos, air, FLAGS);
                        }
                    }
                } else {
                    int target = c.getY() - (int) Math.ceil(r - RADIUS);
                    if (target <= ground) {
                        continue;
                    }
                    for (int y = ground + 1; y < target; y++) {
                        pos.set(x, y, z);
                        level.setBlock(pos, stone(), FLAGS);
                    }
                    set(level, x, target, z, random.nextInt(4) == 0 ? weathered(random) : stone());
                    for (int y = target + 1; y <= surface; y++) {
                        pos.set(x, y, z);
                        if (!level.getBlockState(pos).isAir()) {
                            level.setBlock(pos, air, FLAGS);
                        }
                    }
                    if (random.nextInt(60) == 0) {
                        set(level, x, target + 1, z, IafBlockRegistry.DRAGON_ICE_SPIKES.get().defaultBlockState());
                    }
                }
            }
        }
    }

    private static void floor(ServerLevel level, BlockPos c, RandomSource random) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                double r = Math.hypot(dx, dz);
                if (r > RADIUS + 0.5) {
                    continue;
                }
                set(level, c.getX() + dx, c.getY(), c.getZ() + dz, floorBlock(dx, dz, r, random));
            }
        }
    }

    private static BlockState floorBlock(int dx, int dz, double r, RandomSource random) {
        if (r < 3.5) {
            return (int) r == 2 ? stone() : chiseled();
        }
        int ring = (int) Math.round(r);
        if (ring == 8 || ring == 18 || ring == 27) {
            return tile();
        }
        double angle = ((Math.toDegrees(Math.atan2(dz, dx)) % 45.0) + 45.0) % 45.0;
        double arc = Math.toRadians(Math.min(angle, 45.0 - angle)) * r;
        if (arc < 0.75 && r > 4 && r < 27) {
            return tile();
        }
        return weathered(random);
    }

    private static void wall(ServerLevel level, BlockPos c, RandomSource random) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                double r = Math.hypot(dx, dz);
                if (r <= RADIUS - 2 + 0.5 || r > RADIUS + 0.5) {
                    continue;
                }
                if (Math.abs(dz) <= 5 && Math.abs(dx) > 20) {
                    continue;
                }
                int x = c.getX() + dx;
                int z = c.getZ() + dz;
                int height = 4 + (((int) Math.floor(Math.atan2(dz, dx) * 29.0 / 2.0)) & 1);
                for (int y = 1; y <= height; y++) {
                    set(level, x, c.getY() + y, z, y == height && height == 5 ? chiseled() : weathered(random));
                }
            }
        }
    }

    private static void gates(ServerLevel level, BlockPos c) {
        for (int side : new int[]{-1, 1}) {
            Direction ascends = side > 0 ? Direction.WEST : Direction.EAST;
            boolean emerged = false;
            for (int k = 1; k <= SKIRT; k++) {
                int x = c.getX() + side * (RADIUS + k);
                int y = c.getY() - k + 1;
                int ground = top(level, x, c.getZ());
                if (y > ground) {
                    emerged = true;
                } else if (emerged || k > 12) {
                    break;
                }
                for (int v = -4; v <= 4; v++) {
                    int z = c.getZ() + v;
                    int col = top(level, x, z);
                    for (int fy = Math.max(col + 1, y - 40); fy < y; fy++) {
                        set(level, x, fy, z, stone());
                    }
                    set(level, x, y, z, stairs(ascends));
                    for (int cy = y + 1; cy <= Math.max(col, y + 7); cy++) {
                        BlockPos above = new BlockPos(x, cy, z);
                        if (!level.getBlockState(above).isAir()) {
                            level.setBlock(above, Blocks.AIR.defaultBlockState(), FLAGS);
                        }
                    }
                }
            }
            for (int t = RADIUS - 2; t <= RADIUS; t++) {
                int x = c.getX() + side * t;
                for (int v = -6; v <= 6; v++) {
                    set(level, x, c.getY() + 7, c.getZ() + v, bricks());
                    set(level, x, c.getY() + 8, c.getZ() + v, Math.abs(v) <= 1 ? chiseled() : bricks());
                }
            }
            for (int v : new int[]{-6, 5}) {
                pillar(level, c.getX() + side * (RADIUS - 2) - (side > 0 ? 0 : 1), c.getY(), c.getZ() + v, 8);
            }
        }
    }

    /** A 2x2 column with a chiselled cap and a dread torch on top. */
    private static void pillar(ServerLevel level, int x, int floorY, int z, int height) {
        for (int dx = 0; dx < 2; dx++) {
            for (int dz = 0; dz < 2; dz++) {
                for (int y = 1; y <= height; y++) {
                    set(level, x + dx, floorY + y, z + dz, bricks());
                }
                set(level, x + dx, floorY + height + 1, z + dz, chiseled());
            }
        }
        set(level, x, floorY + height + 2, z, IafBlockRegistry.DREAD_TORCH.get().defaultBlockState());
        set(level, x + 1, floorY + height + 2, z + 1, IafBlockRegistry.DREAD_TORCH.get().defaultBlockState());
    }

    private static void pillars(ServerLevel level, BlockPos c) {
        for (int k = 0; k < 8; k++) {
            double angle = Math.toRadians(22.5 + 45.0 * k);
            int x = (int) Math.round(Math.cos(angle) * 19.0);
            int z = (int) Math.round(Math.sin(angle) * 19.0);
            if (Math.abs(z) > 15 && Math.abs(x) < 11) {
                continue;
            }
            pillar(level, c.getX() + x, c.getY(), c.getZ() + z, 8);
        }
        for (int k = 0; k < 4; k++) {
            double angle = Math.toRadians(45.0 + 90.0 * k);
            pillar(level, c.getX() + (int) Math.round(Math.cos(angle) * 11.0), c.getY(), c.getZ() + (int) Math.round(Math.sin(angle) * 11.0), 5);
        }
    }

    private static void portalDais(ServerLevel level, BlockPos c) {
        for (int dx = -7; dx <= 7; dx++) {
            for (int dz = PORTAL_Z - 6; dz <= PORTAL_Z + 2; dz++) {
                if (Math.hypot(dx, dz) > RADIUS - 2) {
                    continue;
                }
                set(level, c.getX() + dx, c.getY() + 1, c.getZ() + dz, dx == 0 || Math.abs(dx) == 7 || dz == PORTAL_Z - 6 ? tile() : bricks());
            }
        }
        for (int dx = -3; dx <= 3; dx++) {
            set(level, c.getX() + dx, c.getY() + 1, c.getZ() + PORTAL_Z - 7, stairs(Direction.SOUTH));
        }
        BlockPos portal = portalOf(c);
        DreadPortalShape.placeLitFrame(level, portal, Direction.Axis.X);
        for (int y = 2; y <= 7; y++) {
            set(level, portal.getX() - 3, c.getY() + y, portal.getZ(), bricks());
            set(level, portal.getX() + 4, c.getY() + y, portal.getZ(), bricks());
        }
        set(level, portal.getX() - 3, c.getY() + 8, portal.getZ(), IafBlockRegistry.DREAD_TORCH.get().defaultBlockState());
        set(level, portal.getX() + 4, c.getY() + 8, portal.getZ(), IafBlockRegistry.DREAD_TORCH.get().defaultBlockState());
    }

    private static void throneDais(ServerLevel level, BlockPos c, RandomSource random) {
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -12; dz <= 12; dz++) {
                double lower = Math.hypot(dx, dz);
                if (lower <= 10 && Math.hypot(dx, THRONE_Z + 2 + dz) <= RADIUS - 2) {
                    set(level, c.getX() + dx, c.getY() + 1, c.getZ() + THRONE_Z + 2 + dz, lower > 9 ? tile() : bricks());
                }
                if (lower <= 5.5 && Math.hypot(dx, THRONE_Z + dz) <= RADIUS - 2) {
                    set(level, c.getX() + dx, c.getY() + 2, c.getZ() + THRONE_Z + dz, lower > 4.5 ? chiseled() : tile());
                }
            }
        }
        for (int dx = -4; dx <= 4; dx++) {
            set(level, c.getX() + dx, c.getY() + 1, c.getZ() + THRONE_Z + 11, stairs(Direction.NORTH));
        }
        int backZ = c.getZ() - RADIUS + 3;
        for (int dx = -5; dx <= 5; dx++) {
            for (int y = 1; y <= 12; y++) {
                boolean face = dx == 0 && y == 8;
                set(level, c.getX() + dx, c.getY() + y, backZ, face
                    ? IafBlockRegistry.DREAD_STONE_FACE.get().defaultBlockState().setValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING, Direction.SOUTH)
                    : Math.abs(dx) == 5 || y == 12 ? chiseled() : weathered(random));
            }
        }
        for (int dx : new int[]{-8, 7}) {
            pillar(level, c.getX() + dx, c.getY() + 1, c.getZ() + THRONE_Z - 1, 9);
        }
    }
}
