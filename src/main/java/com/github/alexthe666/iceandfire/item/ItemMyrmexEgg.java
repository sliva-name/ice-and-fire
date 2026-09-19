package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ItemMyrmexEgg extends Item {

    boolean isJungle;

    public ItemMyrmexEgg(boolean isJungle) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
        this.isJungle = isJungle;
    }

    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        for (int i = 0; i < 5; i++) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = new CompoundTag();
            tag.putInt("EggOrdinal", i);
            IafItemData.write(stack, tag);
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        String caste;
        int eggOrdinal = IafItemData.has(stack) ? IafItemData.copy(stack).getIntOr("EggOrdinal", 0) : 0;
        switch (eggOrdinal) {
            default:
                caste = "worker";
                break;
            case 1:
                caste = "soldier";
                break;
            case 2:
                caste = "royal";
                break;
            case 3:
                caste = "sentinel";
                break;
            case 4:
                caste = "queen";
        }
        if (eggOrdinal == 4) {
            tooltip.accept(Component.translatable("myrmex.caste_" + caste + ".name").withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.accept(Component.translatable("myrmex.caste_" + caste + ".name").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getPlayer().getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace());
        EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG.get(), context.getLevel());
        int eggOrdinal = IafItemData.has(itemstack) ? IafItemData.copy(itemstack).getIntOr("EggOrdinal", 0) : 0;
        egg.setJungle(isJungle);
        egg.setMyrmexCaste(eggOrdinal);
        egg.snapTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        egg.onPlayerPlace(context.getPlayer());
        if (itemstack.has(DataComponents.CUSTOM_NAME)) {
            egg.setCustomName(itemstack.getHoverName());
        }
        if (!context.getLevel().isClientSide()) {
            context.getLevel().addFreshEntity(egg);
        }
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        int eggOrdinal = IafItemData.has(stack) ? IafItemData.copy(stack).getIntOr("EggOrdinal", 0) : 0;
        return super.isFoil(stack) || eggOrdinal == 4;
    }
}
