package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.armor.ModelSilverArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemSilverArmor extends Item implements IafArmorIdentity {

    private final com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial;
    public ItemSilverArmor(com.github.alexthe666.citadel.server.item.CustomArmorMaterial material, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.iafMaterial = material;
    }

    @Override
    public com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "armor_silver_metal_layer_2" : "armor_silver_metal_layer_1") + ".png";
    }


}