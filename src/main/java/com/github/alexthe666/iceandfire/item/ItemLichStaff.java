package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDreadLichSkull;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemLichStaff extends Item {

    public ItemLichStaff() {
        super(IafItemRegistry.defaultBuilder().durability(100).repairable(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(IceAndFire.MODID, "repairs_lich_staff"))));
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        if (!worldIn.isClientSide()) {
            playerIn.startUsingItem(hand);
            playerIn.swing(hand);
            double d2 = playerIn.getLookAngle().x;
            double d3 = playerIn.getLookAngle().y;
            double d4 = playerIn.getLookAngle().z;
            float inaccuracy = 1.0F;
            d2 = d2 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d3 = d3 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d4 = d4 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            EntityDreadLichSkull charge = new EntityDreadLichSkull(IafEntityRegistry.DREAD_LICH_SKULL.get(), worldIn,
                playerIn, 6);
            charge.shoot(playerIn.getXRot(), playerIn.getYRot(), 0.0F, 7.0F, 1.0F);
            charge.setPos(playerIn.getX(), playerIn.getY() + 1, playerIn.getZ());
            worldIn.addFreshEntity(charge);
            charge.shoot(d2, d3, d4, 1, 1);
            playerIn.playSound(SoundEvents.ZOMBIE_INFECT, 1F, 0.75F + 0.5F * playerIn.getRandom().nextFloat());
            itemStackIn.hurtAndBreak(1, playerIn, hand);
            playerIn.getCooldowns().addCooldown(itemStackIn, 4);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.lich_staff.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.lich_staff.desc_1").withStyle(ChatFormatting.GRAY));
    }
}