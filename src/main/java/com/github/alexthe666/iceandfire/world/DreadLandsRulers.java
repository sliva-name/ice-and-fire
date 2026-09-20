package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.util.HomePosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public final class DreadLandsRulers {
    private static final int SEARCH_RANGE = 384;

    private DreadLandsRulers() {
    }

    public static void ensurePresent(ServerLevel dread, BlockPos portal) {
        ensurePresent(dread, portal, true);
    }

    public static void ensurePresent(ServerLevel dread, BlockPos portal, boolean allowSpawn) {
        if (!IafDimensions.isDreadLands(dread)) {
            return;
        }
        DreadLandsData data = DreadLandsData.get(dread);
        if (data == null) {
            return;
        }

        cullDuplicates(dread, data, portal);

        if (data.isDefeated()) {
            return;
        }
        if (claimLoadedRulers(dread, data, portal)) {
            return;
        }
        if (data.hasSpawned() || !allowSpawn) {
            if (!allowSpawn) {
                data.markSpawned();
            }
            return;
        }

        spawnPair(dread, portal, data);
    }

    public static void markQueenDefeated(ServerLevel dread) {
        DreadLandsData data = DreadLandsData.get(dread);
        if (data != null) {
            data.markDefeated();
        }
    }

    private static boolean claimLoadedRulers(ServerLevel dread, DreadLandsData data, BlockPos portal) {
        EntityDreadQueen queen = findQueen(dread, data, portal);
        EntityIceDragon dragon = findDragon(dread, data, portal);
        if (queen == null && dragon == null) {
            return false;
        }
        UUID queenId = queen != null ? queen.getUUID() : data.getQueenId();
        UUID dragonId = dragon != null ? dragon.getUUID() : data.getDragonId();
        if (queen != null && dragon == null && queen.getVehicle() instanceof EntityIceDragon mount && mount.isBlackFrost()) {
            dragonId = mount.getUUID();
            dragon = mount;
        }
        restrictHome(dragon, portal);
        remount(queen, dragon);
        data.setRulers(queenId, dragonId);
        return true;
    }

    private static void spawnPair(ServerLevel dread, BlockPos portal, DreadLandsData data) {
        EntityIceDragon frost = IafEntityRegistry.ICE_DRAGON.get().create(dread, EntitySpawnReason.EVENT);
        EntityDreadQueen queen = IafEntityRegistry.DREAD_QUEEN.get().create(dread, EntitySpawnReason.EVENT);
        if (frost == null || queen == null) {
            return;
        }
        if (!data.tryBeginSpawn()) {
            return;
        }

        BlockPos spawn = surfaceNear(dread, portal.offset(16, 0, 12));
        frost.snapTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
        frost.finalizeSpawn(dread, dread.getCurrentDifficultyAt(spawn), EntitySpawnReason.EVENT, null);
        frost.applyBlackFrost();
        restrictHome(frost, portal);
        dread.addFreshEntity(frost);

        queen.snapTo(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, 0.0F, 0.0F);
        queen.finalizeSpawn(dread, dread.getCurrentDifficultyAt(spawn), EntitySpawnReason.EVENT, null);
        queen.setPersistenceRequired();
        queen.setAnimation(com.github.alexthe666.citadel.animation.IAnimatedEntity.NO_ANIMATION);
        queen.invulnerableTime = 100;
        dread.addFreshEntity(queen);
        queen.startRiding(frost, true, true);
        data.setRulers(queen.getUUID(), frost.getUUID());
    }

    private static void cullDuplicates(ServerLevel dread, DreadLandsData data, BlockPos portal) {
        AABB area = searchBox(dread, portal);
        UUID keepQueen = data.getQueenId();
        UUID keepDragon = data.getDragonId();

        EntityDreadQueen chosenQueen = keepQueen != null && dread.getEntity(keepQueen) instanceof EntityDreadQueen living && living.isAlive()
            ? living : null;
        for (EntityDreadQueen queen : dread.getEntitiesOfClass(EntityDreadQueen.class, area, Entity::isAlive)) {
            if (chosenQueen == null) {
                chosenQueen = queen;
                continue;
            }
            if (queen != chosenQueen) {
                queen.discard();
            }
        }

        EntityIceDragon chosenDragon = keepDragon != null && dread.getEntity(keepDragon) instanceof EntityIceDragon living && living.isAlive() && living.isBlackFrost()
            ? living : null;
        for (EntityIceDragon dragon : dread.getEntitiesOfClass(EntityIceDragon.class, area, dragon -> dragon.isAlive() && dragon.isBlackFrost())) {
            if (chosenDragon == null) {
                chosenDragon = dragon;
                continue;
            }
            if (dragon != chosenDragon) {
                dragon.discard();
            }
        }
    }

    private static EntityDreadQueen findQueen(ServerLevel dread, DreadLandsData data, BlockPos portal) {
        if (data.getQueenId() != null && dread.getEntity(data.getQueenId()) instanceof EntityDreadQueen queen && queen.isAlive()) {
            return queen;
        }
        List<EntityDreadQueen> queens = dread.getEntitiesOfClass(EntityDreadQueen.class, searchBox(dread, portal), Entity::isAlive);
        return queens.isEmpty() ? null : queens.get(0);
    }

    private static EntityIceDragon findDragon(ServerLevel dread, DreadLandsData data, BlockPos portal) {
        if (data.getDragonId() != null && dread.getEntity(data.getDragonId()) instanceof EntityIceDragon dragon && dragon.isAlive() && dragon.isBlackFrost()) {
            return dragon;
        }
        List<EntityIceDragon> dragons = dread.getEntitiesOfClass(EntityIceDragon.class, searchBox(dread, portal),
            dragon -> dragon.isAlive() && dragon.isBlackFrost());
        return dragons.isEmpty() ? null : dragons.get(0);
    }

    private static void remount(EntityDreadQueen queen, EntityIceDragon dragon) {
        if (queen == null || dragon == null || !queen.isAlive() || !dragon.isAlive() || dragon.isModelDead()) {
            return;
        }
        if (queen.getVehicle() != dragon) {
            queen.startRiding(dragon, true, true);
        }
    }

    private static void restrictHome(EntityIceDragon dragon, BlockPos portal) {
        if (dragon != null) {
            dragon.homePos = new HomePosition(portal, dragon.level());
            dragon.hasHomePosition = true;
        }
    }

    private static BlockPos surfaceNear(ServerLevel level, BlockPos around) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, around.getX(), around.getZ());
        int min = level.getMinY() + 8;
        int max = level.getMaxY() - 8;
        if (y < min) {
            y = 72;
        }
        return new BlockPos(around.getX(), Math.min(max, y), around.getZ());
    }

    private static AABB searchBox(ServerLevel level, BlockPos portal) {
        AABB area = new AABB(portal).inflate(SEARCH_RANGE, 160.0D, SEARCH_RANGE);
        for (ServerPlayer player : level.players()) {
            area = area.minmax(player.getBoundingBox().inflate(128.0D));
        }
        return area;
    }
}
