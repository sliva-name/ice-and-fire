package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGeneric extends Item {
    int description = 0;

    public ItemGeneric() {
        super(IafItemRegistry.defaultBuilder());
    }

    public ItemGeneric(int textLength) {
        super(IafItemRegistry.defaultBuilder());
        this.description = textLength;
    }

    public ItemGeneric(int textLength, boolean hide) {
        super(IafItemRegistry.defaultBuilder());
        this.description = textLength;
    }

    public ItemGeneric(int textLength, int stacksize) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
        this.description = textLength;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        if (this == IafItemRegistry.CREATIVE_DRAGON_MEAL.get()) {
            return true;
        } else {
            return super.isFoil(stack);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (description > 0) {
            for (int i = 0; i < description; i++) {
                tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc_" + i).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
