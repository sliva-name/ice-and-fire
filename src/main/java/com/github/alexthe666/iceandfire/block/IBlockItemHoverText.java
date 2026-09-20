package com.github.alexthe666.iceandfire.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 26.x {@code Block} no longer has {@code appendHoverText}. Block items that need extra
 * tooltip lines implement this and are wrapped by {@link IafBlockItem}.
 */
public interface IBlockItemHoverText {
    void appendItemHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                             Consumer<Component> tooltip, TooltipFlag flag);
}
