package com.github.alexthe666.iceandfire.entity.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Death-worm / dragon breath: 1.18 vanilla explode sampling, then launch falling blocks.
 */
public class BlockLaunchExplosion implements Explosion {
    private final float size;
    private final Level world;
    private final double x;
    private final double y;
    private final double z;
    private final BlockInteraction mode;
    private final Mob exploder;
    private final List<BlockPos> affectedBlockPositions = Lists.newArrayList();
    private final Map<Player, Vec3> playerKnockbackMap = Maps.newHashMap();
    private final Vec3 position;

    public BlockLaunchExplosion(Level world, Mob entity, double x, double y, double z, float size) {
        this(world, entity, x, y, z, size, BlockInteraction.DESTROY);
    }

    public BlockLaunchExplosion(Level world, Mob entity, double x, double y, double z, float size, BlockInteraction mode) {
        this(world, entity, null, x, y, z, size, mode);
    }

    public BlockLaunchExplosion(Level world, Mob entity, DamageSource source, double x, double y, double z, float size, BlockInteraction mode) {
        this.world = world;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mode = mode == null ? BlockInteraction.DESTROY : mode;
        this.exploder = entity;
        this.position = new Vec3(x, y, z);
    }

    public void explode() {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        IafExplosion.collectBlocks(this, server, exploder, x, y, z, size, affectedBlockPositions);
        IafExplosion.hurtAndKnockback(this, server, exploder, x, y, z, size, affectedBlockPositions, playerKnockbackMap, false);
    }

    public void finalizeExplosion(boolean spawnParticles) {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        IafExplosion.playEffects(server, x, y, z, size, mode, spawnParticles);
        if (mode == BlockInteraction.KEEP) {
            return;
        }
        IafExplosion.shuffle(affectedBlockPositions, server.getRandom());
        Vec3 center = new Vec3(this.x, this.y, this.z);
        for (BlockPos blockpos : affectedBlockPositions) {
            BlockState blockstate = server.getBlockState(blockpos);
            if (blockstate.isAir()) {
                continue;
            }
            BlockPos start = blockpos.immutable();
            net.minecraft.util.profiling.Profiler.get().push("explosion_blocks");
            blockstate.onBlockExploded(server, blockpos, this);
            FallingBlockEntity falling = FallingBlockEntity.fall(server, start, blockstate);
            double d5 = falling.getX() - this.x;
            double d7 = falling.getEyeY() - this.y;
            double d9 = falling.getZ() - this.z;
            float f3 = this.size * 2.0F;
            double d12 = Math.sqrt(falling.distanceToSqr(center)) / f3;
            double d14 = IafExplosion.getSeenPercent(center, falling);
            double d11 = (1.0D - d12) * d14;
            falling.setDeltaMovement(falling.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
            net.minecraft.util.profiling.Profiler.get().pop();
        }
    }

    public List<BlockPos> getToBlow() {
        return affectedBlockPositions;
    }

    @Override
    public ServerLevel level() {
        return world instanceof ServerLevel server ? server : null;
    }

    @Override
    public BlockInteraction getBlockInteraction() {
        return mode;
    }

    @Override
    public @Nullable LivingEntity getIndirectSourceEntity() {
        return exploder;
    }

    @Override
    public @Nullable Entity getDirectSourceEntity() {
        return exploder;
    }

    @Override
    public float radius() {
        return size;
    }

    @Override
    public Vec3 center() {
        return position;
    }

    @Override
    public boolean canTriggerBlocks() {
        return false;
    }

    @Override
    public boolean shouldAffectBlocklikeEntities() {
        return mode.shouldAffectBlocklikeEntities();
    }
}
