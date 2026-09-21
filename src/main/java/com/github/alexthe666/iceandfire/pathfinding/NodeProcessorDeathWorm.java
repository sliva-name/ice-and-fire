package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.Target;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class NodeProcessorDeathWorm extends NodeEvaluator {

    @Override
    public @NotNull Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public @NotNull Target getTarget(double x, double y, double z) {
        return this.getTargetNodeAt(x - 0.4, y + 0.5D, z - 0.4);
    }

    @Override
    public @NotNull PathType getPathTypeOfMob(@NotNull PathfindingContext context, int x, int y, int z, @NotNull Mob mob) {
        return this.getPathType(context, x, y, z);
    }

    @Override
    public @NotNull PathType getPathType(@NotNull PathfindingContext context, int x, int y, int z) {
        return classify(context.getBlockState(new BlockPos(x, y, z)), context.getBlockState(new BlockPos(x, y - 1, z)));
    }

    @Override
    public int getNeighbors(Node @NotNull [] neighbors, @NotNull Node node) {
        int i = 0;
        for (Direction direction : Direction.values()) {
            Node pathpoint = this.getSandNode(node.x + direction.getStepX(), node.y + direction.getStepY(), node.z + direction.getStepZ());
            if (pathpoint != null && !pathpoint.closed) {
                neighbors[i++] = pathpoint;
            }
        }
        return i;
    }

    @Nullable
    private Node getSandNode(int x, int y, int z) {
        PathType pathnodetype = this.isFree(x, y, z);
        return pathnodetype != PathType.BREACH && pathnodetype != PathType.WATER ? null : this.getNode(x, y, z);
    }

    private PathType isFree(int x, int y, int z) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean sand = false;
        boolean breach = false;
        for (int i = x; i < x + this.entityWidth; ++i) {
            for (int j = y; j < y + this.entityHeight; ++j) {
                for (int k = z; k < z + this.entityDepth; ++k) {
                    BlockState state = this.currentContext.getBlockState(cursor.set(i, j, k));
                    PathType type = classify(state, this.currentContext.getBlockState(new BlockPos(i, j - 1, k)));
                    if (type == PathType.BLOCKED) {
                        return PathType.BLOCKED;
                    }
                    if (type == PathType.WATER) {
                        sand = true;
                    } else if (type == PathType.BREACH) {
                        breach = true;
                    }
                }
            }
        }
        if (breach) {
            return PathType.BREACH;
        }
        return sand ? PathType.WATER : PathType.BLOCKED;
    }

    private static PathType classify(BlockState state, BlockState below) {
        if (isSand(state)) {
            return PathType.WATER;
        }
        if (state.isAir() && isSand(below)) {
            return PathType.BREACH;
        }
        return PathType.BLOCKED;
    }

    private static boolean isSand(BlockState state) {
        return state.is(BlockTags.SAND) || state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL);
    }
}
