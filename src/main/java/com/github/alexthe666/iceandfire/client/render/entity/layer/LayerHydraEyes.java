package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.HydraRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHydra;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

/** Body emissive pass, matching the original generic glowing layer. */
public class LayerHydraEyes extends RenderLayer<HydraRenderState, EntityModel<HydraRenderState>> {
    public LayerHydraEyes(RenderHydra renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, HydraRenderState state, float yRot, float xRot) {
        if (!state.stone && !state.isInvisible) {
            collector.order(1).submitModel(this.getParentModel(), state, poseStack, RenderTypes.eyes(RenderHydra.TEXUTURE_EYES),
                lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
