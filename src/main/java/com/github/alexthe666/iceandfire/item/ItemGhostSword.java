package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityGhostSword;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class ItemGhostSword extends Item {

    public ItemGhostSword() {
        super(IafItemRegistry.defaultBuilder().sword(IafItemRegistry.GHOST_SWORD_TOOL_MATERIAL, 5, -1.0F));
    }

    public static void spawnGhostSwordEntity(ItemStack stack, Player playerEntity) {
        if (playerEntity.getCooldowns().isOnCooldown(stack)) {
            return;
        }
        if (playerEntity.getItemInHand(InteractionHand.MAIN_HAND) != stack) {
            return;
        }
        double[] totalDmg = {0D};
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
            if (attribute == Attributes.ATTACK_DAMAGE) {
                totalDmg[0] += modifier.amount();
            }
        });
        playerEntity.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1);
        EntityGhostSword shot = new EntityGhostSword(IafEntityRegistry.GHOST_SWORD.get(), playerEntity.level(), playerEntity, (float) (totalDmg[0] * 0.5F));
        shot.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, 1, 0.5f);
        playerEntity.level().addFreshEntity(shot);
        stack.hurtAndBreak(1, playerEntity, EquipmentSlot.MAINHAND);
        playerEntity.getCooldowns().addCooldown(stack, 10);
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity targetEntity, @NotNull LivingEntity attacker) {
        super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.ghost_sword.desc_0").withStyle(ChatFormatting.GRAY));
    }
}
