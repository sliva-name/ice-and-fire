package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.RenderTroll;
import com.github.alexthe666.iceandfire.client.render.entity.TrollRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerTrollEyes extends RenderLayer<TrollRenderState, EntityModel<TrollRenderState>> {
    public LayerTrollEyes(RenderTroll renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light,
                       TrollRenderState state, float yRot, float xRot) {
        if (state.hasEyes()) {
            collector.order(1).submitModel(getParentModel(), state, poses,
                RenderTypes.eyes(state.variant.eyesTexture), light, OverlayTexture.NO_OVERLAY,
                -1, null, state.outlineColor, null);
        }
    }
}
