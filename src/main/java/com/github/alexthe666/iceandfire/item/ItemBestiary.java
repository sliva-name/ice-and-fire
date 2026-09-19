package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.google.common.primitives.Ints;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class ItemBestiary extends Item {

    public ItemBestiary() {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
    }

    @Override
    public void onCraftedBy(ItemStack stack, @NotNull Player playerIn) {
        IafItemData.update(stack, tag -> tag.putIntArray("Pages", new int[]{0}));
    }

    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
        ItemStack stack = new ItemStack(IafItemRegistry.BESTIARY.get());
        int[] pages = new int[EnumBestiaryPages.values().length];
        for (int i = 0; i < EnumBestiaryPages.values().length; i++) {
            pages[i] = i;
        }
        IafItemData.update(stack, tag -> tag.putIntArray("Pages", pages));
        items.add(stack);
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide()) {
            IceAndFire.PROXY.openBestiaryGui(itemStackIn);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel worldIn, @NotNull Entity entityIn, @NotNull net.minecraft.world.entity.EquipmentSlot slot) {
        if (!IafItemData.has(stack)) {
            IafItemData.update(stack, tag -> tag.putIntArray("Pages", new int[]{EnumBestiaryPages.INTRODUCTION.ordinal()}));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (IafItemData.has(stack)) {
            if (IceAndFire.PROXY.shouldSeeBestiaryContents()) {
                tooltip.accept(Component.translatable("bestiary.contains").withStyle(ChatFormatting.GRAY));
                int[] pageIds = IafItemData.copy(stack).getIntArray("Pages").orElseGet(() -> new int[0]);
                final Set<EnumBestiaryPages> pages = EnumBestiaryPages
                    .containedPages(Ints.asList(pageIds));
                for (EnumBestiaryPages page : pages) {
                    tooltip.accept(Component.literal(ChatFormatting.WHITE + "-").append(Component.translatable("bestiary." + EnumBestiaryPages.values()[page.ordinal()].toString().toLowerCase())).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltip.accept(Component.translatable("bestiary.hold_shift").withStyle(ChatFormatting.GRAY));
            }

        }
    }

}
