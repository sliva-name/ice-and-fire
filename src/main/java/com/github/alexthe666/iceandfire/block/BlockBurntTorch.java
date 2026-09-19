package com.github.alexthe666.iceandfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class BlockBurntTorch extends TorchBlock implements IDreadBlock, IWallBlock {

    public BlockBurntTorch() {
        super(
            ParticleTypes.SMOKE,
            IafBlockRegistry.id(Properties.of().mapColor(MapColor.WOOD)
                .lightLevel((state) -> 0)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .dynamicShape()
                .noCollision())
        );
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
    }

    @Override
    public Block wallBlock() {
        return IafBlockRegistry.BURNT_TORCH_WALL.get();
    }
}
