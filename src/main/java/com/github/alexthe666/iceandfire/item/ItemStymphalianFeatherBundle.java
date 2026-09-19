package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianFeather;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemStymphalianFeatherBundle extends Item {

    public ItemStymphalianFeatherBundle() {
        super(IafItemRegistry.defaultBuilder());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = player.getItemInHand(hand);
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(itemStackIn, 15);
        player.playSound(SoundEvents.EGG_THROW, 1, 1);
        float rotation = player.yHeadRot;
        for (int i = 0; i < 8; i++) {
            EntityStymphalianFeather feather = new EntityStymphalianFeather(IafEntityRegistry.STYMPHALIAN_FEATHER.get(),
                worldIn, player);
            rotation += 45;
            feather.shootFromRotation(player, 0, rotation, 0.0F, 1.5F, 1.0F);
            if (!worldIn.isClientSide()) {
                worldIn.addFreshEntity(feather);
            }
        }
        if (!player.isCreative()) {
            itemStackIn.shrink(1);
        }
        return InteractionResult.PASS;
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.stymphalian_feather_bundle.desc_0").withStyle(ChatFormatting.GRAY));
    }
}