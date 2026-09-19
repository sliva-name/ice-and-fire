package com.github.alexthe666.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import static com.github.alexthe666.iceandfire.item.IafItemRegistry.*;

public class ItemDragonsteelArmor extends Item implements IafArmorIdentity, IProtectAgainstDragonItem {

    private final com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial;
    private final EquipmentSlot armorSlot;

    public ItemDragonsteelArmor(com.github.alexthe666.citadel.server.item.CustomArmorMaterial material, int renderIndex, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.iafMaterial = material;
        this.armorSlot = slot;
    }

    @Override
    public com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }

    public int getDefense() {
        return this.iafMaterial.getDefenseForSlot(this.armorSlot);
    }

    public String getArmorTexture(ItemStack stack, net.minecraft.world.entity.Entity entity, EquipmentSlot slot, String type) {
        if (iafMaterial == DRAGONSTEEL_FIRE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_fire" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else if (iafMaterial == DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_dragonsteel_ice" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_dragonsteel_lightning" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        }
    }
}
