package com.github.alexthe666.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemHydraHeart extends Item {

    public ItemHydraHeart() {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
    }

    public static void tickHotbar(Player player) {
        for (int slot = 0; slot <= 8; slot++) {
            if (player.getInventory().getItem(slot).getItem() instanceof ItemHydraHeart) {
                applyRegen(player);
                return;
            }
        }
    }

    private static void applyRegen(Player player) {
        double healthPercentage = player.getHealth() / Math.max(1, player.getMaxHealth());
        if (healthPercentage < 1.0D) {
            int level = 0;
            if (healthPercentage < 0.25D) {
                level = 3;
            } else if (healthPercentage < 0.5D) {
                level = 2;
            } else if (healthPercentage < 0.75D) {
                level = 1;
            }
            if (!player.hasEffect(MobEffects.REGENERATION) || player.getEffect(MobEffects.REGENERATION).getAmplifier() < level) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, level, true, false));
            }
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity entity, @NotNull EquipmentSlot slot) {
        if (entity instanceof Player player) {
            applyRegen(player);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hydra_heart.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hydra_heart.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
