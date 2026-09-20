package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.world.DreadPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockDreadWoodLock extends Block implements IDragonProof, IDreadBlock {
    public static final BooleanProperty PLAYER_PLACED = BooleanProperty.create("player_placed");

    public BlockDreadWoodLock() {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.WOOD)
                .strength(-1.0F, 1000000F)
                .sound(SoundType.WOOD))
        );
        this.registerDefaultState(this.getStateDefinition().any().setValue(PLAYER_PLACED, Boolean.FALSE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getDestroyProgress(BlockState state, @NotNull Player player, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
        if (state.getValue(PLAYER_PLACED)) {
            float f = 8f;
            return player.getDestroySpeed(state) / f / (float) 30;
        }
        return super.getDestroyProgress(state, player, worldIn, pos);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult resultIn) {
        if (stack.getItem() != IafItemRegistry.DREAD_KEY.get()) {
            return InteractionResult.PASS;
        }
        if (worldIn.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        DreadPortalShape.Found frame = DreadPortalShape.findFromKeyhole(worldIn, pos);
        if (frame != null) {
            if (!frame.isLit(worldIn)) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                frame.light(worldIn);
                worldIn.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("iceandfire.dread_portal.lit"));
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (hasNearbyWood(worldIn, pos)) {
            Block.popResource(worldIn, pos, new ItemStack(IafBlockRegistry.DREADWOOD_PLANKS_LOCK.get()));
            deleteNearbyWood(worldIn, pos, pos);
            worldIn.playSound(null, pos, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.BLOCKS, 1.0F, 1.0F);
            worldIn.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 2.0F);
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("iceandfire.dread_portal.incomplete"));
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean hasNearbyWood(Level worldIn, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            if (worldIn.getBlockState(pos.relative(facing)).is(IafBlockRegistry.DREADWOOD_PLANKS.get())) {
                return true;
            }
        }
        return false;
    }

    private void deleteNearbyWood(Level worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.distSqr(startPos) < 32) {
            BlockState state = worldIn.getBlockState(pos);
            if (state.is(IafBlockRegistry.DREADWOOD_PLANKS.get()) || state.is(IafBlockRegistry.DREADWOOD_PLANKS_LOCK.get())) {
                worldIn.destroyBlock(pos, false);
                for (Direction facing : Direction.values()) {
                    deleteNearbyWood(worldIn, pos.relative(facing), startPos);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PLAYER_PLACED, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLAYER_PLACED);
    }
}
