package com.github.alexthe666.iceandfire.entity.util;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

/** Shared 1.18 explosion sampling used by the custom IAF explosions. */
final class IafExplosion {
    private IafExplosion() {
    }

    static void collectBlocks(Explosion explosion, ServerLevel world, Entity exploder,
                              double x, double y, double z, float size, List<BlockPos> dest) {
        Set<BlockPos> set = Sets.newHashSet();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = size * (0.7F + world.getRandom().nextFloat() * 0.6F);
                        double d4 = x;
                        double d6 = y;
                        double d8 = z;
                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = BlockPos.containing(d4, d6, d8);
                            BlockState blockstate = world.getBlockState(blockpos);
                            FluidState fluid = world.getFluidState(blockpos);
                            if (!blockstate.isAir() || !fluid.isEmpty()) {
                                float f2 = Math.max(blockstate.getExplosionResistance(world, blockpos, explosion),
                                    fluid.getExplosionResistance(world, blockpos, explosion));
                                if (exploder instanceof LivingEntity living) {
                                    f2 = living.getBlockExplosionResistance(explosion, world, blockpos, blockstate, fluid, f2);
                                }
                                f -= (f2 + 0.3F) * 0.3F;
                            }
                            if (f > 0.0F && (exploder == null || !(exploder instanceof LivingEntity living)
                                || living.shouldBlockExplode(explosion, world, blockpos, blockstate, f))) {
                                set.add(blockpos);
                            }
                            d4 += d0 * 0.3F;
                            d6 += d1 * 0.3F;
                            d8 += d2 * 0.3F;
                        }
                    }
                }
            }
        }
        dest.addAll(set);
    }

    static void shuffle(List<BlockPos> positions, net.minecraft.util.RandomSource random) {
        for (int i = positions.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            BlockPos swap = positions.get(i);
            positions.set(i, positions.get(j));
            positions.set(j, swap);
        }
    }

    static void hurtAndKnockback(Explosion explosion, ServerLevel world, Entity exploder,
                                 double x, double y, double z, float size, List<BlockPos> blocks,
                                 Map<Player, Vec3> playerKnockback, boolean skipItemEntities) {
        float f3 = size * 2.0F;
        int k1 = Mth.floor(x - (double) f3 - 1.0D);
        int l1 = Mth.floor(x + (double) f3 + 1.0D);
        int i2 = Mth.floor(y - (double) f3 - 1.0D);
        int i1 = Mth.floor(y + (double) f3 + 1.0D);
        int j2 = Mth.floor(z - (double) f3 - 1.0D);
        int j1 = Mth.floor(z + (double) f3 + 1.0D);
        List<Entity> list = world.getEntities(exploder, new AABB(k1, i2, j2, l1, i1, j1));
        ForgeEventFactory.onExplosionDetonate(world, explosion, blocks, list, f3);
        Vec3 center = new Vec3(x, y, z);
        for (Entity entity : list) {
            if (entity.ignoreExplosion(explosion) || (skipItemEntities && entity instanceof ItemEntity)) {
                continue;
            }
            double d12 = Math.sqrt(entity.distanceToSqr(center)) / f3;
            if (d12 <= 1.0D) {
                double d5 = entity.getX() - x;
                double d7 = entity.getEyeY() - y;
                double d9 = entity.getZ() - z;
                double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
                if (d13 != 0.0D) {
                    d5 = d5 / d13;
                    d7 = d7 / d13;
                    d9 = d9 / d13;
                    double d14 = getSeenPercent(center, entity);
                    double d10 = (1.0D - d12) * d14;
                    entity.hurt(world.damageSources().explosion(explosion),
                        (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f3 + 1.0D)));
                    entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d10, d7 * d10, d9 * d10));
                    if (entity instanceof Player player
                        && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        playerKnockback.put(player, new Vec3(d5 * d10, d7 * d10, d9 * d10));
                    }
                }
            }
        }
    }

    static void playEffects(ServerLevel world, double x, double y, double z, float size,
                            Explosion.BlockInteraction mode, boolean spawnParticles) {
        world.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F,
            (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);
        boolean flag = mode != Explosion.BlockInteraction.KEEP;
        if (spawnParticles) {
            if (!(size < 2.0F) && flag) {
                world.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            } else {
                world.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    static float getSeenPercent(Vec3 explosionPos, Entity entity) {
        AABB box = entity.getBoundingBox();
        double stepX = 1.0D / ((box.maxX - box.minX) * 2.0D + 1.0D);
        double stepY = 1.0D / ((box.maxY - box.minY) * 2.0D + 1.0D);
        double stepZ = 1.0D / ((box.maxZ - box.minZ) * 2.0D + 1.0D);
        if (stepX < 0 || stepY < 0 || stepZ < 0) {
            return 0;
        }
        int seen = 0;
        int samples = 0;
        for (double u = 0; u <= 1; u += stepX) {
            for (double v = 0; v <= 1; v += stepY) {
                for (double w = 0; w <= 1; w += stepZ) {
                    double sx = Mth.lerp(u, box.minX, box.maxX);
                    double sy = Mth.lerp(v, box.minY, box.maxY);
                    double sz = Mth.lerp(w, box.minZ, box.maxZ);
                    samples++;
                    if (entity.level().clip(new net.minecraft.world.level.ClipContext(
                        new Vec3(sx, sy, sz), explosionPos,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType()
                        == net.minecraft.world.phys.HitResult.Type.MISS) {
                        seen++;
                    }
                }
            }
        }
        return samples == 0 ? 0 : (float) seen / (float) samples;
    }
}
