package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollLeather;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public enum EnumTroll {
    FOREST(IafItemRegistry.TROLL_FOREST_ARMOR_MATERIAL, Weapon.TRUNK, Weapon.COLUMN_FOREST, Weapon.AXE, Weapon.HAMMER),
    FROST(IafItemRegistry.TROLL_FROST_ARMOR_MATERIAL, Weapon.COLUMN_FROST, Weapon.TRUNK_FROST, Weapon.AXE, Weapon.HAMMER),
    MOUNTAIN(IafItemRegistry.TROLL_MOUNTAIN_ARMOR_MATERIAL, Weapon.COLUMN, Weapon.AXE, Weapon.HAMMER);

    private final Weapon[] weapons;
    public Identifier TEXTURE;
    public Identifier TEXTURE_STONE;
    public Identifier TEXTURE_EYES;
    public CustomArmorMaterial material;
    public Supplier<Item> leather;
    public Supplier<Item> helmet;
    public Supplier<Item> chestplate;
    public Supplier<Item> leggings;
    public Supplier<Item> boots;

    EnumTroll(CustomArmorMaterial material, Weapon... weapons) {
        this.weapons = weapons;
        this.material = material;
        TEXTURE = Identifier.parse("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase(Locale.ROOT) + ".png");
        TEXTURE_STONE = Identifier.parse("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase(Locale.ROOT) + "_stone.png");
        TEXTURE_EYES = Identifier.parse("iceandfire:textures/models/troll/troll_" + this.name().toLowerCase(Locale.ROOT) + "_eyes.png");
        leather = () ->new ItemTrollLeather(this);
        helmet = () -> new ItemTrollArmor(this, material, EquipmentSlot.HEAD);
        chestplate = () -> new ItemTrollArmor(this, material, EquipmentSlot.CHEST);
        leggings = () -> new ItemTrollArmor(this, material, EquipmentSlot.LEGS);
        boots = () -> new ItemTrollArmor(this, material, EquipmentSlot.FEET);


        //leather = IafItemRegistry.deferredRegister.register("troll_leather_" + name().toLowerCase(Locale.ROOT), () -> new ItemTrollLeather(this));

        //Function<EquipmentSlot, RegistryObject<Item>> genArmor = (slot) ->
        //        IafItemRegistry.deferredRegister.register(ItemTrollArmor.getName(this, slot), () -> new ItemTrollArmor(this, material, slot));
        //helmet = genArmor.apply(EquipmentSlot.HEAD);
        //chestplate = genArmor.apply(EquipmentSlot.CHEST);
        //leggings = genArmor.apply(EquipmentSlot.LEGS);
        //boots = genArmor.apply(EquipmentSlot.FEET);


    }

    public TagKey<Item> repairTag() {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("iceandfire",
                "repairs_troll_" + name().toLowerCase(Locale.ROOT) + "_armor"));
    }

    public ResourceKey<EquipmentAsset> equipmentAsset() {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("iceandfire",
                "troll_" + name().toLowerCase(Locale.ROOT)));
    }

    public ArmorMaterial armorMaterial() {
        return material.toArmorMaterial(repairTag(), equipmentAsset());
    }

    /** ItemTrollArmor's 26.1 constructor must apply this to its registry-keyed properties. */
    public Item.Properties armorProperties(Item.Properties properties, ArmorType type) {
        return properties.humanoidArmor(armorMaterial(), type);
    }

    public static EnumTroll getBiomeType(Holder<Biome> biome) {
        List<EnumTroll> types = new ArrayList<>();
        if (BiomeConfig.test(BiomeConfig.snowyTrollBiomes, biome)) {
            types.add(EnumTroll.FROST);
        }
        if (BiomeConfig.test(BiomeConfig.forestTrollBiomes, biome)) {
            types.add(EnumTroll.FOREST);
        }
        if (BiomeConfig.test(BiomeConfig.mountainTrollBiomes, biome)) {
            types.add(EnumTroll.MOUNTAIN);
        }
        if (types.isEmpty()) {
            return values()[ThreadLocalRandom.current().nextInt(values().length)];
        } else {
            return types.get(ThreadLocalRandom.current().nextInt(types.size()));
        }
    }


    public static Weapon getWeaponForType(EnumTroll troll) {
        return troll.weapons[ThreadLocalRandom.current().nextInt(troll.weapons.length)];
    }

    public static void initArmors() {
        for (EnumTroll troll: EnumTroll.values()) {
            troll.leather = IafItemRegistry.register("troll_leather_%s".formatted(troll.name().toLowerCase(Locale.ROOT)), () -> new ItemTrollLeather(troll));
            troll.helmet = IafItemRegistry.register(ItemTrollArmor.getName(troll, EquipmentSlot.HEAD), () -> new ItemTrollArmor(troll, troll.material, EquipmentSlot.HEAD));
            troll.chestplate = IafItemRegistry.register(ItemTrollArmor.getName(troll, EquipmentSlot.CHEST), () -> new ItemTrollArmor(troll, troll.material, EquipmentSlot.CHEST));
            troll.leggings = IafItemRegistry.register(ItemTrollArmor.getName(troll, EquipmentSlot.LEGS), () -> new ItemTrollArmor(troll, troll.material, EquipmentSlot.LEGS));
            troll.boots = IafItemRegistry.register(ItemTrollArmor.getName(troll, EquipmentSlot.FEET), () -> new ItemTrollArmor(troll, troll.material, EquipmentSlot.FEET));
        }
    }

    public enum Weapon {
        AXE, COLUMN, COLUMN_FOREST, COLUMN_FROST, HAMMER, TRUNK, TRUNK_FROST;
        public Identifier TEXTURE;
        public Supplier<Item> item;

        Weapon() {
            TEXTURE = Identifier.parse("iceandfire:textures/models/troll/weapon/weapon_" + this.name().toLowerCase(Locale.ROOT) + ".png");
            item = IafItemRegistry.register("troll_weapon_" + this.name().toLowerCase(Locale.ROOT), () -> new ItemTrollWeapon(this));
        }

    }
}
