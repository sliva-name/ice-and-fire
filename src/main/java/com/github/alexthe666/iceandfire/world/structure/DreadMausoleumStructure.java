package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.world.DreadPortalShape;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DreadMausoleumStructure extends Structure {
    public static final MapCodec<DreadMausoleumStructure> CODEC = simpleCodec(DreadMausoleumStructure::new);

    public DreadMausoleumStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        return IafJigsawStructures.generate(context, IafJigsawStructures.MAUSOLEUM);
    }

    @Override
    public @NotNull StructureType<?> type() {
        return IafWorldRegistry.MAUSOLEUM.get();
    }

    @Override
    public void afterPlace(@NotNull WorldGenLevel level, @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos pos, @NotNull PiecesContainer container) {
        BoundingBox structureBox = container.calculateBoundingBox();
        Vec3i center = structureBox.getCenter();
        if (!pos.equals(new ChunkPos(center.getX() >> 4, center.getZ() >> 4))) {
            return;
        }
        BlockPos spawnPos = findInteriorSpawn(level, new BlockPos(center), structureBox);
        if (spawnPos == null) {
            return;
        }
        placeUnlitPortalFrame(level, spawnPos, structureBox, pos, random);
    }

    private static void placeUnlitPortalFrame(WorldGenLevel level, BlockPos spawnPos, BoundingBox structureBox, ChunkPos chunk, RandomSource random) {
        int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}, {3, 0}, {0, 3}, {1, 1}, {-1, 1}};
        for (int[] offset : offsets) {
            BlockPos interior = spawnPos.offset(offset[0], 0, offset[1]);
            for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
                if (!fitsInBox(interior, axis, structureBox) || !DreadPortalShape.canFitFrame(level, interior, axis, chunk)) {
                    continue;
                }
                DreadPortalShape.placeUnlitFrame(level, interior, axis);
                DreadPortalShape.decorateRuin(level, interior, axis, chunk, random);
                return;
            }
        }
    }

    private static boolean fitsInBox(BlockPos interior, Direction.Axis axis, BoundingBox box) {
        int minAlong = axis == Direction.Axis.X ? interior.getX() : interior.getZ();
        int fixed = axis == Direction.Axis.X ? interior.getZ() : interior.getX();
        for (int along = -1; along <= DreadPortalShape.MIN_WIDTH; along++) {
            for (int y = -1; y <= DreadPortalShape.MIN_HEIGHT; y++) {
                BlockPos pos = axis == Direction.Axis.X
                    ? new BlockPos(minAlong + along, interior.getY() + y, fixed)
                    : new BlockPos(fixed, interior.getY() + y, minAlong + along);
                if (!box.isInside(pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Nullable
    private static BlockPos findInteriorSpawn(WorldGenLevel level, BlockPos center, BoundingBox structureBox) {
        BlockPos best = null;
        int bestScore = Integer.MAX_VALUE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int bottom = Math.max(structureBox.minY(), level.getMinY() + 1);
        for (int x = structureBox.minX(); x <= structureBox.maxX(); x++) {
            for (int z = structureBox.minZ(); z <= structureBox.maxZ(); z++) {
                for (int y = bottom; y < structureBox.maxY(); y++) {
                    cursor.set(x, y, z);
                    if (!isInteriorFloor(level, cursor, structureBox)) {
                        continue;
                    }
                    int score = y * 40 + Math.abs(x - center.getX()) + Math.abs(z - center.getZ());
                    if (score < bestScore) {
                        bestScore = score;
                        best = cursor.above().immutable();
                    }
                }
            }
        }
        return best;
    }

    private static boolean isInteriorFloor(WorldGenLevel level, BlockPos floor, BoundingBox box) {
        if (!level.getBlockState(floor).canOcclude() || !level.isEmptyBlock(floor.above()) || !level.isEmptyBlock(floor.above(2))) {
            return false;
        }
        int max = Math.min(box.maxY(), floor.getY() + 12);
        for (int y = floor.getY() + 3; y <= max; y++) {
            if (level.getBlockState(new BlockPos(floor.getX(), y, floor.getZ())).canOcclude()) {
                return true;
            }
        }
        return false;
    }
}
