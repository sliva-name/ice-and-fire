package com.github.alexthe666.iceandfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockIafOre extends Block {
    public Item itemBlock;

    public BlockIafOre(int toollevel, float hardness, float resistance) {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.STONE)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops())
		);
    }

}

