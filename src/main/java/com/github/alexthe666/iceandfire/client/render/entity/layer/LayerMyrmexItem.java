package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerMyrmexItem extends RenderLayer<MyrmexRenderState, EntityModel<MyrmexRenderState>> {

    protected final RenderMyrmexBase livingEntityRenderer;

    public LayerMyrmexItem(RenderMyrmexBase livingEntityRenderer) {
        super(livingEntityRenderer);
        this.livingEntityRenderer = livingEntityRenderer;
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector collector, int lightCoords, MyrmexRenderState state, float yRot, float xRot) {
        if (state.caste != MyrmexRenderState.Caste.WORKER || state.growthStage < 2 || state.mouthItem.isEmpty()) {
            return;
        }
        stack.pushPose();
        if (state.shiftKeyDown) {
            stack.translate(0.0F, 0.2F, 0.0F);
        }
        // LivingEntityRenderer poses the selected adapter before submitting layers.
        // Use the adult Citadel head chain, never cast a native adapter or a juvenile model.
        livingEntityRenderer.getAdultModel().postRenderArm(0.0F, stack);
        stack.translate(0.0F, 0.3F, -1.6F);
        if (state.mouthItemIsBlock) {
            stack.translate(0.0F, 0.0F, 0.2F);
        } else {
            stack.translate(0.0F, 0.2F, 0.3F);
        }
        stack.mulPose(Axis.XP.rotationDegrees(160.0F));
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        state.mouthItem.submit(stack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        stack.popPose();
    }
}
