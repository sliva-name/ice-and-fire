package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.props.OathProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class ItemOath extends Item {
    private final OathType type;

    public ItemOath(OathType type) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
        this.type = type;
    }

    public OathType type() {
        return type;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            OathType held = OathProperties.get(player);
            if (held == null) {
                player.sendSystemMessage(Component.translatable(OathProperties.isForsworn(player) ? "oath.message.shame" : "oath.message.none"));
            } else {
                OathProperties.forswear(player, held);
                level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.7F, 0.6F);
                player.sendSystemMessage(Component.translatable("oath.message.cleared"));
            }
            return InteractionResult.SUCCESS;
        }
        if (OathProperties.isForsworn(player)) {
            player.sendSystemMessage(Component.translatable("oath.message.forsworn"));
            return InteractionResult.SUCCESS;
        }
        OathType held = OathProperties.get(player);
        if (held == type) {
            player.sendSystemMessage(Component.translatable("oath.message.kept"));
            return InteractionResult.SUCCESS;
        }
        if (held != null) {
            OathProperties.forswear(player, held);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.7F, 0.6F);
            player.sendSystemMessage(Component.translatable("oath.message.betrayed"));
            return InteractionResult.SUCCESS;
        }
        OathProperties.set(player, type);
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.8F, 1.0F);
        player.sendSystemMessage(Component.translatable("oath.message.bound." + type.id()));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.accept(Component.translatable(getDescriptionId() + ".desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable(getDescriptionId() + ".desc_1").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.accept(Component.translatable("oath.message.how").withStyle(ChatFormatting.DARK_GRAY));
    }
}
