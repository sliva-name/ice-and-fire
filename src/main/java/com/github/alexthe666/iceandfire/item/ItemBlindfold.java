package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemBlindfold extends Item implements IafArmorIdentity {

    public ItemBlindfold() {
        super(IafArmors.properties(IafItemRegistry.BLINDFOLD_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    }

    @Override
    public com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial() {
        return IafItemRegistry.BLINDFOLD_ARMOR_MATERIAL;
    }

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel world, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player && slot == EquipmentSlot.HEAD) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, false, false));
        }
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "iceandfire:textures/models/armor/blindfold_layer_1.png";
    }
}
