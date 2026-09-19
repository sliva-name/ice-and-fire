package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.*;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.util.Locale;

import static com.github.alexthe666.iceandfire.item.DragonSteelTier.*;

public class IafItemRegistry {
    public static CustomArmorMaterial SILVER_ARMOR_MATERIAL = new IafArmorMaterial("silver", 15, new int[]{1, 4, 5, 2}, 20, SoundEvents.ARMOR_EQUIP_CHAIN, 0);
    public static CustomArmorMaterial COPPER_ARMOR_MATERIAL = new IafArmorMaterial("copper", 10, new int[]{1, 3, 4, 2}, 15, SoundEvents.ARMOR_EQUIP_GOLD, 0);
    public static CustomArmorMaterial BLINDFOLD_ARMOR_MATERIAL = new IafArmorMaterial("blindfold", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial SHEEP_ARMOR_MATERIAL = new IafArmorMaterial("sheep", 5, new int[]{1, 3, 2, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial MYRMEX_DESERT_ARMOR_MATERIAL = new IafArmorMaterial("myrmexdesert", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial MYRMEX_JUNGLE_ARMOR_MATERIAL = new IafArmorMaterial("myrmexjungle", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial EARPLUGS_ARMOR_MATERIAL = new IafArmorMaterial("earplugs", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial DEATHWORM_0_ARMOR_MATERIAL = new IafArmorMaterial("yellow seathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial DEATHWORM_1_ARMOR_MATERIAL = new IafArmorMaterial("white seathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial DEATHWORM_2_ARMOR_MATERIAL = new IafArmorMaterial("red deathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial TROLL_MOUNTAIN_ARMOR_MATERIAL = new IafArmorMaterial("mountain troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial TROLL_FOREST_ARMOR_MATERIAL = new IafArmorMaterial("forest troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial TROLL_FROST_ARMOR_MATERIAL = new IafArmorMaterial("frost troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial DRAGONSTEEL_FIRE_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel_fire", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 6.0F);
    public static CustomArmorMaterial DRAGONSTEEL_ICE_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel_ice", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 6.0F);
    public static CustomArmorMaterial DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel_lightning", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ARMOR_EQUIP_DIAMOND, 6.0F);
    // Citadel 8018e44d's constructor takes damage, speed; native ToolMaterial takes speed, damage.
    public static ToolMaterial SILVER_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 460, 11.0F, 1.0F, 18, toolRepairTag("silver"));
    public static ToolMaterial COPPER_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 300, 0.7F, 0.0F, 10, toolRepairTag("copper"));
    public static ToolMaterial DRAGONBONE_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 1660, 10.0F, 4.0F, 22, toolRepairTag("dragonbone"));
    public static ToolMaterial FIRE_DRAGONBONE_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2000, 10F, 5.5F, 22, toolRepairTag("dragonbone"));
    public static ToolMaterial ICE_DRAGONBONE_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2000, 10F, 5.5F, 22, toolRepairTag("dragonbone"));
    public static ToolMaterial LIGHTNING_DRAGONBONE_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2000, 10F, 5.5F, 22, toolRepairTag("dragonbone"));
    public static ToolMaterial TROLL_WEAPON_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 300, 10F, 1F, 1, toolRepairTag("troll_weapon"));
    public static ToolMaterial MYRMEX_CHITIN_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 600, 6.0F, 1.0F, 8, toolRepairTag("myrmex_chitin"));
    public static ToolMaterial HIPPOGRYPH_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 10F, 2.5F, 10, toolRepairTag("hippogryph_sword"));
    public static ToolMaterial STYMHALIAN_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 10.0F, 2, 10, toolRepairTag("stymphalian_sword"));
    public static ToolMaterial AMPHITHERE_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 10F, 1F, 10, toolRepairTag("amphithere_sword"));
    public static ToolMaterial HIPPOCAMPUS_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 500, 0F, -2F, 50, toolRepairTag("hippocampus_sword"));
    public static ToolMaterial DREAD_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 100, 10F, 1F, 0, toolRepairTag("dread"));
    public static ToolMaterial DREAD_KNIGHT_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1200, 0F, 13F, 10, toolRepairTag("dread"));
    // Copper and ghost had no repair ingredient; their empty tags preserve that behavior.
    public static ToolMaterial GHOST_SWORD_TOOL_MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 3000, 10.0F, 5, 25, toolRepairTag("ghost_sword"));

    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, IceAndFire.MODID);
    private static final ThreadLocal<ResourceKey<Item>> CURRENT_KEY = new ThreadLocal<>();

    public static Item.Properties id(Item.Properties properties) {
        ResourceKey<Item> key = CURRENT_KEY.get();
        if (key == null) {
            throw new IllegalStateException("Item properties built outside IafItemRegistry.register");
        }
        return properties.setId(key);
    }

    public static <I extends Item> RegistryObject<I> register(String name, java.util.function.Supplier<I> factory) {
        return ITEMS.register(name, () -> {
            CURRENT_KEY.set(ITEMS.key(name));
            try {
                return factory.get();
            } finally {
                CURRENT_KEY.remove();
            }
        });
    }


    public static final RegistryObject<Item> BESTIARY = register("bestiary", ItemBestiary::new);
    public static final RegistryObject<Item> MANUSCRIPT = register("manuscript", ItemGeneric::new);
    public static final RegistryObject<Item> SAPPHIRE_GEM = register("sapphire_gem", ItemGeneric::new);
    public static final RegistryObject<Item> SILVER_INGOT = register("silver_ingot", ItemGeneric::new);
    public static final RegistryObject<Item> SILVER_NUGGET = register("silver_nugget", ItemGeneric::new);
    public static final RegistryObject<Item> AMYTHEST_GEM = register("amythest_gem", ItemGeneric::new);
    public static final RegistryObject<Item> COPPER_INGOT = register("copper_ingot", ItemGeneric::new);
    public static final RegistryObject<Item> COPPER_NUGGET = register("copper_nugget", ItemGeneric::new);
    public static final RegistryObject<Item> SILVER_HELMET = register("armor_silver_metal_helmet", () -> new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> SILVER_CHESTPLATE = register("armor_silver_metal_chestplate", () -> new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> SILVER_LEGGINGS = register("armor_silver_metal_leggings", () -> new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> SILVER_BOOTS = register("armor_silver_metal_boots", () -> new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> SILVER_SWORD = register("silver_sword", () -> new ItemModSword(SILVER_TOOL_MATERIAL, toolProperties("silver_sword")));
    public static final RegistryObject<Item> SILVER_SHOVEL = register("silver_shovel", () -> new ItemModShovel(SILVER_TOOL_MATERIAL, toolProperties("silver_shovel")));
    public static final RegistryObject<Item> SILVER_PICKAXE = register("silver_pickaxe", () -> new ItemModPickaxe(SILVER_TOOL_MATERIAL, toolProperties("silver_pickaxe")));
    public static final RegistryObject<Item> SILVER_AXE = register("silver_axe", () -> new ItemModAxe(SILVER_TOOL_MATERIAL, toolProperties("silver_axe")));
    public static final RegistryObject<Item> SILVER_HOE = register("silver_hoe", () -> new ItemModHoe(SILVER_TOOL_MATERIAL, toolProperties("silver_hoe")));

    public static final RegistryObject<Item> COPPER_HELMET = register("armor_copper_metal_helmet", () -> new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> COPPER_CHESTPLATE = register("armor_copper_metal_chestplate", () -> new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> COPPER_LEGGINGS = register("armor_copper_metal_leggings", () -> new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> COPPER_BOOTS = register("armor_copper_metal_boots", () -> new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> COPPER_SWORD = register("copper_sword", () -> new ItemModSword(COPPER_TOOL_MATERIAL, toolProperties("copper_sword")));
    public static final RegistryObject<Item> COPPER_SHOVEL = register("copper_shovel", () -> new ItemModShovel(COPPER_TOOL_MATERIAL, toolProperties("copper_shovel")));
    public static final RegistryObject<Item> COPPER_PICKAXE = register("copper_pickaxe", () -> new ItemModPickaxe(COPPER_TOOL_MATERIAL, toolProperties("copper_pickaxe")));
    public static final RegistryObject<Item> COPPER_AXE = register("copper_axe", () -> new ItemModAxe(COPPER_TOOL_MATERIAL, toolProperties("copper_axe")));
    public static final RegistryObject<Item> COPPER_HOE = register("copper_hoe", () -> new ItemModHoe(COPPER_TOOL_MATERIAL, toolProperties("copper_hoe")));

    public static final RegistryObject<Item> FIRE_STEW = register("fire_stew", ItemGeneric::new);
    public static final RegistryObject<Item> FROST_STEW = register("frost_stew", ItemGeneric::new);
    public static final RegistryObject<Item> LIGHTNING_STEW = register("lightning_stew", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGONEGG_RED = register("dragonegg_red", () -> new ItemDragonEgg(EnumDragonEgg.RED));
    public static final RegistryObject<Item> DRAGONEGG_GREEN = register("dragonegg_green", () -> new ItemDragonEgg(EnumDragonEgg.GREEN));
    public static final RegistryObject<Item> DRAGONEGG_BRONZE = register("dragonegg_bronze", () -> new ItemDragonEgg(EnumDragonEgg.BRONZE));
    public static final RegistryObject<Item> DRAGONEGG_GRAY = register("dragonegg_gray", () -> new ItemDragonEgg(EnumDragonEgg.GRAY));
    public static final RegistryObject<Item> DRAGONEGG_BLUE = register("dragonegg_blue", () -> new ItemDragonEgg(EnumDragonEgg.BLUE));
    public static final RegistryObject<Item> DRAGONEGG_WHITE = register("dragonegg_white", () -> new ItemDragonEgg(EnumDragonEgg.WHITE));
    public static final RegistryObject<Item> DRAGONEGG_SAPPHIRE = register("dragonegg_sapphire", () -> new ItemDragonEgg(EnumDragonEgg.SAPPHIRE));
    public static final RegistryObject<Item> DRAGONEGG_SILVER = register("dragonegg_silver", () -> new ItemDragonEgg(EnumDragonEgg.SILVER));
    public static final RegistryObject<Item> DRAGONEGG_ELECTRIC = register("dragonegg_electric", () -> new ItemDragonEgg(EnumDragonEgg.ELECTRIC));
    public static final RegistryObject<Item> DRAGONEGG_AMYTHEST = register("dragonegg_amythest", () -> new ItemDragonEgg(EnumDragonEgg.AMYTHEST));
    public static final RegistryObject<Item> DRAGONEGG_COPPER = register("dragonegg_copper", () -> new ItemDragonEgg(EnumDragonEgg.COPPER));
    public static final RegistryObject<Item> DRAGONEGG_BLACK = register("dragonegg_black", () -> new ItemDragonEgg(EnumDragonEgg.BLACK));
    public static final RegistryObject<Item> DRAGONSCALES_RED = register("dragonscales_red", () -> new ItemDragonScales(EnumDragonEgg.RED));
    public static final RegistryObject<Item> DRAGONSCALES_GREEN = register("dragonscales_green", () -> new ItemDragonScales(EnumDragonEgg.GREEN));
    public static final RegistryObject<Item> DRAGONSCALES_BRONZE = register("dragonscales_bronze", () -> new ItemDragonScales(EnumDragonEgg.BRONZE));
    public static final RegistryObject<Item> DRAGONSCALES_GRAY = register("dragonscales_gray", () -> new ItemDragonScales(EnumDragonEgg.GRAY));
    public static final RegistryObject<Item> DRAGONSCALES_BLUE = register("dragonscales_blue", () -> new ItemDragonScales(EnumDragonEgg.BLUE));
    public static final RegistryObject<Item> DRAGONSCALES_WHITE = register("dragonscales_white", () -> new ItemDragonScales(EnumDragonEgg.WHITE));
    public static final RegistryObject<Item> DRAGONSCALES_SAPPHIRE = register("dragonscales_sapphire", () -> new ItemDragonScales(EnumDragonEgg.SAPPHIRE));
    public static final RegistryObject<Item> DRAGONSCALES_SILVER = register("dragonscales_silver", () -> new ItemDragonScales(EnumDragonEgg.SILVER));
    public static final RegistryObject<Item> DRAGONSCALES_ELECTRIC = register("dragonscales_electric", () -> new ItemDragonScales(EnumDragonEgg.ELECTRIC));
    public static final RegistryObject<Item> DRAGONSCALES_AMYTHEST = register("dragonscales_amythest", () -> new ItemDragonScales(EnumDragonEgg.AMYTHEST));
    public static final RegistryObject<Item> DRAGONSCALES_COPPER = register("dragonscales_copper", () -> new ItemDragonScales(EnumDragonEgg.COPPER));
    public static final RegistryObject<Item> DRAGONSCALES_BLACK = register("dragonscales_black", () -> new ItemDragonScales(EnumDragonEgg.BLACK));
    public static final RegistryObject<Item> DRAGON_BONE = register("dragonbone", () -> new ItemDragonBone());
    public static final RegistryObject<Item> WITHERBONE = register("witherbone", ItemGeneric::new);
    public static final RegistryObject<Item> FISHING_SPEAR = register("fishing_spear", () -> new ItemFishingSpear());
    public static final RegistryObject<Item> WITHER_SHARD = register("wither_shard", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGONBONE_SWORD = register("dragonbone_sword", () -> new ItemModSword(DRAGONBONE_TOOL_MATERIAL, toolProperties("dragonbone_sword")));
    public static final RegistryObject<Item> DRAGONBONE_SHOVEL = register("dragonbone_shovel", () -> new ItemModShovel(DRAGONBONE_TOOL_MATERIAL, toolProperties("dragonbone_shovel")));
    public static final RegistryObject<Item> DRAGONBONE_PICKAXE = register("dragonbone_pickaxe", () -> new ItemModPickaxe(DRAGONBONE_TOOL_MATERIAL, toolProperties("dragonbone_pickaxe")));
    public static final RegistryObject<Item> DRAGONBONE_AXE = register("dragonbone_axe", () -> new ItemModAxe(DRAGONBONE_TOOL_MATERIAL, toolProperties("dragonbone_axe")));
    public static final RegistryObject<Item> DRAGONBONE_HOE = register("dragonbone_hoe", () -> new ItemModHoe(DRAGONBONE_TOOL_MATERIAL, toolProperties("dragonbone_hoe")));
    public static final RegistryObject<Item> DRAGONBONE_SWORD_FIRE = register("dragonbone_sword_fire", () -> new ItemAlchemySword(FIRE_DRAGONBONE_TOOL_MATERIAL));
    public static final RegistryObject<Item> DRAGONBONE_SWORD_ICE = register("dragonbone_sword_ice", () -> new ItemAlchemySword(ICE_DRAGONBONE_TOOL_MATERIAL));
    public static final RegistryObject<Item> DRAGONBONE_SWORD_LIGHTNING = register("dragonbone_sword_lightning", () -> new ItemAlchemySword(LIGHTNING_DRAGONBONE_TOOL_MATERIAL));
    public static final RegistryObject<Item> DRAGONBONE_ARROW = register("dragonbone_arrow", () -> new ItemDragonArrow());
    public static final RegistryObject<Item> DRAGON_BOW = register("dragonbone_bow", () -> new ItemDragonBow());
    public static final RegistryObject<Item> DRAGON_SKULL_FIRE = register(ItemDragonSkull.getName(0), () -> new ItemDragonSkull(0));
    public static final RegistryObject<Item> DRAGON_SKULL_ICE = register(ItemDragonSkull.getName(1), () -> new ItemDragonSkull(1));
    public static final RegistryObject<Item> DRAGON_SKULL_LIGHTNING = register(ItemDragonSkull.getName(2), () -> new ItemDragonSkull(2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_IRON_0 = register("dragonarmor_iron_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.IRON, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_IRON_1 = register("dragonarmor_iron_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.IRON, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_IRON_2 = register("dragonarmor_iron_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.IRON, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_IRON_3 = register("dragonarmor_iron_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.IRON, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_COPPER_0 = register("dragonarmor_copper_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.COPPER, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_COPPER_1 = register("dragonarmor_copper_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.COPPER, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_COPPER_2 = register("dragonarmor_copper_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.COPPER, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_COPPER_3 = register("dragonarmor_copper_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.COPPER, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_GOLD_0 = register("dragonarmor_gold_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.GOLD, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_GOLD_1 = register("dragonarmor_gold_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.GOLD, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_GOLD_2 = register("dragonarmor_gold_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.GOLD, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_GOLD_3 = register("dragonarmor_gold_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.GOLD, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DIAMOND_0 = register("dragonarmor_diamond_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.DIAMOND, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DIAMOND_1 = register("dragonarmor_diamond_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.DIAMOND, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DIAMOND_2 = register("dragonarmor_diamond_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.DIAMOND, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DIAMOND_3 = register("dragonarmor_diamond_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.DIAMOND, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_SILVER_0 = register("dragonarmor_silver_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.SILVER, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_SILVER_1 = register("dragonarmor_silver_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.SILVER, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_SILVER_2 = register("dragonarmor_silver_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.SILVER, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_SILVER_3 = register("dragonarmor_silver_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.SILVER, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_0 = register("dragonarmor_dragonsteel_fire_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.FIRE, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_1 = register("dragonarmor_dragonsteel_fire_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.FIRE, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_2 = register("dragonarmor_dragonsteel_fire_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.FIRE, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_FIRE_3 = register("dragonarmor_dragonsteel_fire_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.FIRE, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_0 = register("dragonarmor_dragonsteel_ice_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.ICE, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_1 = register("dragonarmor_dragonsteel_ice_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.ICE, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_2 = register("dragonarmor_dragonsteel_ice_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.ICE, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_ICE_3 = register("dragonarmor_dragonsteel_ice_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.ICE, 3));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_0 = register("dragonarmor_dragonsteel_lightning_" + ItemDragonArmor.getNameForSlot(0), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.LIGHTNING, 0));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_1 = register("dragonarmor_dragonsteel_lightning_" + ItemDragonArmor.getNameForSlot(1), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.LIGHTNING, 1));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_2 = register("dragonarmor_dragonsteel_lightning_" + ItemDragonArmor.getNameForSlot(2), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.LIGHTNING, 2));
    public static final RegistryObject<ItemDragonArmor> DRAGONARMOR_DRAGONSTEEL_LIGHTNING_3 = register("dragonarmor_dragonsteel_lightning_" + ItemDragonArmor.getNameForSlot(3), () -> new ItemDragonArmor(ItemDragonArmor.DragonArmorType.LIGHTNING, 3));
    public static final RegistryObject<Item> DRAGON_MEAL = register("dragon_meal", ItemGeneric::new);
    public static final RegistryObject<Item> SICKLY_DRAGON_MEAL = register("sickly_dragon_meal", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> CREATIVE_DRAGON_MEAL = register("creative_dragon_meal", () -> new ItemGeneric(2));
    public static final RegistryObject<Item> FIRE_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(0), () -> new ItemDragonFlesh(0));
    public static final RegistryObject<Item> ICE_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(1), () -> new ItemDragonFlesh(1));
    public static final RegistryObject<Item> LIGHTNING_DRAGON_FLESH = register(ItemDragonFlesh.getNameForType(2), () -> new ItemDragonFlesh(2));
    public static final RegistryObject<Item> FIRE_DRAGON_HEART = register("fire_dragon_heart", ItemGeneric::new);
    public static final RegistryObject<Item> ICE_DRAGON_HEART = register("ice_dragon_heart", ItemGeneric::new);
    public static final RegistryObject<Item> LIGHTNING_DRAGON_HEART = register("lightning_dragon_heart", ItemGeneric::new);
    public static final RegistryObject<Item> FIRE_DRAGON_BLOOD = register("fire_dragon_blood", ItemGeneric::new);
    public static final RegistryObject<Item> ICE_DRAGON_BLOOD = register("ice_dragon_blood", ItemGeneric::new);
    public static final RegistryObject<Item> LIGHTNING_DRAGON_BLOOD = register("lightning_dragon_blood", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGON_STAFF = register("dragon_stick", () -> new ItemDragonStaff());
    public static final RegistryObject<Item> DRAGON_HORN = register("dragon_horn", () -> new ItemDragonHorn());
    public static final RegistryObject<Item> DRAGON_FLUTE = register("dragon_flute", () -> new ItemDragonFlute());
    public static final RegistryObject<Item> SUMMONING_CRYSTAL_FIRE = register("summoning_crystal_fire", () -> new ItemSummoningCrystal());
    public static final RegistryObject<Item> SUMMONING_CRYSTAL_ICE = register("summoning_crystal_ice", () -> new ItemSummoningCrystal());
    public static final RegistryObject<Item> SUMMONING_CRYSTAL_LIGHTNING = register("summoning_crystal_lightning", () -> new ItemSummoningCrystal());
    public static final RegistryObject<Item> HIPPOGRYPH_EGG = register("hippogryph_egg", () -> new ItemHippogryphEgg());
    public static final RegistryObject<Item> IRON_HIPPOGRYPH_ARMOR = register("iron_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistryObject<Item> GOLD_HIPPOGRYPH_ARMOR = register("gold_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistryObject<Item> DIAMOND_HIPPOGRYPH_ARMOR = register("diamond_hippogryph_armor", () -> new ItemGeneric(0, 1));
    public static final RegistryObject<Item> HIPPOGRYPH_TALON = register("hippogryph_talon", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> HIPPOGRYPH_SWORD = register("hippogryph_sword", () -> new ItemHippogryphSword());
    public static final RegistryObject<Item> GORGON_HEAD = register("gorgon_head", () -> new ItemGorgonHead());
    public static final RegistryObject<Item> STONE_STATUE = register("stone_statue", () -> new ItemStoneStatue());
    public static final RegistryObject<Item> BLINDFOLD = register("blindfold", () -> new ItemBlindfold());
    public static final RegistryObject<Item> PIXIE_DUST = register("pixie_dust", () -> new ItemPixieDust());
    public static final RegistryObject<Item> PIXIE_WINGS = register("pixie_wings", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> PIXIE_WAND = register("pixie_wand", () -> new ItemPixieWand());
    public static final RegistryObject<Item> AMBROSIA = register("ambrosia", () -> new ItemAmbrosia());
    public static final RegistryObject<Item> CYCLOPS_EYE = register("cyclops_eye", () -> new ItemCyclopsEye());
    public static final RegistryObject<Item> SHEEP_HELMET = register("sheep_helmet", () -> new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> SHEEP_CHESTPLATE = register("sheep_chestplate", () -> new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> SHEEP_LEGGINGS = register("sheep_leggings", () -> new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> SHEEP_BOOTS = register("sheep_boots", () -> new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> SHINY_SCALES = register("shiny_scales", ItemGeneric::new);
    public static final RegistryObject<Item> SIREN_TEAR = register("siren_tear", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> SIREN_FLUTE = register("siren_flute", () -> new ItemSirenFlute());
    public static final RegistryObject<Item> HIPPOCAMPUS_FIN = register("hippocampus_fin", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> HIPPOCAMPUS_SLAPPER = register("hippocampus_slapper", () -> new ItemHippocampusSlapper());
    public static final RegistryObject<Item> EARPLUGS = register("earplugs", () -> new ItemModArmor(EARPLUGS_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DEATH_WORM_CHITIN_YELLOW = register("deathworm_chitin_yellow", ItemGeneric::new);
    public static final RegistryObject<Item> DEATH_WORM_CHITIN_WHITE = register("deathworm_chitin_white", ItemGeneric::new);
    public static final RegistryObject<Item> DEATH_WORM_CHITIN_RED = register("deathworm_chitin_red", ItemGeneric::new);
    public static final RegistryObject<Item> DEATHWORM_YELLOW_HELMET = register("deathworm_yellow_helmet", () -> new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DEATHWORM_YELLOW_CHESTPLATE = register("deathworm_yellow_chestplate", () -> new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DEATHWORM_YELLOW_LEGGINGS = register("deathworm_yellow_leggings", () -> new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DEATHWORM_YELLOW_BOOTS = register("deathworm_yellow_boots", () -> new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> DEATHWORM_WHITE_HELMET = register("deathworm_white_helmet", () -> new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DEATHWORM_WHITE_CHESTPLATE = register("deathworm_white_chestplate", () -> new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DEATHWORM_WHITE_LEGGINGS = register("deathworm_white_leggings", () -> new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DEATHWORM_WHITE_BOOTS = register("deathworm_white_boots", () -> new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> DEATHWORM_RED_HELMET = register("deathworm_red_helmet", () -> new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DEATHWORM_RED_CHESTPLATE = register("deathworm_red_chestplate", () -> new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DEATHWORM_RED_LEGGINGS = register("deathworm_red_leggings", () -> new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DEATHWORM_RED_BOOTS = register("deathworm_red_boots", () -> new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> DEATHWORM_EGG = register("deathworm_egg", () -> new ItemDeathwormEgg(false));
    public static final RegistryObject<Item> DEATHWORM_EGG_GIGANTIC = register("deathworm_egg_giant", () -> new ItemDeathwormEgg(true));
    public static final RegistryObject<Item> DEATHWORM_TOUNGE = register("deathworm_tounge", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> DEATHWORM_GAUNTLET_YELLOW = register("deathworm_gauntlet_yellow", () -> new ItemDeathwormGauntlet());
    public static final RegistryObject<Item> DEATHWORM_GAUNTLET_WHITE = register("deathworm_gauntlet_white", () -> new ItemDeathwormGauntlet());
    public static final RegistryObject<Item> DEATHWORM_GAUNTLET_RED = register("deathworm_gauntlet_red", () -> new ItemDeathwormGauntlet());
    public static final RegistryObject<Item> ROTTEN_EGG = register("rotten_egg", () -> new ItemRottenEgg());
    public static final RegistryObject<Item> COCKATRICE_EYE = register("cockatrice_eye", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> ITEM_COCKATRICE_SCEPTER = register("cockatrice_scepter", () -> new ItemCockatriceScepter());
    public static final RegistryObject<Item> STYMPHALIAN_BIRD_FEATHER = register("stymphalian_bird_feather", ItemGeneric::new);
    public static final RegistryObject<Item> STYMPHALIAN_ARROW = register("stymphalian_arrow", () -> new ItemStymphalianArrow());
    public static final RegistryObject<Item> STYMPHALIAN_FEATHER_BUNDLE = register("stymphalian_feather_bundle", () -> new ItemStymphalianFeatherBundle());
    public static final RegistryObject<Item> STYMPHALIAN_DAGGER = register("stymphalian_bird_dagger", () -> new ItemStymphalianDagger());
    public static final RegistryObject<Item> TROLL_TUSK = register("troll_tusk", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_DESERT_EGG = register("myrmex_desert_egg", () -> new ItemMyrmexEgg(false));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_EGG = register("myrmex_jungle_egg", () -> new ItemMyrmexEgg(true));
    public static final RegistryObject<Item> MYRMEX_DESERT_RESIN = register("myrmex_desert_resin", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_JUNGLE_RESIN = register("myrmex_jungle_resin", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_DESERT_CHITIN = register("myrmex_desert_chitin", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_JUNGLE_CHITIN = register("myrmex_jungle_chitin", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_STINGER = register("myrmex_stinger", ItemGeneric::new);
    public static final RegistryObject<Item> MYRMEX_DESERT_SWORD = register("myrmex_desert_sword", () -> new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_sword")));
    public static final RegistryObject<Item> MYRMEX_DESERT_SWORD_VENOM = register("myrmex_desert_sword_venom", () -> new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_sword_venom")));
    public static final RegistryObject<Item> MYRMEX_DESERT_SHOVEL = register("myrmex_desert_shovel", () -> new ItemModShovel(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_shovel")));
    public static final RegistryObject<Item> MYRMEX_DESERT_PICKAXE = register("myrmex_desert_pickaxe", () -> new ItemModPickaxe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_pickaxe")));
    public static final RegistryObject<Item> MYRMEX_DESERT_AXE = register("myrmex_desert_axe", () -> new ItemModAxe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_axe")));
    public static final RegistryObject<Item> MYRMEX_DESERT_HOE = register("myrmex_desert_hoe", () -> new ItemModHoe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_desert_hoe")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_SWORD = register("myrmex_jungle_sword", () -> new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_sword")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_SWORD_VENOM = register("myrmex_jungle_sword_venom", () -> new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_sword_venom")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_SHOVEL = register("myrmex_jungle_shovel", () -> new ItemModShovel(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_shovel")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_PICKAXE = register("myrmex_jungle_pickaxe", () -> new ItemModPickaxe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_pickaxe")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_AXE = register("myrmex_jungle_axe", () -> new ItemModAxe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_axe")));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_HOE = register("myrmex_jungle_hoe", () -> new ItemModHoe(MYRMEX_CHITIN_TOOL_MATERIAL, toolProperties("myrmex_jungle_hoe")));
    public static final RegistryObject<Item> MYRMEX_DESERT_STAFF = register("myrmex_desert_staff", () -> new ItemMyrmexStaff(false));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_STAFF = register("myrmex_jungle_staff", () -> new ItemMyrmexStaff(true));
    public static final RegistryObject<Item> MYRMEX_DESERT_HELMET = register("myrmex_desert_helmet", () -> new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> MYRMEX_DESERT_CHESTPLATE = register("myrmex_desert_chestplate", () -> new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> MYRMEX_DESERT_LEGGINGS = register("myrmex_desert_leggings", () -> new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> MYRMEX_DESERT_BOOTS = register("myrmex_desert_boots", () -> new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_HELMET = register("myrmex_jungle_helmet", () -> new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_CHESTPLATE = register("myrmex_jungle_chestplate", () -> new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_LEGGINGS = register("myrmex_jungle_leggings", () -> new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_BOOTS = register("myrmex_jungle_boots", () -> new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlot.FEET));
    public static final RegistryObject<Item> MYRMEX_DESERT_SWARM = register("myrmex_desert_swarm", () -> new ItemMyrmexSwarm(false));
    public static final RegistryObject<Item> MYRMEX_JUNGLE_SWARM = register("myrmex_jungle_swarm", () -> new ItemMyrmexSwarm(true));
    public static final RegistryObject<Item> AMPHITHERE_FEATHER = register("amphithere_feather", ItemGeneric::new);
    public static final RegistryObject<Item> AMPHITHERE_ARROW = register("amphithere_arrow", () -> new ItemAmphithereArrow());
    public static final RegistryObject<Item> AMPHITHERE_MACUAHUITL = register("amphithere_macuahuitl", () -> new ItemAmphithereMacuahuitl());
    public static final RegistryObject<Item> SERPENT_FANG = register("sea_serpent_fang", ItemGeneric::new);
    public static final RegistryObject<Item> SEA_SERPENT_ARROW = register("sea_serpent_arrow", () -> new ItemSeaSerpentArrow());
    public static final RegistryObject<Item> TIDE_TRIDENT_INVENTORY = register("tide_trident_inventory", () -> new ItemGeneric(0, true));
    public static final RegistryObject<Item> TIDE_TRIDENT = register("tide_trident", () -> new ItemTideTrident());
    public static final RegistryObject<Item> CHAIN = register("chain", () -> new ItemChain(false));
    public static final RegistryObject<Item> CHAIN_STICKY = register("chain_sticky", () -> new ItemChain(true));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_INGOT = register("dragonsteel_fire_ingot", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_SWORD = register("dragonsteel_fire_sword", () -> new ItemModSword(DRAGONSTEEL_TIER_FIRE, toolProperties("dragonsteel_fire_sword")));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_PICKAXE = register("dragonsteel_fire_pickaxe", () -> new ItemModPickaxe(DRAGONSTEEL_TIER_FIRE, toolProperties("dragonsteel_fire_pickaxe")));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_AXE = register("dragonsteel_fire_axe", () -> new ItemModAxe(DRAGONSTEEL_TIER_FIRE, toolProperties("dragonsteel_fire_axe")));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_SHOVEL = register("dragonsteel_fire_shovel", () -> new ItemModShovel(DRAGONSTEEL_TIER_FIRE, toolProperties("dragonsteel_fire_shovel")));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_HOE = register("dragonsteel_fire_hoe", () -> new ItemModHoe(DRAGONSTEEL_TIER_FIRE, toolProperties("dragonsteel_fire_hoe")));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_HELMET = register("dragonsteel_fire_helmet", () -> new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 0, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_CHESTPLATE = register("dragonsteel_fire_chestplate", () -> new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 1, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_LEGGINGS = register("dragonsteel_fire_leggings", () -> new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 2, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DRAGONSTEEL_FIRE_BOOTS = register("dragonsteel_fire_boots", () -> new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 3, EquipmentSlot.FEET));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_INGOT = register("dragonsteel_ice_ingot", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_SWORD = register("dragonsteel_ice_sword", () -> new ItemModSword(DRAGONSTEEL_TIER_ICE, toolProperties("dragonsteel_ice_sword")));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_PICKAXE = register("dragonsteel_ice_pickaxe", () -> new ItemModPickaxe(DRAGONSTEEL_TIER_ICE, toolProperties("dragonsteel_ice_pickaxe")));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_AXE = register("dragonsteel_ice_axe", () -> new ItemModAxe(DRAGONSTEEL_TIER_ICE, toolProperties("dragonsteel_ice_axe")));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_SHOVEL = register("dragonsteel_ice_shovel", () -> new ItemModShovel(DRAGONSTEEL_TIER_ICE, toolProperties("dragonsteel_ice_shovel")));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_HOE = register("dragonsteel_ice_hoe", () -> new ItemModHoe(DRAGONSTEEL_TIER_ICE, toolProperties("dragonsteel_ice_hoe")));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_HELMET = register("dragonsteel_ice_helmet", () -> new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 0, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_CHESTPLATE = register("dragonsteel_ice_chestplate", () -> new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 1, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_LEGGINGS = register("dragonsteel_ice_leggings", () -> new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 2, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DRAGONSTEEL_ICE_BOOTS = register("dragonsteel_ice_boots", () -> new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 3, EquipmentSlot.FEET));

    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_INGOT = register("dragonsteel_lightning_ingot", ItemGeneric::new);
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_SWORD = register("dragonsteel_lightning_sword", () -> new ItemModSword(DRAGONSTEEL_TIER_LIGHTNING, toolProperties("dragonsteel_lightning_sword")));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_PICKAXE = register("dragonsteel_lightning_pickaxe", () -> new ItemModPickaxe(DRAGONSTEEL_TIER_LIGHTNING, toolProperties("dragonsteel_lightning_pickaxe")));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_AXE = register("dragonsteel_lightning_axe", () -> new ItemModAxe(DRAGONSTEEL_TIER_LIGHTNING, toolProperties("dragonsteel_lightning_axe")));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_SHOVEL = register("dragonsteel_lightning_shovel", () -> new ItemModShovel(DRAGONSTEEL_TIER_LIGHTNING, toolProperties("dragonsteel_lightning_shovel")));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_HOE = register("dragonsteel_lightning_hoe", () -> new ItemModHoe(DRAGONSTEEL_TIER_LIGHTNING, toolProperties("dragonsteel_lightning_hoe")));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_HELMET = register("dragonsteel_lightning_helmet", () -> new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 0, EquipmentSlot.HEAD));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_CHESTPLATE = register("dragonsteel_lightning_chestplate", () -> new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 1, EquipmentSlot.CHEST));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_LEGGINGS = register("dragonsteel_lightning_leggings", () -> new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 2, EquipmentSlot.LEGS));
    public static final RegistryObject<Item> DRAGONSTEEL_LIGHTNING_BOOTS = register("dragonsteel_lightning_boots", () -> new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 3, EquipmentSlot.FEET));


    public static final RegistryObject<Item> WEEZER_BLUE_ALBUM = register("weezer_blue_album", () -> new ItemGeneric(1, true));
    public static final RegistryObject<Item> DRAGON_DEBUG_STICK = register("dragon_debug_stick", () -> new ItemGeneric(1, true));
    public static final RegistryObject<Item> DREAD_SWORD = register("dread_sword", () -> new ItemModSword(DREAD_SWORD_TOOL_MATERIAL, toolProperties("dread_sword")));
    public static final RegistryObject<Item> DREAD_KNIGHT_SWORD = register("dread_knight_sword", () -> new ItemModSword(DREAD_KNIGHT_TOOL_MATERIAL, toolProperties("dread_knight_sword")));
    public static final RegistryObject<Item> LICH_STAFF = register("lich_staff", () -> new ItemLichStaff());
    public static final RegistryObject<Item> DREAD_QUEEN_SWORD = register("dread_queen_sword", () -> new ItemModSword(DRAGONSTEEL_TIER_DREAD_QUEEN, toolProperties("dread_queen_sword")));
    public static final RegistryObject<Item> DREAD_QUEEN_STAFF = register("dread_queen_staff", () -> new ItemDreadQueenStaff());
    public static final RegistryObject<Item> DREAD_SHARD = register("dread_shard", () -> new ItemGeneric(0));
    public static final RegistryObject<Item> DREAD_KEY = register("dread_key", () -> new ItemGeneric(0));
    public static final RegistryObject<Item> HYDRA_FANG = register("hydra_fang", () -> new ItemGeneric(0));
    public static final RegistryObject<Item> HYDRA_HEART = register("hydra_heart", () -> new ItemHydraHeart());
    public static final RegistryObject<Item> HYDRA_ARROW = register("hydra_arrow", () -> new ItemHydraArrow());
    public static final RegistryObject<Item> CANNOLI = register("cannoli", () -> new ItemCannoli());
    public static final RegistryObject<Item> ECTOPLASM = register("ectoplasm", ItemGeneric::new);
    public static final RegistryObject<Item> GHOST_INGOT = register("ghost_ingot", () -> new ItemGeneric(1));
    public static final RegistryObject<Item> GHOST_SWORD = register("ghost_sword", () -> new ItemGhostSword());

    public static final RegistryObject<Item> PATTERN_FIRE = register("banner_pattern_fire", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_ICE = register("banner_pattern_ice", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_LIGHTNING = register("banner_pattern_lightning", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_FIRE_HEAD = register("banner_pattern_fire_head", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_ICE_HEAD = register("banner_pattern_ice_head", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_LIGHTNING_HEAD = register("banner_pattern_lightning_head", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_AMPHITHERE = register("banner_pattern_amphithere", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_BIRD = register("banner_pattern_bird", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_EYE = register("banner_pattern_eye", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_FAE = register("banner_pattern_fae", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_FEATHER = register("banner_pattern_feather", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_GORGON = register("banner_pattern_gorgon", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_HIPPOCAMPUS = register("banner_pattern_hippocampus", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_HIPPOGRYPH_HEAD = register("banner_pattern_hippogryph_head", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_MERMAID = register("banner_pattern_mermaid", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_SEA_SERPENT = register("banner_pattern_sea_serpent", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_TROLL = register("banner_pattern_troll", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_WEEZER = register("banner_pattern_weezer", () -> new Item(unstackable()));
    public static final RegistryObject<Item> PATTERN_DREAD = register("banner_pattern_dread", () -> new Item(unstackable()));

    static {
        EnumDragonArmor.initArmors();
        EnumSeaSerpent.initArmors();
        EnumSkullType.initItems();
        EnumTroll.initArmors();
        IafBlockRegistry.registerBlockItems();
    }

    private static TagKey<Item> toolRepairTag(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("iceandfire", "tool_materials/" + name));
    }

    private static Item.Properties toolProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("iceandfire", name)));
    }

    public static Item.Properties defaultBuilder() {
        // 26.1 fills creative tabs via BuildCreativeModeTabContentsEvent; wiring is pending the main-class migration.
        return id(new Item.Properties());
    }

    public static Item.Properties unstackable() {
        return defaultBuilder().stacksTo(1);
    }

    public static final RegistryObject<Item> SPAWN_EGG_FIRE_DRAGON = spawnEgg("spawn_egg_fire_dragon", IafEntityRegistry.FIRE_DRAGON);
    public static final RegistryObject<Item> SPAWN_EGG_ICE_DRAGON = spawnEgg("spawn_egg_ice_dragon", IafEntityRegistry.ICE_DRAGON);
    public static final RegistryObject<Item> SPAWN_EGG_LIGHTNING_DRAGON = spawnEgg("spawn_egg_lightning_dragon", IafEntityRegistry.LIGHTNING_DRAGON);
    public static final RegistryObject<Item> SPAWN_EGGHIPPOGRYPH = spawnEgg("spawn_egg_hippogryph", IafEntityRegistry.HIPPOGRYPH);
    public static final RegistryObject<Item> SPAWN_EGG_GORGON = spawnEgg("spawn_egg_gorgon", IafEntityRegistry.GORGON);
    public static final RegistryObject<Item> SPAWN_EGG_PIXIE = spawnEgg("spawn_egg_pixie", IafEntityRegistry.PIXIE);
    public static final RegistryObject<Item> SPAWN_EGG_CYCLOPS = spawnEgg("spawn_egg_cyclops", IafEntityRegistry.CYCLOPS);
    public static final RegistryObject<Item> SPAWN_EGG_SIREN = spawnEgg("spawn_egg_siren", IafEntityRegistry.SIREN);
    public static final RegistryObject<Item> SPAWN_EGGHIPPOCAMPUS = spawnEgg("spawn_egg_hippocampus", IafEntityRegistry.HIPPOCAMPUS);
    public static final RegistryObject<Item> SPAWN_EGG_DEATH_WORM = spawnEgg("spawn_egg_death_worm", IafEntityRegistry.DEATH_WORM);
    public static final RegistryObject<Item> SPAWN_EGG_COCKATRICE = spawnEgg("spawn_egg_cockatrice", IafEntityRegistry.COCKATRICE);
    public static final RegistryObject<Item> SPAWN_EGG_STYMPHALIAN_BIRD = spawnEgg("spawn_egg_stymphalian_bird", IafEntityRegistry.STYMPHALIAN_BIRD);
    public static final RegistryObject<Item> SPAWN_EGG_TROLL = spawnEgg("spawn_egg_troll", IafEntityRegistry.TROLL);
    public static final RegistryObject<Item> SPAWN_EGG_MYRMEX_WORKER = spawnEgg("spawn_egg_myrmex_worker", IafEntityRegistry.MYRMEX_WORKER);
    public static final RegistryObject<Item> SPAWN_EGG_MYRMEX_SOLDIER = spawnEgg("spawn_egg_myrmex_soldier", IafEntityRegistry.MYRMEX_SOLDIER);
    public static final RegistryObject<Item> SPAWN_EGG_MYRMEX_SENTINEL = spawnEgg("spawn_egg_myrmex_sentinel", IafEntityRegistry.MYRMEX_SENTINEL);
    public static final RegistryObject<Item> SPAWN_EGG_MYRMEX_ROYAL = spawnEgg("spawn_egg_myrmex_royal", IafEntityRegistry.MYRMEX_ROYAL);
    public static final RegistryObject<Item> SPAWN_EGG_MYRMEX_QUEEN = spawnEgg("spawn_egg_myrmex_queen", IafEntityRegistry.MYRMEX_QUEEN);
    public static final RegistryObject<Item> SPAWN_EGG_AMPHITHERE = spawnEgg("spawn_egg_amphithere", IafEntityRegistry.AMPHITHERE);
    public static final RegistryObject<Item> SPAWN_EGG_SEA_SERPENT = spawnEgg("spawn_egg_sea_serpent", IafEntityRegistry.SEA_SERPENT);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_THRALL = spawnEgg("spawn_egg_dread_thrall", IafEntityRegistry.DREAD_THRALL);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_GHOUL = spawnEgg("spawn_egg_dread_ghoul", IafEntityRegistry.DREAD_GHOUL);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_BEAST = spawnEgg("spawn_egg_dread_beast", IafEntityRegistry.DREAD_BEAST);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_SCUTTLER = spawnEgg("spawn_egg_dread_scuttler", IafEntityRegistry.DREAD_SCUTTLER);
    public static final RegistryObject<Item> SPAWN_EGG_LICH = spawnEgg("spawn_egg_lich", IafEntityRegistry.DREAD_LICH);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_KNIGHT = spawnEgg("spawn_egg_dread_knight", IafEntityRegistry.DREAD_KNIGHT);
    public static final RegistryObject<Item> SPAWN_EGG_DREAD_HORSE = spawnEgg("spawn_egg_dread_horse", IafEntityRegistry.DREAD_HORSE);
    public static final RegistryObject<Item> SPAWN_EGG_HYDRA = spawnEgg("spawn_egg_hydra", IafEntityRegistry.HYDRA);
    public static final RegistryObject<Item> SPAWN_EGG_GHOST = spawnEgg("spawn_egg_ghost", IafEntityRegistry.GHOST);

    /** 26.1 vanilla {@code SpawnEggItem.getType} reads {@code DataComponents.ENTITY_DATA}. */
    private static RegistryObject<Item> spawnEgg(String name, RegistryObject<? extends EntityType<?>> type) {
        return register(name, () -> new IafSpawnEggItem(type, defaultBuilder().stacksTo(64).spawnEgg(type.get())));
    }

    /**
     Set repair materials etc.
    */
    /** Runs after item registration; invocation from the mod setup is pending its migration. */
    public static void setRepairMaterials() {

        IafItemRegistry.BLINDFOLD_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Items.STRING));
        IafItemRegistry.SILVER_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.SILVER_INGOT.get()));

        for (EnumDragonArmor armor : EnumDragonArmor.values()) {
            armor.armorMaterial.setRepairMaterial(Ingredient.of(EnumDragonArmor.getScaleItem(armor)));
        }
        IafItemRegistry.DRAGONSTEEL_FIRE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DRAGONSTEEL_FIRE_INGOT.get()));
        IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DRAGONSTEEL_ICE_INGOT.get()));
        IafItemRegistry.DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DRAGONSTEEL_LIGHTNING_INGOT.get()));
        IafItemRegistry.SHEEP_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Blocks.WHITE_WOOL));
        IafItemRegistry.EARPLUGS_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(Blocks.OAK_BUTTON));
        IafItemRegistry.DEATHWORM_0_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DEATH_WORM_CHITIN_YELLOW.get()));
        IafItemRegistry.DEATHWORM_1_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DEATH_WORM_CHITIN_RED.get()));
        IafItemRegistry.DEATHWORM_2_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.DEATH_WORM_CHITIN_WHITE.get()));

        IafItemRegistry.TROLL_MOUNTAIN_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EnumTroll.MOUNTAIN.leather.get()));
        IafItemRegistry.TROLL_FOREST_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EnumTroll.FOREST.leather.get()));
        IafItemRegistry.TROLL_FROST_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(EnumTroll.FROST.leather.get()));

        IafItemRegistry.MYRMEX_DESERT_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.MYRMEX_DESERT_CHITIN.get()));
        IafItemRegistry.MYRMEX_JUNGLE_ARMOR_MATERIAL.setRepairMaterial(Ingredient.of(IafItemRegistry.MYRMEX_JUNGLE_CHITIN.get()));


        for (EnumSeaSerpent serpent : EnumSeaSerpent.values()) {
            serpent.armorMaterial.setRepairMaterial(Ingredient.of(serpent.scale.get()));
        }
    }
}
