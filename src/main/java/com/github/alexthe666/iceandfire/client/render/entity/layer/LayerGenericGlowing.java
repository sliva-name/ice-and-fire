package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Full-model emissive pass, matching the original generic glowing layer.
 * Renderers construct it with their own extracted state type.
 */
public class LayerGenericGlowing<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private final Identifier texture;

    public LayerGenericGlowing(RenderLayerParent<S, M> renderIn, Identifier texture) {
        super(renderIn);
        this.texture = texture;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, S state, float yRot, float xRot) {
        if (!state.isInvisible) {
            collector.order(1).submitModel(getParentModel(), state, poseStack, RenderTypes.eyes(texture),
                lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
