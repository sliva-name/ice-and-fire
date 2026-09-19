package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
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

    // Textures: assets/iceandfire/equipment/<color>_deathworm.json (1.18 getArmorTexture is gone in 26.1).
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        IafArmors.initClient(this, consumer);
    }
}
