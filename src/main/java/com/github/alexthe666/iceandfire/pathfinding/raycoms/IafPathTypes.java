package com.github.alexthe666.iceandfire.pathfinding.raycoms;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

/**
 * 26.1 keeps {@link WalkNodeEvaluator#getPathTypeFromState} protected.
 * A subclass exposes the same vanilla types the 1.18 Forge path-type hook used.
 */
public final class IafPathTypes extends WalkNodeEvaluator {
    private IafPathTypes() {
    }

    public static PathType fromState(BlockGetter world, BlockPos pos) {
        return getPathTypeFromState(world, pos);
    }
}
