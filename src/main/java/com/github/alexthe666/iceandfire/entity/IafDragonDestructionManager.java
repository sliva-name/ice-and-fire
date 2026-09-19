package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.block.IafMaterials;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.DragonFireDamageWorldEvent;
import com.github.alexthe666.iceandfire.block.*;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;
import com.github.alexthe666.iceandfire.entity.util.BlockLaunchExplosion;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;

// TODO: This can be refactored/simplified a lot more

public class IafDragonDestructionManager {

    public static void destroyAreaFire(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;
        DamageSource source = destroyer.getRidingPlayer() != null ?
            IafDamageRegistry.causeIndirectDragonFireDamage(destroyer, destroyer.getRidingPlayer()) :
            IafDamageRegistry.causeDragonFireDamage(destroyer);
        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageFire;

        if (stage <= 3) {
            BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (IafConfig.dragonGriefing != 2 && world.getRandom().nextBoolean()) {
                    fireAttackBlock(world, pos);
                }
            });
        } else {
            final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.getRandom().nextInt(1);
            final int k = radius + world.getRandom().nextInt(1);
            final int l = radius + world.getRandom().nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (center.distSqr(pos) <= ff) {
                    if (IafConfig.dragonGriefing != 2 && world.getRandom().nextFloat() > (float) center.distSqr(pos) / ff) {
                        fireAttackBlock(world, pos);
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 5 + stage * 5;
        world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
                (double) center.getX() - damageRadius,
                (double) center.getY() - damageRadius,
                (double) center.getZ() - damageRadius,
                (double) center.getX() + damageRadius,
                (double) center.getY() + damageRadius,
                (double) center.getZ() + damageRadius
            )
        ).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                livingEntity.hurt(source, stageDmg);
                livingEntity.igniteForSeconds(statusDuration);
            }
        });
    }

    public static void destroyAreaIce(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;
        DamageSource source = destroyer.getRidingPlayer() != null ?
            IafDamageRegistry.causeIndirectDragonIceDamage(destroyer, destroyer.getRidingPlayer()) :
            IafDamageRegistry.causeDragonIceDamage(destroyer);
        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageIce;

        if (stage <= 3) {
            BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (IafConfig.dragonGriefing != 2 && world.getRandom().nextBoolean()) {
                    iceAttackBlock(world, pos);
                }
            });
        } else {
            final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.getRandom().nextInt(1);
            final int k = radius + world.getRandom().nextInt(1);
            final int l = radius + world.getRandom().nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (center.distSqr(pos) <= ff) {
                    if (IafConfig.dragonGriefing != 2 && world.getRandom().nextFloat() > (float) center.distSqr(pos) / ff) {
                        iceAttackBlock(world, pos);
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 50 * stage;
        world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
                (double) center.getX() - damageRadius,
                (double) center.getY() - damageRadius,
                (double) center.getZ() - damageRadius,
                (double) center.getX() + damageRadius,
                (double) center.getY() + damageRadius,
                (double) center.getZ() + damageRadius
            )
        ).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                livingEntity.hurt(source, stageDmg);
                FrozenProperties.setFrozenFor(livingEntity, statusDuration);
            }
        });
    }

    public static void destroyAreaLightning(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;
        DamageSource source = destroyer.getRidingPlayer() != null ?
            IafDamageRegistry.causeIndirectDragonLightningDamage(destroyer, destroyer.getRidingPlayer()) :
            IafDamageRegistry.causeDragonLightningDamage(destroyer);
        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageLightning;

        if (stage <= 3) {
            BlockPos.betweenClosedStream(center.offset(-1, -1, -1), center.offset(1, 1, 1)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (IafConfig.dragonGriefing != 2 && world.getRandom().nextBoolean()) {
                    lightningAttackBlock(world, pos);
                }
            });
        } else {
            int radius = stage == 4 ? 2 : 3;
            int j = radius + world.getRandom().nextInt(1);
            int k = radius + world.getRandom().nextInt(1);
            int l = radius + world.getRandom().nextInt(1);
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                if (world.getBlockEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getBlockEntity(pos)).onHitWithFlame();
                    return;
                }
                if (center.distSqr(pos) <= ff) {
                    if (IafConfig.dragonGriefing != 2 && world.getRandom().nextFloat() > (float) center.distSqr(pos) / ff) {
                        lightningAttackBlock(world, pos);
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        world.getEntitiesOfClass(
            LivingEntity.class,
            new AABB(
                (double) center.getX() - damageRadius,
                (double) center.getY() - damageRadius,
                (double) center.getZ() - damageRadius,
                (double) center.getX() + damageRadius,
                (double) center.getY() + damageRadius,
                (double) center.getZ() + damageRadius
            )
        ).stream().forEach(livingEntity -> {
            if (!DragonUtils.onSameTeam(destroyer, livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                livingEntity.hurt(source, stageDmg);
                double d1 = destroyer.getX() - livingEntity.getX();
                double d0 = destroyer.getZ() - livingEntity.getZ();
                livingEntity.knockback(0.3F, d1, d0);
            }
        });
    }

    public static void destroyAreaFireCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;
            DamageSource source = destroyer.getRidingPlayer() != null ?
                IafDamageRegistry.causeIndirectDragonFireDamage(destroyer, destroyer.getRidingPlayer()) :
                IafDamageRegistry.causeDragonFireDamage(destroyer);
            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.getRandom().nextFloat() * 3 > center.distSqr(pos) &&
                        !(world.getBlockState(pos).getBlock() instanceof IDragonProof) &&
                        DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                    if (world.getRandom().nextBoolean()) {
                        fireAttackBlock(world, pos);
                    }
                });
            } else {
                final int radius = stage == 4 ? 2 : 3;
                j = radius + world.getRandom().nextInt(2);
                k = radius + world.getRandom().nextInt(2);
                l = radius + world.getRandom().nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;

                destroyBlocks(world, center, j, k, l, ff);

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (center.distSqr(pos) <= ff) {
                        fireAttackBlock(world, pos);
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 15;
            world.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                    (double) center.getX() - j,
                    (double) center.getY() - k,
                    (double) center.getZ() - l,
                    (double) center.getX() + j,
                    (double) center.getY() + k,
                    (double) center.getZ() + l
                )
            ).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                    livingEntity.hurt(source, stageDmg);
                    livingEntity.igniteForSeconds(statusDuration);
                }
            });
            if (IafConfig.explosiveDragonBreath)
                causeExplosion(world, center, destroyer, source, stage);
        }
    }

    public static void destroyAreaIceCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;
            DamageSource source = destroyer.getRidingPlayer() != null ?
                IafDamageRegistry.causeIndirectDragonIceDamage(destroyer, destroyer.getRidingPlayer()) :
                IafDamageRegistry.causeDragonIceDamage(destroyer);
            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (world.getRandom().nextFloat() * 3 > center.distSqr(pos) &&
                        !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                    if (world.getRandom().nextBoolean()) {
                        iceAttackBlock(world, pos);
                    }
                });
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.getRandom().nextInt(2);
                k = radius + world.getRandom().nextInt(2);
                l = radius + world.getRandom().nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;

                destroyBlocks(world, center, j, k, l, ff);

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (center.distSqr(pos) <= ff) {
                        iceAttackBlock(world, pos);
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 400;
            world.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                    (double) center.getX() - j,
                    (double) center.getY() - k,
                    (double) center.getZ() - l,
                    (double) center.getX() + j,
                    (double) center.getY() + k,
                    (double) center.getZ() + l
                )
            ).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                    livingEntity.hurt(source, stageDmg);
                    FrozenProperties.setFrozenFor(livingEntity, statusDuration);
                }
            });
            if (IafConfig.explosiveDragonBreath)
                causeExplosion(world, center, destroyer, source, stage);
        }
    }


    public static void destroyAreaLightningCharge(Level world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (DragonFireDamageWorldEvent.BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;
            DamageSource source = destroyer.getRidingPlayer() != null ?
                IafDamageRegistry.causeIndirectDragonLightningDamage(destroyer, destroyer.getRidingPlayer()) :
                IafDamageRegistry.causeDragonLightningDamage(destroyer);
            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (Math.pow(world.getRandom().nextFloat() * 7F, 2) > center.distSqr(pos)
                        && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                    if (Math.pow(world.getRandom().nextFloat() * 7F, 2) > center.distSqr(pos)
                        && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                        world.setBlockAndUpdate(pos, transformState);
                    }
                });
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.getRandom().nextInt(2);
                k = radius + world.getRandom().nextInt(2);
                l = radius + world.getRandom().nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;

                destroyBlocks(world, center, j, k, l, ff);

                j++;
                k++;
                l++;
                BlockPos.betweenClosedStream(center.offset(-j, -k, -l), center.offset(j, k, l)).forEach(pos -> {
                    if (center.distSqr(pos) <= ff) {
                        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                            BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                            world.setBlockAndUpdate(pos, transformState);
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            world.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                    (double) center.getX() - j,
                    (double) center.getY() - k,
                    (double) center.getZ() - l,
                    (double) center.getX() + j,
                    (double) center.getY() + k,
                    (double) center.getZ() + l
                )
            ).stream().forEach(livingEntity -> {
                if (!destroyer.isAlliedTo(livingEntity) && !destroyer.is(livingEntity) && destroyer.hasLineOfSight(livingEntity)) {
                    livingEntity.hurt(source, stageDmg);
                    double d1 = destroyer.getX() - livingEntity.getX();
                    double d0 = destroyer.getZ() - livingEntity.getZ();
                    livingEntity.knockback(0.9F, d1, d0);
                }
            });
            if (IafConfig.explosiveDragonBreath)
                causeExplosion(world, center, destroyer, source, stage);
        }
    }

    private static void causeExplosion(Level world, BlockPos center, EntityDragonBase destroyer, DamageSource source, int stage) {
        if (!(world instanceof net.minecraft.server.level.ServerLevel server)) {
            return;
        }
        Explosion.BlockInteraction mode = ForgeEventFactory.getMobGriefingEvent(server, destroyer) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP;
        BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, source, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2), mode);
        explosion.explode();
        explosion.finalizeExplosion(true);
    }


    private static void fireAttackBlock(Level world, BlockPos pos) {
        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
            BlockState transformState = transformBlockFire(world.getBlockState(pos));
            if (transformState.getBlock() != world.getBlockState(pos).getBlock())
                world.setBlockAndUpdate(pos, transformState);
            if (world.getRandom().nextBoolean() && IafMaterials.isSolid(transformState) &&
                world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude() &&
                world.getBlockState(pos).canOcclude() && DragonUtils.canDragonBreak(world.getBlockState(pos.above()).getBlock())) {
                world.setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
            }
        }
    }

    private static void iceAttackBlock(Level world, BlockPos pos) {
        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
            BlockState transformState = transformBlockIce(world.getBlockState(pos));
            if (transformState.getBlock() != world.getBlockState(pos).getBlock())
                world.setBlockAndUpdate(pos, transformState);
            if (world.getRandom().nextInt(9) == 0 && IafMaterials.isSolid(transformState) &&
                world.getFluidState(pos.above()).isEmpty() && !world.getBlockState(pos.above()).canOcclude() &&
                world.getBlockState(pos).canOcclude() && DragonUtils.canDragonBreak(world.getBlockState(pos.above()).getBlock())) {
                world.setBlockAndUpdate(pos.above(), IafBlockRegistry.DRAGON_ICE_SPIKES.get().defaultBlockState());
            }
        }
    }

    private static void lightningAttackBlock(Level world, BlockPos pos) {
        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
            BlockState transformState = transformBlockLightning(world.getBlockState(pos));
            if (transformState.getBlock() != world.getBlockState(pos).getBlock()) {
                world.setBlockAndUpdate(pos, transformState);
            }
        }
    }

    private static void destroyBlocks(Level world, BlockPos center, int x, int y, int z, double radius2) {
        BlockPos.betweenClosedStream(center.offset(-x, -y, -z), center.offset(x, y, z)).forEach(pos -> {
            if (center.distSqr(pos) <= radius2) {
                if (world.getRandom().nextFloat() * 3 > (float) center.distSqr(pos) / radius2 && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }
        });
    }

    public static BlockState transformBlockFire(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyBlock) {
            return IafBlockRegistry.CHARRED_GRASS.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isDirt(in) && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CHARRED_DIRT.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isSand(in) && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CHARRED_GRAVEL.get().defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (IafMaterials.isStone(in) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.CHARRED_COBBLESTONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isStone(in) && in.getBlock() != IafBlockRegistry.CHARRED_COBBLESTONE.get()) {
            return IafBlockRegistry.CHARRED_STONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.DIRT_PATH) {
            return IafBlockRegistry.CHARRED_DIRT_PATH.get().defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (IafMaterials.isWood(in)) {
            return IafBlockRegistry.ASH.get().defaultBlockState();
        } else if (IafMaterials.isLeaves(in) || IafMaterials.isPlant(in) || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

    public static BlockState transformBlockIce(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyBlock) {
            return IafBlockRegistry.FROZEN_GRASS.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isDirt(in) && in.getBlock() == Blocks.DIRT || IafMaterials.isSnow(in)) {
            return IafBlockRegistry.FROZEN_DIRT.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isSand(in) && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.FROZEN_GRAVEL.get().defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (IafMaterials.isSand(in) && in.getBlock() != Blocks.GRAVEL) {
            return in;
        } else if (IafMaterials.isStone(in) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.FROZEN_COBBLESTONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isStone(in) && in.getBlock() != IafBlockRegistry.FROZEN_COBBLESTONE.get()) {
            return IafBlockRegistry.FROZEN_STONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.DIRT_PATH) {
            return IafBlockRegistry.FROZEN_DIRT_PATH.get().defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (IafMaterials.isWood(in)) {
            return IafBlockRegistry.FROZEN_SPLINTERS.get().defaultBlockState();
        } else if (IafMaterials.isWater(in)) {
            return Blocks.ICE.defaultBlockState();
        } else if (IafMaterials.isLeaves(in) || IafMaterials.isPlant(in) || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

    public static BlockState transformBlockLightning(BlockState in) {
        if (in.getBlock() instanceof SpreadingSnowyBlock) {
            return IafBlockRegistry.CRACKLED_GRASS.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isDirt(in) && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CRACKLED_DIRT.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isSand(in) && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CRACKLED_GRAVEL.get().defaultBlockState().setValue(BlockFallingReturningState.REVERTS, true);
        } else if (IafMaterials.isStone(in) && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getDescriptionId().contains("cobblestone"))) {
            return IafBlockRegistry.CRACKLED_COBBLESTONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (IafMaterials.isStone(in) && in.getBlock() != IafBlockRegistry.CRACKLED_COBBLESTONE.get()) {
            return IafBlockRegistry.CRACKLED_STONE.get().defaultBlockState().setValue(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.DIRT_PATH) {
            return IafBlockRegistry.CRACKLED_DIRT_PATH.get().defaultBlockState().setValue(BlockCharedPath.REVERTS, true);
        } else if (IafMaterials.isWood(in)) {
            return IafBlockRegistry.ASH.get().defaultBlockState();
        } else if (IafMaterials.isLeaves(in) || IafMaterials.isPlant(in) || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.defaultBlockState();
        }
        return in;
    }

}
