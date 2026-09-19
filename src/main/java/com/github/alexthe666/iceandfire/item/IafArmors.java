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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

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

    /**
     * 1.18 material names ({@code "silver"}, {@code "red deathworm"},
     * {@code "iceandfire:armor_dragon_scales3"}) as a 26.1 id path. The result names the
     * equipment asset ({@code assets/iceandfire/equipment/<path>.json}) and repair tag.
     */
    public static String assetPath(CustomArmorMaterial material) {
        String name = material.getName().toLowerCase(java.util.Locale.ROOT);
        if (name.startsWith("iceandfire:")) {
            name = name.substring("iceandfire:".length());
        }
        return name.replace(' ', '_').replace(':', '_');
    }

    public static ArmorMaterial vanilla(CustomArmorMaterial material) {
        String path = assetPath(material);
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

    /**
     * Client armor models live in {@code IafArmorRenderProperties}. This uses a
     * string class name so dedicated-server DistCleaner never sees
     * {@code HumanoidModel} in common item bytecode.
     */
    public static void initClient(Item item, java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            Class.forName("com.github.alexthe666.iceandfire.client.render.IafArmorRenderProperties")
                .getMethod("register", Item.class, java.util.function.Consumer.class)
                .invoke(null, item, consumer);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to register Ice and Fire armor client extensions", e);
        }
    }
}
