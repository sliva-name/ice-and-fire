package com.github.alexthe666.iceandfire.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public class DragonSteelTier {

    public static final TagKey<Block> DRAGONSTEEL_TIER_TAG = BlockTags.create(Identifier.parse("iceandfire:needs_dragonsteel"));
    public static final TagKey<Block> INCORRECT_FOR_DRAGONSTEEL_TOOL = BlockTags.create(Identifier.parse("iceandfire:incorrect_for_dragonsteel_tool"));
    public static final ToolMaterial DRAGONSTEEL_TIER_FIRE = createTier("dragonsteel_fire");
    public static final ToolMaterial DRAGONSTEEL_TIER_ICE = createTier("dragonsteel_ice");
    public static final ToolMaterial DRAGONSTEEL_TIER_LIGHTNING = createTier("dragonsteel_lightning");
    public static final ToolMaterial DRAGONSTEEL_TIER_DREAD_QUEEN = createTier("dread_queen");

    private static ToolMaterial createTier(String repairMaterial) {
        return new ToolMaterial(INCORRECT_FOR_DRAGONSTEEL_TOOL, 8000, 10.0F, 21.0F, 10,
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("iceandfire", "tool_materials/" + repairMaterial)));
    }

    public static boolean isDragonsteel(ToolMaterial material) {
        return material.incorrectBlocksForDrops().equals(INCORRECT_FOR_DRAGONSTEEL_TOOL);
    }
}
