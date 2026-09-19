package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.GorgonRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGorgon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class LayerGorgonEyes extends RenderLayer<GorgonRenderState, EntityModel<GorgonRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/gorgon_eyes.png");

    public LayerGorgonEyes(RenderGorgon render) {
        super(render);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, GorgonRenderState state, float yRot, float xRot) {
        if (state.scaring || state.hitting) {
            collector.order(1).submitModel(this.getParentModel(), state, poseStack, RenderTypes.eyes(TEXTURE),
                lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
