package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.util.IafEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemHippogryphSword extends Item {

    public ItemHippogryphSword() {
        super(IafItemRegistry.defaultBuilder().sword(IafItemRegistry.HIPPOGRYPH_SWORD_TOOL_MATERIAL, 3, -2.4F));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity targetEntity, LivingEntity attacker) {
        float f = (float) attacker.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        int sweep = IafEnchantments.level(attacker, Enchantments.SWEEPING_EDGE);
        float ratio = sweep > 0 ? 1.0F - 1.0F / (sweep + 1.0F) : 0.0F;
        float f3 = 1.0F + ratio * f;
        if (attacker instanceof Player player && player.level() instanceof ServerLevel server) {
            for (LivingEntity nearby : attacker.level().getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                if (nearby != player && nearby != targetEntity && !attacker.isAlliedTo(nearby) && attacker.distanceToSqr(nearby) < 9.0D) {
                    nearby.knockback(0.4F, Mth.sin(attacker.getYRot() * 0.017453292F), -Mth.cos(attacker.getYRot() * 0.017453292F));
                    nearby.hurtServer(server, player.damageSources().playerAttack(player), f3);
                }
            }
            double d0 = -Mth.sin(player.getYRot() * 0.017453292F);
            double d1 = Mth.cos(player.getYRot() * 0.017453292F);
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d0, player.getY(0.5D), player.getZ() + d1, 0, d0, 0.0D, d1, 0.0D);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
        }
        super.hurtEnemy(stack, targetEntity, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippogryph_sword.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.hippogryph_sword.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
