package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.RenderTroll;
import com.github.alexthe666.iceandfire.client.render.entity.TrollRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerTrollWeapon extends RenderLayer<TrollRenderState, EntityModel<TrollRenderState>> {
    public LayerTrollWeapon(RenderTroll renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light,
                       TrollRenderState state, float yRot, float xRot) {
        if (state.hasWeapon()) {
            // All weapon shapes share the rig; each original atlas masks the unused shapes.
            collector.order(1).submitModel(getParentModel(), state, poses,
                RenderTypes.entityCutout(state.weapon.texture), light, OverlayTexture.NO_OVERLAY,
                -1, null, state.outlineColor, null);
        }
    }
}
