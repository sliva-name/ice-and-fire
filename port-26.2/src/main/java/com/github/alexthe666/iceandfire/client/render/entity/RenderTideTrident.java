package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.ModelTideTrident;
import com.github.alexthe666.iceandfire.entity.EntityTideTrident;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderTideTrident extends EntityRenderer<EntityTideTrident, ThrownTridentRenderState> {
    public static final Identifier TRIDENT = Identifier.parse("iceandfire:textures/models/misc/tide_trident.png");
    private final EntityModel<ThrownTridentRenderState> tridentModel;

    public RenderTideTrident(EntityRendererProvider.Context context) {
        super(context);
        ModelTideTrident source = new ModelTideTrident();
        source.updateDefaultPose();
        tridentModel = new BasicEntityModel<ThrownTridentRenderState>() {
            @Override
            public Iterable<BasicModelPart> parts() {
                return source.parts();
            }

            @Override
            public void setupAnim(ThrownTridentRenderState state) {
                source.resetToDefaultPose();
            }
        }.asEntityModel();
    }

    @Override
    public ThrownTridentRenderState createRenderState() {
        return new ThrownTridentRenderState();
    }

    @Override
    public void extractRenderState(EntityTideTrident entity, ThrownTridentRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.isFoil = entity.isFoil();
    }

    @Override
    public void submit(ThrownTridentRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.mulPose(Axis.YP.rotationDegrees(state.yRot - 90));
        poses.mulPose(Axis.ZP.rotationDegrees(state.xRot + 90));
        collector.order(0).submitModel(tridentModel, state, poses, TRIDENT,
            state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        if (state.isFoil) {
            collector.order(1).submitModel(tridentModel, state, poses,
                RenderTypes.entityGlint(),
                state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
        poses.popPose();
        super.submit(state, poses, collector, camera);
    }
}
