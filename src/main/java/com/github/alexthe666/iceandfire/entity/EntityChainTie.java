package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.props.ChainProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class EntityChainTie extends HangingEntity {

    public EntityChainTie(EntityType<? extends HangingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityChainTie(EntityType<? extends HangingEntity> type, Level worldIn, BlockPos hangingPositionIn) {
        super(type, worldIn, hangingPositionIn);
        this.setPos(hangingPositionIn.getX() + 0.5D, hangingPositionIn.getY(), hangingPositionIn.getZ() + 0.5D);
    }

    public static EntityChainTie createTie(Level worldIn, BlockPos fence) {
        EntityChainTie entityChainTie = new EntityChainTie(IafEntityRegistry.CHAIN_TIE.get(), worldIn, fence);
        worldIn.addFreshEntity(entityChainTie);
        entityChainTie.playPlacementSound();
        return entityChainTie;
    }

    @Nullable
    public static EntityChainTie getKnotForPosition(Level worldIn, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (EntityChainTie entityleashknot : worldIn.getEntitiesOfClass(EntityChainTie.class,
            new AABB(i - 1.0D, j - 1.0D, k - 1.0D, i + 1.0D, j + 1.0D, k + 1.0D))) {
            if (entityleashknot != null && entityleashknot.getPos() != null && entityleashknot.getPos().equals(pos)) {
                return entityleashknot;
            }
        }
        return null;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(Mth.floor(x) + 0.5D, Mth.floor(y) + 0.5D, Mth.floor(z) + 0.5D);
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setPosRaw(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
            this.pos.getZ() + 0.5D);
        this.setBoundingBox(calculateBoundingBox(this.pos, this.getDirection()));
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(@NotNull BlockPos pos, @NotNull net.minecraft.core.Direction direction) {
        double xSize = 0.3D;
        double ySize = 0.875D;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        return new AABB(x - xSize, y - 0.5, z - xSize, x + xSize, y + ySize - 0.5, z + xSize);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity() instanceof Player) {
            return super.hurtServer(level, source, amount);
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput compound) {
        BlockPos blockpos = this.getPos();
        compound.putInt("TileX", blockpos.getX());
        compound.putInt("TileY", blockpos.getY());
        compound.putInt("TileZ", blockpos.getZ());
    }

    @Override
    public void readAdditionalSaveData(ValueInput compound) {
        this.pos = new BlockPos(compound.getIntOr("TileX", 0), compound.getIntOr("TileY", 0), compound.getIntOr("TileZ", 0));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    public void dropItem(@NotNull ServerLevel level, @Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), 1.0F, 1.0F);
    }

    @Override
    public void remove(Entity.@NotNull RemovalReason removalReason) {
        super.remove(removalReason);
        double d0 = 30D;
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0, this.getX() + d0, this.getY() + d0, this.getZ() + d0));
        for (LivingEntity livingEntity : list) {
            if (ChainProperties.isChainedTo(livingEntity, this)) {
                ChainProperties.removeChain(livingEntity, this);
                ItemEntity entityitem = new ItemEntity(this.level(), this.getX(), this.getY() + 1, this.getZ(),
                    new ItemStack(IafItemRegistry.CHAIN.get()));
                entityitem.setDefaultPickUpDelay();
                this.level().addFreshEntity(entityitem);
            }
        }
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Vec3 hit) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            boolean flag = false;
            double d0 = 30D;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0, this.getX() + d0, this.getY() + d0, this.getZ() + d0));

            for (LivingEntity livingEntity : list) {
                if (ChainProperties.isChainedTo(livingEntity, player)) {
                    ChainProperties.removeChain(livingEntity, player);
                    ChainProperties.attachChain(livingEntity, this);
                    flag = true;
                }
            }

            if (!flag) {
                this.remove(RemovalReason.DISCARDED);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }
    }
    @Override
    public boolean survives() {
        return this.level().getBlockState(this.pos).getBlock() instanceof WallBlock;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), 1.0F, 1.0F);
    }
}
