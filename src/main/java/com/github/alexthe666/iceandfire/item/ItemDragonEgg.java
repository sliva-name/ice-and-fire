package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class ItemDragonEgg extends Item {
    public EnumDragonEgg type;

    public ItemDragonEgg(EnumDragonEgg type) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
        this.type = type;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable("item.iceandfire.dragonegg");
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        IafItemData.write(itemStack, new CompoundTag());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("dragon." + type.toString().toLowerCase()).withStyle(type.color));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getPlayer().getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace());
        EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG.get(), context.getLevel());
        egg.setEggType(type);
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
}
