package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.entity.EntityDreadMob;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockGeneric extends Block {
    public BlockGeneric(MapColor color, float hardness, float resistance, SoundType sound) {
        super(
            IafBlockRegistry.id(BlockBehaviour.Properties
                .of().mapColor(color)
                .sound(sound)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops())
        );
    }

    public BlockGeneric(MapColor color, float hardness, float resistance, SoundType sound, boolean slippery) {
        super(
            IafBlockRegistry.id(BlockBehaviour.Properties
                .of().mapColor(color)
                .sound(sound)
                .strength(hardness, resistance)
                .friction(0.98F))
        );
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(BlockState state) {
        return this != IafBlockRegistry.DRAGON_ICE.get();
    }

    @SuppressWarnings("deprecation")
    public boolean isFullCube(BlockState state) {
        return this != IafBlockRegistry.DRAGON_ICE.get();
    }

    @Deprecated
    public boolean canEntitySpawn(BlockState state, Entity entityIn) {
        return entityIn instanceof EntityDreadMob || !DragonUtils.isDreadBlock(state);
    }

}
