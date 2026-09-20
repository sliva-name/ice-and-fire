package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.github.alexthe666.iceandfire.client.texture.ArrayLayeredTexture;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LayerDragonArmor extends RenderLayer<DragonRenderState, EntityModel<DragonRenderState>> {
    private static final Map<String, Identifier> CACHE = new HashMap<>();
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public LayerDragonArmor(RenderDragonBase renderer, int type) { super(renderer); }
    public static void clearCache(String key) { CACHE.remove(key); }

    public static Identifier extractTexture(EntityDragonBase dragon, int type) {
        String key = dragon.dragonType.getName();
        boolean equipped = false;
        var textures = new ArrayList<String>();
        for (EquipmentSlot slot : SLOTS) {
            int ordinal = dragon.getArmorOrdinal(dragon.getItemBySlot(slot));
            key += "_" + ordinal;
            equipped |= ordinal != 0;
            var armor = EnumDragonTextures.Armor.getArmorForDragon(dragon, slot);
            boolean blackFrost = dragon instanceof EntityIceDragon ice && ice.isBlackFrost();
            textures.add((type == 0 || blackFrost ? armor.FIRETEXTURE : type == 1 ? armor.ICETEXTURE : armor.LIGHTNINGTEXTURE).toString());
        }
        if (!equipped) return null;
        return CACHE.computeIfAbsent(key, name -> {
            Identifier id = Identifier.fromNamespaceAndPath("iceandfire", "dragon_armor_" + name);
            Minecraft.getInstance().getTextureManager().registerAndLoad(id, new ArrayLayeredTexture(textures));
            return id;
        });
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, DragonRenderState state, float yaw, float pitch) {
        if (state.armorTexture != null) {
            collector.order(1).submitModel(getParentModel(), state, poses, RenderTypes.entityCutoutCull(state.armorTexture),
                light, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
        }
    }
}
