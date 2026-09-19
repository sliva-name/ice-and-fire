package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerPixieItem extends RenderLayer<PixieRenderState, EntityModel<PixieRenderState>> {
    public LayerPixieItem(RenderPixie renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack stack, SubmitNodeCollector collector, int lightCoords, PixieRenderState state, float yRot, float xRot) {
        if (state.heldItem.isEmpty()) {
            return;
        }
        stack.pushPose();
        // Both block and non-block items used these same offsets in the original layer.
        stack.translate(-0.0625F, 0.53125F, 0.21875F);
        stack.translate(-0.075F, 0, -0.05F);
        stack.translate(0.05F, 0.55F, -0.4F);
        stack.mulPose(Axis.XP.rotationDegrees(200));
        stack.mulPose(Axis.YP.rotationDegrees(180));
        state.heldItem.submit(stack, collector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        stack.popPose();
    }
}
