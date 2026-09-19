package com.github.alexthe666.iceandfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockBurntTorchWall extends WallTorchBlock implements IDreadBlock {

    public BlockBurntTorchWall() {
        super(
            ParticleTypes.SMOKE,
            IafBlockRegistry.id(Properties.of().mapColor(MapColor.WOOD)
                .lightLevel((state) -> 0)
                .sound(SoundType.WOOD).noOcclusion().dynamicShape()
                .overrideLootTable(Optional.of(com.github.alexthe666.iceandfire.entity.util.IafLoot.table("blocks/burnt_torch")))
                .noCollision())
        );
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
    }
}
