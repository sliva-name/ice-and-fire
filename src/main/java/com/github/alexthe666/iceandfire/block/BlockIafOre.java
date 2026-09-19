package com.github.alexthe666.iceandfire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jspecify.annotations.Nullable;

public class BlockIafOre extends Block {
    public Item itemBlock;
    private final @Nullable IntProvider xpRange;

    public BlockIafOre(int toollevel, float hardness, float resistance) {
        this(toollevel, hardness, resistance, null);
    }

    public BlockIafOre(int toollevel, float hardness, float resistance, @Nullable IntProvider xpRange) {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.STONE)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops())
        );
        this.xpRange = xpRange;
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return this.xpRange != null && silkTouchLevel == 0 ? this.xpRange.sample(randomSource) : 0;
    }
}

