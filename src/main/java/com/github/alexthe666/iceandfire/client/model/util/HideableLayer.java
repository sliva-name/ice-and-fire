package com.github.alexthe666.iceandfire.client.model.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class HideableLayer<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public boolean hidden;
    private final RenderLayer<S, M> layerRenderer;

    public HideableLayer(RenderLayer<S, M> layerRenderer, RenderLayerParent<S, M> entityRendererIn) {
        super(entityRendererIn);
        hidden = false;
        this.layerRenderer = layerRenderer;
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, S state, float yRot, float xRot) {
        if (!hidden) {
            layerRenderer.submit(poses, collector, light, state, yRot, xRot);
        }
    }
}
