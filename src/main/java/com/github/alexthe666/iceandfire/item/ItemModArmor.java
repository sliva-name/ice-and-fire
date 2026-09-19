package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemModArmor extends Item implements IafArmorIdentity {
    private final CustomArmorMaterial iafMaterial;

    public ItemModArmor(CustomArmorMaterial material, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.iafMaterial = material;
    }

    @Override
    public CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (this == IafItemRegistry.EARPLUGS.get()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1) {
                return Component.translatable("item.iceandfire.air_pods");
            }
        }
        return super.getName(stack);
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.iafMaterial == IafItemRegistry.MYRMEX_DESERT_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "myrmex_desert_layer_2" : "myrmex_desert_layer_1") + ".png";
        }
        if (this.iafMaterial == IafItemRegistry.MYRMEX_JUNGLE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "myrmex_jungle_layer_2" : "myrmex_jungle_layer_1") + ".png";
        }
        if (this.iafMaterial == IafItemRegistry.SHEEP_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "sheep_disguise_layer_2" : "sheep_disguise_layer_1") + ".png";
        }
        if (this.iafMaterial == IafItemRegistry.EARPLUGS_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/earplugs_layer_1.png";
        }
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (this == IafItemRegistry.EARPLUGS.get()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1) {
                tooltip.accept(Component.translatable("item.iceandfire.air_pods.desc").withStyle(ChatFormatting.GREEN));
            }
        }
        super.appendHoverText(stack, context, display, tooltip, flagIn);
    }
}
