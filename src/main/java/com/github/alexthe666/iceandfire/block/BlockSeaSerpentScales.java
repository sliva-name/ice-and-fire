package com.github.alexthe666.iceandfire.block;

import net.minecraft.ChatFormatting;
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


public class BlockSeaSerpentScales extends Block implements IBlockItemHoverText {
    ChatFormatting color;
    String name;

    public BlockSeaSerpentScales(String name, ChatFormatting color) {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.STONE)
                .strength(30F, 500F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops())
        );

        this.color = color;
        this.name = name;
    }

    @Override
    public void appendItemHoverText(@NotNull ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                    Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("sea_serpent." + name).withStyle(color));
    }
}
