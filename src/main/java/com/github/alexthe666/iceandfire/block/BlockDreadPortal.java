package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
import com.github.alexthe666.iceandfire.world.DreadPortalShape;
import com.github.alexthe666.iceandfire.world.DreadPortalTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry.DREAD_PORTAL;

public class BlockDreadPortal extends BaseEntityBlock implements IDreadBlock, Portal {

    public BlockDreadPortal() {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.COLOR_BLACK)
                .noCollision()
                .noOcclusion()
                .dynamicShape()
                .strength(-1, 100000)
                .lightLevel(state -> 11)
                .randomTicks())
        );
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(properties -> new BlockDreadPortal());
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected @NotNull VoxelShape getEntityInsideCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Entity entity) {
        return state.getShape(level, pos);
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Entity entity, @NotNull InsideBlockEffectApplier applier, boolean unknown) {
        if (!entity.canUsePortal(false)) {
            return;
        }
        entity.setAsInsidePortal(this, pos);
    }

    @Override
    public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
        return entity instanceof ServerPlayer ? 40 : 0;
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(@NotNull ServerLevel currentLevel, @NotNull Entity entity, @NotNull BlockPos portalEntryPos) {
        return DreadPortalTeleporter.destination(currentLevel, entity, portalEntryPos);
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, @NotNull net.minecraft.util.RandomSource rand) {
        if (!this.canSurviveAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, false);
        }
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @Nullable net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        if (!this.canSurviveAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, false);
        }
    }

    public boolean canSurviveAt(Level world, BlockPos pos) {
        return DreadPortalShape.findFromInterior(world, pos) != null;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull net.minecraft.util.RandomSource rand) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof TileEntityDreadPortal) {
            for (int j = 0; j < 3; ++j) {
                double d0 = (float) pos.getX() + rand.nextFloat();
                double d1 = (float) pos.getY() + rand.nextFloat();
                double d2 = (float) pos.getZ() + rand.nextFloat();
                double d3 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                double d4 = ((double) rand.nextFloat()) * -0.25D;
                double d5 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                IceAndFire.PROXY.spawnParticle(EnumParticles.Dread_Portal, d0, d1, d2, d3, d4, d5);
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return createTickerHelper(entityType, DREAD_PORTAL.get(), TileEntityDreadPortal::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityDreadPortal(pos, state);
    }
}
