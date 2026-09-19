package com.github.alexthe666.iceandfire.block;

import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Random;

import static com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry.PIXIE_HOUSE;

public class BlockPixieHouse extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockPixieHouse() {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.WOOD)
                .noOcclusion()
                .dynamicShape()
                .strength(2.0F, 5.0F)
                .randomTicks())
		);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(properties -> new BlockPixieHouse());
    }

    static String name(String type) {
        return "pixie_house_%s".formatted(type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void affectNeighborsAfterRemoval(@NotNull BlockState state, @NotNull net.minecraft.server.level.ServerLevel worldIn, @NotNull BlockPos pos, boolean movedByPiston) {
        dropPixie(worldIn, pos);
        popResource(worldIn, pos, new ItemStack(this, 0));
        super.affectNeighborsAfterRemoval(state, worldIn, pos, movedByPiston);
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        this.checkFall(worldIn, pos);
    }

    private boolean checkFall(Level worldIn, BlockPos pos) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
            dropPixie(worldIn, pos);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    private boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        return true;
    }

    public void dropPixie(Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof TileEntityPixieHouse && ((TileEntityPixieHouse) world.getBlockEntity(pos)).hasPixie) {
            ((TileEntityPixieHouse) world.getBlockEntity(pos)).releasePixie();
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> entityType) {
        return level.isClientSide() ? createTickerHelper(entityType, PIXIE_HOUSE.get(), TileEntityPixieHouse::tick) : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPixieHouse(pos, state);
    }
}
