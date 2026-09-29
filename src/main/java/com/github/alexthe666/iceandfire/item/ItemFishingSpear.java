package com.github.alexthe666.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemFishingSpear extends Item {

    public ItemFishingSpear() {
        super(IafItemRegistry.defaultBuilder().durability(64));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (!player.isInWater()) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.translatable("item.iceandfire.fishing_spear.dry"));
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            ItemStack fish = new ItemStack(rollFish(player));
            if (!player.getInventory().add(fish)) {
                player.drop(fish, false);
            }
            stack.hurtAndBreak(1, player, hand);
            player.getCooldowns().addCooldown(stack, 30);
            level.playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    private static net.minecraft.world.item.Item rollFish(Player player) {
        int roll = player.getRandom().nextInt(10);
        if (roll == 0) {
            return Items.PUFFERFISH;
        }
        if (roll < 3) {
            return Items.SALMON;
        }
        return Items.COD;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.accept(Component.translatable("item.iceandfire.fishing_spear.desc").withStyle(ChatFormatting.GRAY));
    }
}
