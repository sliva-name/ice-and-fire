package com.github.alexthe666.iceandfire.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockFallingGeneric extends FallingBlock {
    public Item itemBlock;

    public BlockFallingGeneric(MapColor color, float hardness, float resistance, SoundType sound) {
        super(
            IafBlockRegistry.id(BlockBehaviour.Properties
                .of().mapColor(color)
                .sound(sound)
                .strength(hardness, resistance))
        );
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends FallingBlock> codec() {
        return simpleCodec(properties -> new BlockFallingGeneric(MapColor.SAND, 0.5F, 0.5F, SoundType.SAND));
    }

    @SuppressWarnings("deprecation")
    public BlockFallingGeneric(MapColor color, float hardness, float resistance, SoundType sound, boolean slippery) {
        super(
            IafBlockRegistry.id(BlockBehaviour.Properties
                .of().mapColor(color)
                .sound(sound)
                .strength(hardness, resistance)
                .friction(0.98F))
        );
    }


    @Override
    public int getDustColor(BlockState blkst, BlockGetter level, BlockPos pos) {
        return -8356741;
    }
}
