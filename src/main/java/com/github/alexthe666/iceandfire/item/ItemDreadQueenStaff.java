package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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

public class ItemDreadQueenStaff extends Item {

    public ItemDreadQueenStaff() {
        super(IafItemRegistry.defaultBuilder().durability(100).repairable(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(IceAndFire.MODID, "repairs_dread_queen_staff"))));
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        if (!worldIn.isClientSide()) {
            playerIn.startUsingItem(hand);
            playerIn.swing(hand);
            int x = Mth.floor(playerIn.getX() + playerIn.getLookAngle().x * 2.0D) - 2 + playerIn.getRandom().nextInt(5);
            int z = Mth.floor(playerIn.getZ() + playerIn.getLookAngle().z * 2.0D) - 2 + playerIn.getRandom().nextInt(5);
            boolean knight = playerIn.getRandom().nextBoolean();
            EntityDreadQueen.spawnMinion(worldIn, playerIn, playerIn.getLastHurtMob(),
                x + 0.5D, EntityDreadQueen.heightFromXZ(worldIn, playerIn.getY(), x, z), z + 0.5D, knight);
            playerIn.playSound(SoundEvents.ZOMBIE_INFECT, 1F, 0.75F + 0.5F * playerIn.getRandom().nextFloat());
            itemStackIn.hurtAndBreak(1, playerIn, hand);
            playerIn.getCooldowns().addCooldown(itemStackIn, 60);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.dread_queen_staff.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.dread_queen_staff.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
