package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerPixieGlow extends RenderLayer<PixieRenderState, EntityModel<PixieRenderState>> {
    public LayerPixieGlow(RenderPixie renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector collector, int lightCoords, PixieRenderState state, float yRot, float xRot) {
        collector.order(1).submitModel(this.getParentModel(), state, stack,
            RenderTypes.eyes(PixieRenderState.textureFor(state.color)), lightCoords,
            OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }
}
