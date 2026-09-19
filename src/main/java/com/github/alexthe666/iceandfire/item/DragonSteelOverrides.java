package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Consumer;

public interface DragonSteelOverrides<T extends Item & DragonSteelOverrides<T>> {

    ToolMaterial getTier();

    static ToolMaterial toolMaterial(ToolMaterial material) {
        if (!DragonSteelTier.isDragonsteel(material)) {
            return material;
        }
        // Vanilla applies durability and attribute modifiers while building the tool properties.
        // Keep the original material on the item: elemental effects compare its identity.
        return new ToolMaterial(material.incorrectBlocksForDrops(), IafConfig.dragonsteelBaseDurability,
            material.speed(), 0.0F, material.enchantmentValue(), material.repairItems());
    }

    static float attackDamageBaseline(ToolMaterial material, float normalDamage, double dragonsteelDamage) {
        return DragonSteelTier.isDragonsteel(material) ? (float) dragonsteelDamage : normalDamage;
    }

    default float getAttackDamage(T item) {
        ItemAttributeModifiers modifiers = item.components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return (float) modifiers.compute(Attributes.ATTACK_DAMAGE, 0.0D, EquipmentSlot.MAINHAND);
    }

    default boolean isDragonsteel(ToolMaterial tier) {
        return DragonSteelTier.isDragonsteel(tier);
    }

    default boolean isDragonsteelFire(ToolMaterial tier) {
        return tier == DragonSteelTier.DRAGONSTEEL_TIER_FIRE;
    }

    default boolean isDragonsteelIce(ToolMaterial tier) {
        return tier == DragonSteelTier.DRAGONSTEEL_TIER_ICE;
    }

    default boolean isDragonsteelLightning(ToolMaterial tier) {
        return tier == DragonSteelTier.DRAGONSTEEL_TIER_LIGHTNING;
    }

    default void hurtEnemy(T item, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(target.level() instanceof ServerLevel level)) {
            return;
        }
        if (item.getTier() == IafItemRegistry.SILVER_TOOL_MATERIAL && target.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
            target.hurtServer(level, target.damageSources().magic(), getAttackDamage(item) + 3.0F);
        }
        if (item.getTier() == IafItemRegistry.MYRMEX_CHITIN_TOOL_MATERIAL) {
            if (!target.getType().builtInRegistryHolder().is(EntityTypeTags.ARTHROPOD)) {
                target.hurtServer(level, target.damageSources().generic(), getAttackDamage(item) + 5.0F);
            }
            if (target instanceof EntityDeathWorm) {
                target.hurtServer(level, target.damageSources().generic(), getAttackDamage(item) + 5.0F);
            }
        }
        if (isDragonsteelFire(item.getTier()) && IafConfig.dragonWeaponFireAbility) {
            target.igniteForSeconds(15.0F);
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (isDragonsteelIce(item.getTier()) && IafConfig.dragonWeaponIceAbility) {
            FrozenProperties.setFrozenFor(target, 300);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, 2));
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (isDragonsteelLightning(item.getTier()) && IafConfig.dragonWeaponLightningAbility) {
            if (!(attacker instanceof Player) || attacker.attackAnim <= 0.2F) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    bolt.addTag(ServerEvents.BOLT_DONT_DESTROY_LOOT);
                    bolt.addTag(attacker.getStringUUID());
                    bolt.snapTo(target.position());
                    level.addFreshEntity(bolt);
                }
            }
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
    }

    default void appendHoverText(ToolMaterial tier, Consumer<Component> tooltip) {
        if (tier == IafItemRegistry.SILVER_TOOL_MATERIAL) {
            tooltip.accept(Component.translatable("silvertools.hurt").withStyle(ChatFormatting.GREEN));
        }
        if (tier == IafItemRegistry.MYRMEX_CHITIN_TOOL_MATERIAL) {
            tooltip.accept(Component.translatable("myrmextools.hurt").withStyle(ChatFormatting.GREEN));
        }
        if (isDragonsteelFire(tier) && IafConfig.dragonWeaponFireAbility) {
            tooltip.accept(Component.translatable("dragon_sword_fire.hurt2").withStyle(ChatFormatting.DARK_RED));
        }
        if (isDragonsteelIce(tier) && IafConfig.dragonWeaponIceAbility) {
            tooltip.accept(Component.translatable("dragon_sword_ice.hurt2").withStyle(ChatFormatting.AQUA));
        }
        if (isDragonsteelLightning(tier) && IafConfig.dragonWeaponLightningAbility) {
            tooltip.accept(Component.translatable("dragon_sword_lightning.hurt2").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
