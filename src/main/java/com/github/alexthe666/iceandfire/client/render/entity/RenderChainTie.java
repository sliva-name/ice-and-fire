package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelChainTie;
import com.github.alexthe666.iceandfire.entity.EntityChainTie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RenderChainTie extends EntityRenderer<EntityChainTie, ChainTieRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/chain_tie.png");
    private final EntityModel<ChainTieRenderState> leashKnotModel = new ModelChainTie().asEntityModel();

    public RenderChainTie(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ChainTieRenderState createRenderState() {
        return new ChainTieRenderState();
    }

    @Override
    public void extractRenderState(EntityChainTie entity, ChainTieRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // The knot was always posed with zero head angles, independently of entity rotation.
        state.yRot = 0;
        state.xRot = 0;
    }

    @Override
    public void submit(ChainTieRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.translate(0, 0.5F, 0);
        poses.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(leashKnotModel, state, poses, RenderTypes.entityCutout(TEXTURE),
            state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poses.popPose();
        super.submit(state, poses, collector, camera);
    }
}
