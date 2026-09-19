package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityTideTrident;
import com.github.alexthe666.iceandfire.entity.util.IafEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ItemTideTrident extends TridentItem {

    public ItemTideTrident() {
        super(IafItemRegistry.defaultBuilder().durability(400).attributes(createTideAttributes()));
    }

    private static ItemAttributeModifiers createTideAttributes() {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 12.0D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.9D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .build();
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) {
            return false;
        }
        int charge = this.getUseDuration(stack, entityLiving) - timeLeft;
        if (charge < 10) {
            return false;
        }
        int riptide = IafEnchantments.level(stack, player, Enchantments.RIPTIDE);
        if (riptide > 0 && !player.isInWaterOrRain()) {
            return false;
        }
        if (!worldIn.isClientSide()) {
            stack.hurtAndBreak(1, player, entityLiving.getUsedItemHand());
            if (riptide == 0) {
                EntityTideTrident thrown = new EntityTideTrident(worldIn, player, stack);
                thrown.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float) riptide * 0.5F, 1.0F);
                if (player.getAbilities().instabuild) {
                    thrown.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                worldIn.addFreshEntity(thrown);
                worldIn.playSound(null, thrown, SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    player.getInventory().removeItem(stack);
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (riptide > 0) {
            float yaw = player.getYRot();
            float pitch = player.getXRot();
            float x = -Mth.sin(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
            float y = -Mth.sin(pitch * 0.017453292F);
            float z = Mth.cos(yaw * 0.017453292F) * Mth.cos(pitch * 0.017453292F);
            float length = Mth.sqrt(x * x + y * y + z * z);
            float force = 3.0F * ((1.0F + (float) riptide) / 4.0F);
            x *= force / length;
            y *= force / length;
            z *= force / length;
            player.push(x, y, z);
            player.startAutoSpinAttack(20, EnchantmentHelper.getTridentSpinAttackStrength(stack, player), stack);
            if (player.onGround()) {
                player.move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));
            }

            SoundEvent riptideSound;
            if (riptide >= 3) {
                riptideSound = SoundEvents.TRIDENT_RIPTIDE_3.value();
            } else if (riptide == 2) {
                riptideSound = SoundEvents.TRIDENT_RIPTIDE_2.value();
            } else {
                riptideSound = SoundEvents.TRIDENT_RIPTIDE_1.value();
            }
            worldIn.playSound(null, player, riptideSound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.PIERCING)) {
            return true;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.tide_trident.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.tide_trident.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
