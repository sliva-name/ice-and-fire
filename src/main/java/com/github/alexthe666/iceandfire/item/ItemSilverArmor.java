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

    // Textures: assets/iceandfire/equipment/silver.json (1.18 getArmorTexture is gone in 26.1).
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(com.github.alexthe666.iceandfire.client.render.IafArmorRenderProperties.armorModel(
            (stack, inner) -> new ModelSilverArmor(inner)));
    }


}