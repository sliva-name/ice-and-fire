package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.armor.ModelDeathWormArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemDeathwormArmor extends Item implements IafArmorIdentity {

    private final com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial;
    public ItemDeathwormArmor(com.github.alexthe666.citadel.server.item.CustomArmorMaterial material, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.iafMaterial = material;
    }

    @Override
    public com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.iafMaterial() == IafItemRegistry.DEATHWORM_2_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_deathworm_red" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else if (this.iafMaterial() == IafItemRegistry.DEATHWORM_1_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_deathworm_white" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_deathworm_yellow" + (slot == EquipmentSlot.LEGS ? "_legs.png" : ".png");
        }
    }
}
