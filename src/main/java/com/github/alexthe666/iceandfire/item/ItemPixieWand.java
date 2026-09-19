package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityPixieCharge;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.util.IafEnchantments;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemPixieWand extends Item {

    public ItemPixieWand() {
        super(IafItemRegistry.defaultBuilder().stacksTo(1).durability(500));
    }


    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        boolean flag = playerIn.isCreative() || IafEnchantments.has(itemStackIn, playerIn, Enchantments.INFINITY);
        ItemStack itemstack = this.findAmmo(playerIn);
        playerIn.startUsingItem(hand);
        playerIn.swing(hand);
        if (!itemstack.isEmpty() || flag) {
            boolean flag1 = playerIn.isCreative() || this.isInfinite(itemstack, itemStackIn, playerIn);
            if (!flag1) {
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    playerIn.getInventory().removeItem(itemstack);
                }
            }
            double d2 = playerIn.getLookAngle().x;
            double d3 = playerIn.getLookAngle().y;
            double d4 = playerIn.getLookAngle().z;
            float inaccuracy = 1.0F;
            d2 = d2 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d3 = d3 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d4 = d4 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            EntityPixieCharge charge = new EntityPixieCharge(IafEntityRegistry.PIXIE_CHARGE.get(), worldIn, playerIn,
                d2, d3, d4);
            charge.setPos(playerIn.getX(), playerIn.getY() + 1, playerIn.getZ());
            if (!worldIn.isClientSide()) {
                worldIn.addFreshEntity(charge);
            }
            playerIn.playSound(IafSoundRegistry.PIXIE_WAND, 1F, 0.75F + 0.5F * playerIn.getRandom().nextFloat());
            itemstack.hurtAndBreak(1, playerIn, playerIn.getUsedItemHand());
            playerIn.getCooldowns().addCooldown(itemStackIn, 5);
        }
        return InteractionResult.PASS;
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
        return IafEnchantments.has(bow, player, Enchantments.INFINITY) && stack.getItem() == IafItemRegistry.PIXIE_DUST.get();
    }

    private ItemStack findAmmo(Player player) {
        if (this.isAmmo(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (this.isAmmo(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);

                if (this.isAmmo(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    protected boolean isAmmo(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItemRegistry.PIXIE_DUST.get();
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.pixie_wand.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.pixie_wand.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
