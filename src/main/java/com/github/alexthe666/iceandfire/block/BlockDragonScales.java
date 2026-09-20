package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BlockDragonScales extends Block implements IDragonProof, IBlockItemHoverText {
    EnumDragonEgg type;

    public BlockDragonScales(EnumDragonEgg type) {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.STONE)
                .dynamicShape()
                .strength(30F, 500)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops())
        );

        this.type = type;
    }


    @Override
    public void appendItemHoverText(@NotNull ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                    Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("dragon." + type.toString().toLowerCase()).withStyle(type.color));
    }
}
