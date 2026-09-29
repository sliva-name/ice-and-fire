package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.world.IafDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemCarriedBlessing extends Item {
    public enum Kind {
        WASTE_SPUR,
        FROST_WARD,
        NIGHT_WRAP,
        STORM_PINION,
        GAZE_GAUZE
    }

    private final Kind kind;

    public ItemCarriedBlessing(Kind kind) {
        super(IafItemRegistry.unstackable());
        this.kind = kind;
    }

    public static void tick(Player player) {
        boolean spur = carries(player, IafItemRegistry.WASTE_SPUR.get());
        boolean ward = carries(player, IafItemRegistry.FROST_WARD.get());
        if (spur && player.onGround() && player.level().getBlockState(player.blockPosition().below()).is(BlockTags.SAND)) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, true, false));
        }
        if (ward && IafDimensions.isDreadLands(player.level())) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 0, true, false));
        }
        if (carries(player, IafItemRegistry.NIGHT_WRAP.get()) && !player.level().isBrightOutside()) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, true, false));
        }
        if (carries(player, IafItemRegistry.STORM_PINION.get()) && player.level().isRaining()) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, true, false));
        }
    }

    public static boolean carries(Player player, Item item) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            if (player.getInventory().getItem(slot).is(item)) {
                return true;
            }
        }
        return false;
    }

    public Kind kind() {
        return kind;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.accept(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
