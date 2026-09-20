package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
        closeHatchDoors(level, box);
        BlockPos spawnPos = findGorgonSpawn(container);
        if (spawnPos == null || !pos.equals(ChunkPos.containing(spawnPos))) {
            return;
        }
        List<EntityGorgon> gorgons = collectGorgons(level, spawnPos);
        EntityGorgon keeper = null;
        for (EntityGorgon gorgon : gorgons) {
            if (keeper == null) {
                keeper = gorgon;
                gorgon.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, gorgon.getYRot(), 0.0F);
            } else {
                gorgon.remove(Entity.RemovalReason.DISCARDED);
            }
        }
        if (keeper != null) {
            return;
        }
        EntityGorgon gorgon = IafEntityRegistry.GORGON.get().create(level.getLevel(), EntitySpawnReason.STRUCTURE);
        if (gorgon == null) {
            return;
        }
        gorgon.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        gorgon.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.STRUCTURE, null);
        level.addFreshEntity(gorgon);
    }

    @Nullable
    private static BlockPos findGorgonSpawn(PiecesContainer container) {
        StructurePiece marker = null;
        StructurePiece lowest = null;
        for (StructurePiece piece : container.pieces()) {
            if (isGorgonMarker(piece)) {
                marker = piece;
                break;
            }
            BoundingBox bb = piece.getBoundingBox();
            if (lowest == null || bb.minY() < lowest.getBoundingBox().minY()) {
                lowest = piece;
            }
        }
        if (marker != null) {
            BoundingBox bb = marker.getBoundingBox();
            return new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        }
        if (lowest == null) {
            return null;
        }
        BoundingBox bb = lowest.getBoundingBox();
        Vec3i center = bb.getCenter();
        return new BlockPos(center.getX(), bb.minY() + 1, center.getZ());
    }

    private static boolean isGorgonMarker(StructurePiece piece) {
        BoundingBox bb = piece.getBoundingBox();
        int volume = (bb.maxX() - bb.minX() + 1) * (bb.maxY() - bb.minY() + 1) * (bb.maxZ() - bb.minZ() + 1);
        if (volume <= 4) {
            return true;
        }
        if (piece instanceof PoolElementStructurePiece pool) {
            return pool.getElement().toString().contains("gorgon_temple/gorgon");
        }
        return false;
    }

    private static List<EntityGorgon> collectGorgons(WorldGenLevel level, BlockPos spawnPos) {
        AABB area = new AABB(spawnPos).inflate(8.0D);
        List<EntityGorgon> gorgons = new ArrayList<>(level.getEntities(IafEntityRegistry.GORGON.get(), area, Entity::isAlive));
        ServerLevel server = level.getLevel();
        for (EntityGorgon gorgon : server.getEntities(IafEntityRegistry.GORGON.get(), area, Entity::isAlive)) {
            if (!gorgons.contains(gorgon)) {
                gorgons.add(gorgon);
            }
        }
        return gorgons;
    }

    private static void closeHatchDoors(WorldGenLevel level, BoundingBox box) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockState state = level.getBlockState(cursor.set(x, y, z));
                    if (state.getBlock() instanceof TrapDoorBlock && state.hasProperty(BlockStateProperties.OPEN) && state.getValue(BlockStateProperties.OPEN)) {
                        level.setBlock(cursor, state.setValue(BlockStateProperties.OPEN, false), 2);
                    }
                }
            }
        }
    }
}
