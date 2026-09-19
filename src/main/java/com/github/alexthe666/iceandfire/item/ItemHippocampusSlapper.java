package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHippocampusSlapper extends Item {

    public ItemHippocampusSlapper() {
        super(IafItemRegistry.defaultBuilder().sword(IafItemRegistry.HIPPOCAMPUS_SWORD_TOOL_MATERIAL, 3, -2.4F));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, LivingEntity targetEntity, @NotNull LivingEntity attacker) {
        targetEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
        targetEntity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 2));
        targetEntity.playSound(SoundEvents.GUARDIAN_FLOP, 3, 1);
        super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippocampus_slapper.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippocampus_slapper.desc_1").withStyle(ChatFormatting.GRAY));
    }
}