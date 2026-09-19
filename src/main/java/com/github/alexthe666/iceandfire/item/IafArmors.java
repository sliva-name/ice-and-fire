package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * 1.18 {@code ArmorItem} is gone. Defense stays FEET/LEGS/CHEST/HEAD;
 * {@link CustomArmorMaterial#toArmorMaterial} then applies native slot
 * durability multipliers (13/15/16/11), matching 1.18 {@code IafArmorMaterial}.
 */
public final class IafArmors {
    private IafArmors() {
    }

    public static ArmorType type(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArmorType.HELMET;
            case CHEST -> ArmorType.CHESTPLATE;
            case LEGS -> ArmorType.LEGGINGS;
            case FEET -> ArmorType.BOOTS;
            default -> throw new IllegalArgumentException("Not a humanoid armor slot: " + slot);
        };
    }

    public static ArmorMaterial vanilla(CustomArmorMaterial material) {
        String path = material.getName().replace(' ', '_').replace(':', '_').toLowerCase();
        TagKey<Item> repair = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("iceandfire", "repairs_" + path));
        ResourceKey<EquipmentAsset> asset = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("iceandfire", path));
        return material.toArmorMaterial(repair, asset);
    }

    public static Item.Properties properties(CustomArmorMaterial material, EquipmentSlot slot) {
        return properties(material, slot, IafItemRegistry.defaultBuilder());
    }

    public static Item.Properties properties(CustomArmorMaterial material, EquipmentSlot slot, Item.Properties extra) {
        return extra.humanoidArmor(vanilla(material), type(slot));
    }

    public static boolean isIafArmor(ItemStack stack) {
        return stack.getItem() instanceof IafArmorIdentity;
    }
}
