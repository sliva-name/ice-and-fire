package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.props.FrozenProperties;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAlchemySword extends Item {

    public ItemAlchemySword(ToolMaterial toolmaterial) {
        super(IafItemRegistry.defaultBuilder().sword(toolmaterial, 3, -2.4F));
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (this == IafItemRegistry.DRAGONBONE_SWORD_FIRE.get() && IafConfig.dragonWeaponFireAbility) {
            if (target instanceof EntityIceDragon) {
                target.hurt(target.damageSources().inFire(), 13.5F);
            }
            target.igniteForSeconds(5);
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (this == IafItemRegistry.DRAGONBONE_SWORD_ICE.get() && IafConfig.dragonWeaponIceAbility) {
            if (target instanceof EntityFireDragon) {
                target.hurt(target.damageSources().drown(), 13.5F);
            }
            FrozenProperties.setFrozenFor(target, 200);
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
            target.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 100, 2));
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        if (this == IafItemRegistry.DRAGONBONE_SWORD_LIGHTNING.get() && IafConfig.dragonWeaponLightningAbility) {
            boolean flag = true;
            if (attacker instanceof Player) {
                if (attacker.getAttackAnim(1.0F) > 0.2F) {
                    flag = false;
                }
            }
            if (!attacker.level().isClientSide() && flag) {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(target.level(), net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
                lightningboltentity.addTag(ServerEvents.BOLT_DONT_DESTROY_LOOT);
                lightningboltentity.addTag(attacker.getStringUUID());
                lightningboltentity.snapTo(target.position());
                if (!target.level().isClientSide()) {
                    target.level().addFreshEntity(lightningboltentity);
                }
            }
            if (target instanceof EntityFireDragon || target instanceof EntityIceDragon) {
                target.hurt(target.damageSources().lightningBolt(), 9.5F);
            }
            target.knockback(1F, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
        }
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        if (this == IafItemRegistry.DRAGONBONE_SWORD_FIRE.get()) {
            tooltip.accept(Component.translatable("dragon_sword_fire.hurt1").withStyle(ChatFormatting.GREEN));
            if (IafConfig.dragonWeaponFireAbility)
                tooltip.accept(Component.translatable("dragon_sword_fire.hurt2").withStyle(ChatFormatting.DARK_RED));
        }
        if (this == IafItemRegistry.DRAGONBONE_SWORD_ICE.get()) {
            tooltip.accept(Component.translatable("dragon_sword_ice.hurt1").withStyle(ChatFormatting.GREEN));
            if (IafConfig.dragonWeaponIceAbility)
                tooltip.accept(Component.translatable("dragon_sword_ice.hurt2").withStyle(ChatFormatting.AQUA));
        }
        if (this == IafItemRegistry.DRAGONBONE_SWORD_LIGHTNING.get()) {
            tooltip.accept(Component.translatable("dragon_sword_lightning.hurt1").withStyle(ChatFormatting.GREEN));
            if (IafConfig.dragonWeaponLightningAbility)
                tooltip.accept(Component.translatable("dragon_sword_lightning.hurt2").withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
}
