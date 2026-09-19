package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumParticles;
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

public class BlockDreadTorch extends TorchBlock implements IDreadBlock, IWallBlock {

    public BlockDreadTorch() {
        super(
            ParticleTypes.SMOKE,
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.WOOD)
                .lightLevel((state) -> 5)
                .sound(SoundType.STONE)
                .noOcclusion()
                .dynamicShape()
                .noCollision())
        );
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level worldIn, BlockPos pos, @NotNull RandomSource rand) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.6D;
        double d2 = (double) pos.getZ() + 0.5D;
        IceAndFire.PROXY.spawnParticle(EnumParticles.Dread_Torch, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public Block wallBlock() {
        return IafBlockRegistry.DREAD_TORCH_WALL.get();
    }
}
