package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IafConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemModShovel extends ShovelItem implements DragonSteelOverrides<ItemModShovel> {

    private final ToolMaterial tier;

    public ItemModShovel(ToolMaterial toolmaterial) {
        this(toolmaterial, IafItemRegistry.defaultBuilder());
    }

    public ItemModShovel(ToolMaterial toolmaterial, Item.Properties properties) {
        super(DragonSteelOverrides.toolMaterial(toolmaterial),
            DragonSteelOverrides.attackDamageBaseline(toolmaterial, 1.5F, IafConfig.dragonsteelBaseDamage - 1F + 1.5F),
            -3.0F, properties);
        this.tier = toolmaterial;
    }

    @Override
    public ToolMaterial getTier() {
        return tier;
    }

    @Override
    public void hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        hurtEnemy(this, stack, target, attacker);
        super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        appendHoverText(getTier(), tooltip);
    }
}
