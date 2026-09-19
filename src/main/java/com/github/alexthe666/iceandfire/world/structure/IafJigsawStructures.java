package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * 1.18 jigsaw start math: 5-block rotation offsets, min of four WORLD_SURFACE_WG
 * corners, then a type-specific Y bonus. Depths stay 2 / 3 / 5.
 */
public final class IafJigsawStructures {
    public static final ResourceKey<StructureTemplatePool> GRAVEYARD_POOL = ResourceKey.create(
        Registries.TEMPLATE_POOL, Identifier.fromNamespaceAndPath(IceAndFire.MODID, "graveyard/top_pool"));
    public static final ResourceKey<StructureTemplatePool> GORGON_POOL = ResourceKey.create(
        Registries.TEMPLATE_POOL, Identifier.fromNamespaceAndPath(IceAndFire.MODID, "gorgon_temple/top_pool"));
    public static final ResourceKey<StructureTemplatePool> DREAD_POOL = ResourceKey.create(
        Registries.TEMPLATE_POOL, Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dread_mausoleum/start_pool"));

    public static final Spec GRAVEYARD = new Spec(GRAVEYARD_POOL, 2, 1, false, () -> IafConfig.generateGraveyards);
    public static final Spec GORGON = new Spec(GORGON_POOL, 3, 2, false, () -> IafConfig.spawnGorgons);
    public static final Spec MAUSOLEUM = new Spec(DREAD_POOL, 5, 1, true, () -> IafConfig.generateMausoleums);

    private IafJigsawStructures() {
    }

    public record Spec(ResourceKey<StructureTemplatePool> pool, int depth, int yBonus, boolean requireValidBiome, BooleanSupplier enabled) {
    }

    public record Offset(int x, int z) {
    }

    public record StructureSetPlan(int spacing, int separation, int salt, int graveyardWeight, int mausoleumWeight, int gorgonWeight) {
    }

    public static Offset rotationOffsets(Rotation rotation) {
        int xOffset = 5;
        int zOffset = 5;
        if (rotation == Rotation.CLOCKWISE_90) {
            xOffset = -5;
        } else if (rotation == Rotation.CLOCKWISE_180) {
            xOffset = -5;
            zOffset = -5;
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            zOffset = -5;
        }
        return new Offset(xOffset, zOffset);
    }

    public static int startY(int y1, int y2, int y3, int y4, int yBonus) {
        return Math.min(Math.min(y1, y2), Math.min(y3, y4)) + yBonus;
    }

    public static StructureSetPlan structureSetPlan(int spawnGorgonsChance, int generateMausoleumChance, int generateGraveyardChance) {
        int graveyardWeight = generateGraveyardChance * 3;
        int average = (int) Math.ceil((spawnGorgonsChance + generateMausoleumChance + graveyardWeight) / 3.0D);
        return new StructureSetPlan(
            Math.max(average, 2),
            Math.max(average / 2, 1),
            342226450,
            graveyardWeight,
            generateMausoleumChance,
            spawnGorgonsChance
        );
    }

    public static Optional<Structure.GenerationStub> generate(Structure.GenerationContext context, Spec spec) {
        if (!spec.enabled().getAsBoolean()) {
            return Optional.empty();
        }
        ChunkPos pos = context.chunkPos();
        int x = pos.getMiddleBlockX();
        int z = pos.getMiddleBlockZ();
        int yTop = context.chunkGenerator().getFirstOccupiedHeight(
            x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        if (spec.requireValidBiome()) {
            Holder<Biome> biome = context.chunkGenerator().getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(yTop), QuartPos.fromBlock(z), context.randomState().sampler());
            if (!context.validBiome().test(biome)) {
                return Optional.empty();
            }
        }
        Rotation rotation = Rotation.getRandom(context.random());
        Offset offset = rotationOffsets(rotation);
        int y1 = yTop;
        int y2 = context.chunkGenerator().getFirstOccupiedHeight(
            x, z + offset.z(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int y3 = context.chunkGenerator().getFirstOccupiedHeight(
            x + offset.x(), z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int y4 = context.chunkGenerator().getFirstOccupiedHeight(
            x + offset.x(), z + offset.z(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        BlockPos blockpos = pos.getMiddleBlockPosition(startY(y1, y2, y3, y4, spec.yBonus()));
        Optional<Holder.Reference<StructureTemplatePool>> pool = context.registryAccess()
            .lookupOrThrow(Registries.TEMPLATE_POOL)
            .get(spec.pool());
        if (pool.isEmpty()) {
            return Optional.empty();
        }
        return JigsawPlacement.addPieces(
            context,
            pool.get(),
            Optional.empty(),
            spec.depth(),
            blockpos,
            false,
            Optional.empty(),
            new JigsawStructure.MaxDistance(80),
            PoolAliasLookup.EMPTY,
            DimensionPadding.ZERO,
            LiquidSettings.APPLY_WATERLOGGING
        );
    }
}
