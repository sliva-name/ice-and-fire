package com.github.alexthe666.iceandfire.entity.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Troll smash: 1.18 16^3 / 0.225 rays, skip ItemEntity, DESTROY blocks and drop loot.
 */
public class BlockBreakExplosion implements Explosion {
    private final Level world;
    private final double x;
    private final double y;
    private final double z;
    private final Mob exploder;
    private final float size;
    private final List<BlockPos> affectedBlockPositions;
    private final Map<Player, Vec3> playerKnockbackMap;
    private final Vec3 position;
    private final BlockInteraction mode = BlockInteraction.DESTROY;

    public BlockBreakExplosion(Level world, Mob entity, double x, double y, double z, float size) {
        this.affectedBlockPositions = Lists.newArrayList();
        this.playerKnockbackMap = Maps.newHashMap();
        this.world = world;
        this.exploder = entity;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.position = new Vec3(x, y, z);
    }

    private static void handleExplosionDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> dropPositionArray, ItemStack stack, BlockPos pos) {
        int i = dropPositionArray.size();
        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = dropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, stack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, stack, 16);
                dropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }
        dropPositionArray.add(Pair.of(stack, pos));
    }

    public void explode() {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        IafExplosion.collectBlocks(this, server, exploder, x, y, z, size, affectedBlockPositions);
        IafExplosion.hurtAndKnockback(this, server, exploder, x, y, z, size, affectedBlockPositions, playerKnockbackMap, true);
    }

    public void finalizeExplosion(boolean spawnParticles) {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        IafExplosion.playEffects(server, x, y, z, size, mode, spawnParticles);
        if (mode == BlockInteraction.KEEP) {
            return;
        }
        ObjectArrayList<Pair<ItemStack, BlockPos>> drops = new ObjectArrayList<>();
        IafExplosion.shuffle(affectedBlockPositions, server.getRandom());
        for (BlockPos blockpos : affectedBlockPositions) {
            BlockState blockstate = server.getBlockState(blockpos);
            if (blockstate.isAir()) {
                continue;
            }
            BlockPos immutable = blockpos.immutable();
            net.minecraft.util.profiling.Profiler.get().push("explosion_blocks");
            if (blockstate.getBlock().canDropFromExplosion(blockstate, server, blockpos, this)) {
                BlockEntity tile = blockstate.hasBlockEntity() ? server.getBlockEntity(blockpos) : null;
                for (ItemStack stack : Block.getDrops(blockstate, server, blockpos, tile, exploder, ItemStack.EMPTY)) {
                    handleExplosionDrops(drops, stack, immutable);
                }
            }
            blockstate.onBlockExploded(server, blockpos, this);
            net.minecraft.util.profiling.Profiler.get().pop();
        }
        for (Pair<ItemStack, BlockPos> pair : drops) {
            Block.popResource(server, pair.getSecond(), pair.getFirst());
        }
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

    public @NotNull Map<Player, Vec3> getHitPlayers() {
        return this.playerKnockbackMap;
    }

    public void clearToBlow() {
        this.affectedBlockPositions.clear();
    }

    public @NotNull List<BlockPos> getToBlow() {
        return this.affectedBlockPositions;
    }

    public @NotNull Vec3 getPosition() {
        return this.position;
    }
}
