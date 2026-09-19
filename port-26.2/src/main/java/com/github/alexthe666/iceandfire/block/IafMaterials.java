package com.github.alexthe666.iceandfire.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

/**
 * 1.18 {@code Material} replacement. Block construction uses {@code MapColor};
 * runtime checks use tags and native {@link BlockState} flags so the same
 * vanilla buckets still match.
 */
public final class IafMaterials {
    private IafMaterials() {
    }

    public static boolean isAir(BlockState state) {
        return state.isAir();
    }

    public static boolean isSolid(BlockState state) {
        return state.isSolid();
    }

    public static boolean isLiquid(BlockState state) {
        return state.liquid();
    }

    public static boolean blocksMotion(BlockState state) {
        return state.blocksMotion();
    }

    public static boolean isReplaceable(BlockState state) {
        return state.canBeReplaced();
    }

    public static boolean isWater(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER);
    }

    public static boolean isFire(BlockState state) {
        return state.is(BlockTags.FIRE);
    }

    /** 1.18 {@code Material.SAND}: sand, gravel, soul sand/soil, concrete powder. */
    public static boolean isSand(BlockState state) {
        return state.is(BlockTags.SAND)
            || state.is(Tags.Blocks.GRAVELS)
            || state.is(BlockTags.CONCRETE_POWDERS)
            || state.is(Blocks.SOUL_SAND)
            || state.is(Blocks.SOUL_SOIL);
    }

    /** 1.18 {@code Material.DIRT}: dirt-like, not grass_block / dirt path / sand. */
    public static boolean isDirt(BlockState state) {
        return state.is(BlockTags.DIRT) && !isGrass(state);
    }

    /** 1.18 {@code Material.GRASS}: grass blocks and dirt path. */
    public static boolean isGrass(BlockState state) {
        return state.is(BlockTags.GRASS_BLOCKS) || state.is(Blocks.DIRT_PATH);
    }

    /** 1.18 {@code Material.STONE}: stone, cobble, ores, sandstone, terracotta, nether/end stone. */
    public static boolean isStone(BlockState state) {
        return state.is(Tags.Blocks.STONES)
            || state.is(Tags.Blocks.COBBLESTONES)
            || state.is(Tags.Blocks.ORES)
            || state.is(Tags.Blocks.SANDSTONE_BLOCKS)
            || state.is(BlockTags.STONE_BRICKS)
            || state.is(BlockTags.TERRACOTTA)
            || state.is(Tags.Blocks.END_STONES)
            || state.is(Blocks.NETHERRACK)
            || state.is(Blocks.BASALT)
            || state.is(Blocks.SMOOTH_BASALT)
            || state.is(Blocks.BLACKSTONE)
            || state.is(Blocks.OBSIDIAN)
            || state.is(Blocks.CRYING_OBSIDIAN)
            || state.is(Blocks.BEDROCK);
    }

    /** 1.18 {@code Material.WOOD}: logs, planks and other axe-mineable non-leaves. */
    public static boolean isWood(BlockState state) {
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.BAMBOO_BLOCKS) || state.is(Blocks.BAMBOO)) {
            return false;
        }
        return state.is(BlockTags.LOGS)
            || state.is(BlockTags.PLANKS)
            || state.is(BlockTags.WOODEN_BUTTONS)
            || state.is(BlockTags.WOODEN_DOORS)
            || state.is(BlockTags.WOODEN_STAIRS)
            || state.is(BlockTags.WOODEN_SLABS)
            || state.is(BlockTags.WOODEN_FENCES)
            || state.is(BlockTags.WOODEN_TRAPDOORS)
            || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
            || state.is(BlockTags.FENCE_GATES)
            || state.is(BlockTags.CEILING_HANGING_SIGNS)
            || state.is(BlockTags.STANDING_SIGNS)
            || state.is(Tags.Blocks.CHESTS)
            || state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    public static boolean isLeaves(BlockState state) {
        return state.is(BlockTags.LEAVES);
    }

    /** 1.18 {@code Material.PLANT}: flowers, saplings, crops and other replaceable plants. */
    public static boolean isPlant(BlockState state) {
        return state.is(BlockTags.FLOWERS)
            || state.is(net.minecraft.tags.BlockItemTags.SAPLINGS.block())
            || state.is(BlockTags.CROPS)
            || (state.is(BlockTags.REPLACEABLE) && !state.isAir() && !isLiquid(state) && !isFire(state) && !isSnow(state));
    }

    public static boolean isSnow(BlockState state) {
        return state.is(BlockTags.SNOW);
    }

    /** 1.18 {@code Material.ICE}: ice that is not packed/blue. */
    public static boolean isIce(BlockState state) {
        return state.is(Blocks.ICE) || state.is(Blocks.FROSTED_ICE);
    }

    /** 1.18 {@code Material.ICE_SOLID}: packed and blue ice. */
    public static boolean isIceSolid(BlockState state) {
        return state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE);
    }
}
