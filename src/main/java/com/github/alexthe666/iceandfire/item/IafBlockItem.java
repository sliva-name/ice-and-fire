package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.block.IBlockItemHoverText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class IafBlockItem extends BlockItem {
    public IafBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        if (getBlock() instanceof IBlockItemHoverText extra) {
            extra.appendItemHoverText(stack, context, display, tooltip, flag);
        }
        super.appendHoverText(stack, context, display, tooltip, flag);
    }
}
