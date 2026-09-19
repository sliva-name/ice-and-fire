package com.github.alexthe666.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSeaSerpentScales extends ItemGeneric {

    private final ChatFormatting color;
    private final String colorName;

    public ItemSeaSerpentScales(String colorName, ChatFormatting color) {
        super();
        this.color = color;
        this.colorName = colorName;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("sea_serpent." + colorName).withStyle(color));
    }
}
