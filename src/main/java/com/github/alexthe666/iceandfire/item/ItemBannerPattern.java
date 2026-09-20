package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 1.18 used {@code BannerPatternItem}, which always appended {@code <id>.desc} in gray.
 * 26.x {@code LoomMenu.isPatternItem} requires {@code #minecraft:loom_patterns} plus
 * {@code PROVIDES_BANNER_PATTERNS}. Vanilla creeper/skull items bind that component
 * with {@code HolderLookup.Provider.getOrThrow(pattern-item tag)}.
 */
public class ItemBannerPattern extends Item {
    public ItemBannerPattern(String patternName) {
        super(IafItemRegistry.unstackable().delayedComponent(
            DataComponents.PROVIDES_BANNER_PATTERNS,
            registries -> registries.getOrThrow(
                TagKey.create(Registries.BANNER_PATTERN,
                    Identifier.fromNamespaceAndPath(IceAndFire.MODID, "pattern_item/" + patternName)))));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.accept(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
