package com.github.alexthe666.iceandfire.client.render.entity.layer;

import net.minecraft.client.renderer.entity.RenderLayerParent;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class LayerSeaSerpentAncient extends RenderLayer<SeaSerpentRenderState, EntityModel<SeaSerpentRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/seaserpent/ancient_overlay.png");
    private static final Identifier TEXTURE_BLINK = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/seaserpent/ancient_overlay_blink.png");

    // Legacy entityNoOutline used translucent blending, light/overlay, no cull and COLOR_WRITE.
    // entityTranslucent(texture, false) is not equivalent: it also writes depth.
    private static final RenderPipeline PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
        .withLocation(Identifier.fromNamespaceAndPath("iceandfire", "pipeline/seaserpent_ancient"))
        .withShaderDefine("ALPHA_CUTOUT", 0.1F)
        .withShaderDefine("PER_FACE_LIGHTING")
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(false)
        .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
        .build();
    private static final RenderType ANCIENT = overlay(TEXTURE);
    private static final RenderType ANCIENT_BLINK = overlay(TEXTURE_BLINK);

    public LayerSeaSerpentAncient(RenderLayerParent<SeaSerpentRenderState, EntityModel<SeaSerpentRenderState>> renderer) {
        super(renderer);
    }

    private static RenderType overlay(Identifier texture) {
        return RenderType.create("iceandfire_seaserpent_ancient", RenderSetup.builder(PIPELINE)
            .withTexture("Sampler0", texture)
            .useLightmap()
            .useOverlay()
            .sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE)
            .createRenderSetup());
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, SeaSerpentRenderState state, float yRot, float xRot) {
        // The original layer rendered ancient markings even when the body was invisible.
        if (state.ancient) {
            collector.order(1).submitModel(getParentModel(), state, poseStack, state.blinking ? ANCIENT_BLINK : ANCIENT,
                lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        }
    }
}
