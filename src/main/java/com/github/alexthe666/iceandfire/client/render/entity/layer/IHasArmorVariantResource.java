package com.github.alexthe666.iceandfire.client.render.entity.layer;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;

public interface IHasArmorVariantResource {

    Identifier getArmorResource(int variant, EquipmentSlot slotType);

}
