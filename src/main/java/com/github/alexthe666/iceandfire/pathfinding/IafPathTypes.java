package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.world.level.pathfinder.PathType;

/**
 * 1.18 {@code BlockPathTypes.getDanger()} returned a damaging type or null. 26.1 {@link PathType}
 * dropped that helper; this keeps the same "is this a damaging/dangerous node" set.
 */
public final class IafPathTypes {
    private IafPathTypes() {
    }

    public static boolean hasDanger(PathType pathType) {
        return pathType == PathType.FIRE
            || pathType == PathType.FIRE_IN_NEIGHBOR
            || pathType == PathType.DAMAGING
            || pathType == PathType.DAMAGING_IN_NEIGHBOR
            || pathType == PathType.LAVA;
    }
}
