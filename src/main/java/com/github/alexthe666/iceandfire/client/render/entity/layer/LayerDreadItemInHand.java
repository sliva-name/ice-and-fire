package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;

public class LayerDreadItemInHand<M extends EntityModel<DreadHumanoidRenderState> & ArmedModel<DreadHumanoidRenderState>>
    extends ItemInHandLayer<DreadHumanoidRenderState, M> {

    public LayerDreadItemInHand(RenderLayerParent<DreadHumanoidRenderState, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light,
                       DreadHumanoidRenderState state, float yRot, float xRot) {
        if (state.hideHeldItems) {
            return;
        }
        super.submit(poses, collector, light, state, yRot, xRot);
    }
}
