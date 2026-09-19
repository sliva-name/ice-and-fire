package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IafConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemModSword extends Item implements DragonSteelOverrides<ItemModSword> {

    private final ToolMaterial tier;

    public ItemModSword(ToolMaterial toolmaterial) {
        this(toolmaterial, IafItemRegistry.defaultBuilder());
    }

    public ItemModSword(ToolMaterial toolmaterial, Item.Properties properties) {
        super(IafToolProperties.sword(properties, DragonSteelOverrides.toolMaterial(toolmaterial),
            DragonSteelOverrides.attackDamageBaseline(toolmaterial, 3.0F, IafConfig.dragonsteelBaseDamage - 1F), -2.4F));
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

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }
}
