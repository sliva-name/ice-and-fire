package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.model.ModelBipedBase;
import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LayerBipedArmorMultiple<M extends EntityModel<DreadHumanoidRenderState>> extends LayerBipedArmor<M> {
    private final IHasArmorVariantResource resources;

    public LayerBipedArmorMultiple(RenderLayerParent<DreadHumanoidRenderState, M> renderer,
                                   IHasArmorVariantResource resources, ModelBipedBase body,
                                   ModelBipedBase modelLeggings, ModelBipedBase modelChest,
                                   ModelBipedBase modelFeet, ModelBipedBase modelHead,
                                   Identifier defaultArmor, Identifier defaultLegArmor) {
        super(renderer, body, modelLeggings, modelChest, modelFeet, modelHead, defaultArmor, defaultLegArmor);
        this.resources = resources;
    }

    @Override
    public Identifier getArmorResource(DreadHumanoidRenderState state, ItemStack stack, EquipmentSlot slot, String type) {
        return this.resources.getArmorResource(state.armorVariant, slot);
    }
}
