package com.github.alexthe666.iceandfire.client.model.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.render.entity.RenderTideTridentItem;
import com.github.alexthe666.iceandfire.client.render.tile.IceAndFireTEISR;
import com.github.alexthe666.iceandfire.client.render.tile.RenderDeathWormGauntlet;
import com.github.alexthe666.iceandfire.client.render.tile.RenderGorgonHead;
import com.github.alexthe666.iceandfire.client.render.tile.RenderTrollWeapon;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;

/**
 * Registers the client item-model extensions referenced from {@code assets/iceandfire/items/*.json}.
 * <p>
 * 26.1 items render through item model definitions instead of 1.18 {@code ItemProperties} /
 * {@code IItemRenderProperties}. Vanilla keeps the codec id maps private and Forge 26.1 has no
 * registration event for them, so the maps are opened by {@code META-INF/accesstransformer.cfg}.
 * Must run during mod construction: the first resource reload parses item definitions before
 * {@code FMLClientSetupEvent}.
 */
public final class IafItemModelRegistry {
    private static boolean registered;

    private IafItemModelRegistry() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        // Special renderers: 1.18 builtin/entity items (BEWLR / TEISR).
        SpecialModelRenderers.ID_MAPPER.put(id("block_item"), IceAndFireTEISR.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(id("gorgon_head"), RenderGorgonHead.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(id("troll_weapon"), RenderTrollWeapon.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(id("deathworm_gauntlet"), RenderDeathWormGauntlet.Unbaked.MAP_CODEC);
        SpecialModelRenderers.ID_MAPPER.put(id("tide_trident"), RenderTideTridentItem.Unbaked.MAP_CODEC);
        // Item model properties: 1.18 ItemProperties predicates.
        RangeSelectItemModelProperties.ID_MAPPER.put(id("dragon_horn_type"), DragonHornTypeProperty.MAP_CODEC);
        ConditionalItemModelProperties.ID_MAPPER.put(id("has_dragon"), HasDragonProperty.MAP_CODEC);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(IceAndFire.MODID, path);
    }
}
