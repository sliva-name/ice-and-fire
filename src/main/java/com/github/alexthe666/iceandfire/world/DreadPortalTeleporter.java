package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class DreadPortalTeleporter {
    private DreadPortalTeleporter() {
    }

    @Nullable
    public static TeleportTransition destination(ServerLevel current, Entity entity, BlockPos portalPos) {
        boolean fromDread = IafDimensions.isDreadLands(current);
        ResourceKey<Level> targetKey = fromDread ? Level.OVERWORLD : IafDimensions.DREAD_LANDS;
        ServerLevel target = current.getServer().getLevel(targetKey);
        if (target == null) {
            return null;
        }
        BlockPos landing = fromDread ? findReturn(current, target, portalPos) : findOrCreateExit(current, target, portalPos);
        if (landing == null) {
            return null;
        }
        return new TeleportTransition(
            target,
            Vec3.atBottomCenterOf(landing),
            Vec3.ZERO,
            entity.getYRot(),
            entity.getXRot(),
            TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
        );
    }

    private static BlockPos findReturn(ServerLevel dread, ServerLevel overworld, BlockPos portalPos) {
        BlockPos linked = readLinkedExit(dread, portalPos);
        if (linked != null) {
            return standingPos(overworld, linked);
        }
        BlockPos fallback = overworld.getLevelData().getRespawnData().pos();
        return standingPos(overworld, fallback);
    }

    private static BlockPos findOrCreateExit(ServerLevel overworld, ServerLevel dread, BlockPos portalPos) {
        DreadArena.Site site = DreadArena.ensure(dread);
        BlockPos exit = site.portal();
        linkPortals(overworld, portalPos, dread, exit);
        DreadLandsRulers.ensurePresent(dread, exit, site.built());
        return standingPos(dread, exit);
    }

    @Nullable
    private static BlockPos readLinkedExit(ServerLevel level, BlockPos portalPos) {
        BlockEntity blockEntity = level.getBlockEntity(portalPos);
        if (blockEntity instanceof TileEntityDreadPortal portal) {
            return portal.getExitPortal();
        }
        BlockEntity below = level.getBlockEntity(portalPos.below());
        if (below instanceof TileEntityDreadPortal portal) {
            return portal.getExitPortal();
        }
        BlockEntity above = level.getBlockEntity(portalPos.above());
        if (above instanceof TileEntityDreadPortal portal) {
            return portal.getExitPortal();
        }
        return null;
    }

    private static void linkPortals(ServerLevel fromLevel, BlockPos from, ServerLevel toLevel, BlockPos to) {
        setExit(fromLevel, from, toLevel.dimension(), to);
        setExit(toLevel, to, fromLevel.dimension(), from);
    }

    private static void setExit(ServerLevel level, BlockPos portal, ResourceKey<Level> destDim, BlockPos destPos) {
        DreadPortalShape.Found found = DreadPortalShape.findFromInterior(level, portal);
        if (found != null) {
            found.forEachInterior(pos -> {
                if (level.getBlockEntity(pos) instanceof TileEntityDreadPortal tile) {
                    tile.setExit(destDim, destPos);
                }
            });
            return;
        }
        for (BlockPos cursor : new BlockPos[]{portal, portal.above(), portal.below()}) {
            if (level.getBlockEntity(cursor) instanceof TileEntityDreadPortal tile) {
                tile.setExit(destDim, destPos);
            }
        }
    }

    private static boolean isPortal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(IafBlockRegistry.DREAD_PORTAL.get()) || level.getBlockState(pos.above()).is(IafBlockRegistry.DREAD_PORTAL.get());
    }

    private static BlockPos standingPos(Level level, BlockPos portal) {
        if (level.getBlockState(portal).is(IafBlockRegistry.DREAD_PORTAL.get()) && level.isEmptyBlock(portal.above())) {
            return portal;
        }
        if (level.getBlockState(portal.above()).is(IafBlockRegistry.DREAD_PORTAL.get())) {
            return portal.above();
        }
        return portal;
    }
}
