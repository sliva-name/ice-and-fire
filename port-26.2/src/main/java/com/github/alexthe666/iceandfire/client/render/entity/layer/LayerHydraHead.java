package com.github.alexthe666.iceandfire.client.render.entity.layer;


import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.model.ModelHydraHead;
import com.github.alexthe666.iceandfire.client.render.entity.HydraRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHydra;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class LayerHydraHead extends RenderLayer<HydraRenderState, EntityModel<HydraRenderState>> {
    public static final Identifier TEXTURE_STONE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hydra/stone.png");
    private static final float[][] TRANSLATE = new float[][]{
        {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
        {-0.15F, 0.15F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
        {-0.3F, 0F, 0.3F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
        {-0.4F, -0.1F, 0.1F, 0.4F, 0F, 0F, 0F, 0F, 0F},//etc...
        {-0.5F, -0.2F, 0F, 0.2F, 0.5F, 0F, 0F, 0F, 0F},
        {-0.7F, -0.4F, -0.2F, 0.2F, 0.4F, 0.7F, 0F, 0F, 0F},
        {-0.7F, -0.4F, -0.2F, 0, 0.2F, 0.4F, 0.7F, 0F, 0F},
        {-0.6F, -0.4F, -0.2F, -0.1F, 0.1F, 0.2F, 0.4F, 0.6F, 0F},
        {-0.6F, -0.4F, -0.2F, -0.1F, 0.0F, 0.1F, 0.2F, 0.4F, 0.6F},
    };
    private static final float[][] ROTATE = new float[][]{
            {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
            {10F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
            {10F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
            {25F, 10F, -10F, -25F, 0F, 0F, 0F, 0F, 0F},//etc...
            {30F, 15F, 0F, -15F, -30F, 0F, 0F, 0F, 0F},
            {40F, 25F, 5F, -5F, -25F, -40F, 0F, 0F, 0F},
            {40F, 30F, 15F, 0F, -15F, -30F, -40F, 0F, 0F},
            {45F, 30F, 20F, 5F, -5F, -20F, -30F, -45F, 0F},
            {50F, 37F, 25F, 15F, 0, -15F, -25F, -37F, -50F},
    };
    private final ModelHydraBody body;
    private final List<EntityModel<HydraRenderState>> headModels = new ArrayList<>();

    public LayerHydraHead(RenderHydra renderer) {
        super(renderer);
        this.body = renderer.getHydraModel();
        for (int i = 0; i < HydraRenderState.MAX_HEADS; i++) {
            // Each deferred submission must keep its own head index; never mutate one shared index.
            headModels.add(new ModelHydraHead(i).asEntityModel());
        }
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, HydraRenderState state, float yRot, float xRot) {
        if (state.isInvisible) {
            return;
        }
        int heads = Mth.clamp(state.headCount, 1, HydraRenderState.MAX_HEADS);
        RenderType type = RenderTypes.entityCutout(getHeadTexture(state));
        // Evaluate explicitly: attachments must not depend on when queued body geometry is drawn.
        body.setupAnim(state);
        poseStack.pushPose();
        try {
            translateToBody(body, poseStack);
            for (int head = 0; head < heads; head++) {
                poseStack.pushPose();
                try {
                    translateHead(poseStack, heads, head);
                    EntityModel<HydraRenderState> model = headModels.get(head);
                    collector.submitModel(model, state, poseStack, type, lightCoords,
                        LivingEntityRenderer.getOverlayCoords(state, 0.0F), state.outlineColor, null);

                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            poseStack.popPose();
        }
    }

    /**
     * Statue overlay: pose the supplied body for neck attachments, then submit isolated head trees.
     * The body mesh itself is drawn separately at rest, matching the 1.18 two-step statue call.
     */
    public static void submitStatueHeads(ModelHydraBody body, HydraRenderState state, PoseStack poses,
                                         SubmitNodeCollector collector, int lightCoords, int outlineColor) {
        state.stone = true;
        body.setupAnim(state);
        RenderType type = RenderTypes.entityCutout(getHeadTexture(state));
        int heads = Mth.clamp(state.headCount, 1, HydraRenderState.MAX_HEADS);
        poses.pushPose();
        try {
            translateToBody(body, poses);
            for (int head = 0; head < heads; head++) {
                poses.pushPose();
                try {
                    translateHead(poses, heads, head);
                    ModelHydraHead headModel = ImmediateModels.HEADS[head];
                    headModel.setupAnim(state);
                    collector.submitModelPart(headModel.asEntityModel().root(), poses, type, lightCoords,
                        LivingEntityRenderer.getOverlayCoords(state, 0.0F), null, -1, null, outlineColor);
                } finally {
                    poses.popPose();
                }
            }
        } finally {
            poses.popPose();
        }
    }

    /**
     * Immediate-mode compatibility for the unported statue caller. This draws real geometry using
     * 26.1 buffers; it does not make RenderStoneStatue's native-model casts adapter-aware.
     */
    @Deprecated
    public static void renderHydraHeads(ModelHydraBody model, boolean stone, PoseStack poseStack, SubmitNodeCollector buffers, int lightCoords, EntityHydra hydra, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        HydraRenderState state = new HydraRenderState();
        RenderHydra.extractHydraState(hydra, state, partialTicks);
        state.stone |= stone;
        state.walkAnimationPos = limbSwing;
        state.walkAnimationSpeed = limbSwingAmount;
        state.ageInTicks = ageInTicks;
        state.yRot = netHeadYaw;
        state.xRot = headPitch;
        state.hasRedOverlay = !state.stone && (hydra.hurtTime > 0 || hydra.deathTime > 0);
        model.setupAnim(state);
        RenderType type = RenderTypes.entityCutout(getHeadTexture(state));
        poseStack.pushPose();
        try {
            translateToBody(model, poseStack);
            for (int head = 0; head < state.headCount; head++) {
                poseStack.pushPose();
                try {
                    translateHead(poseStack, state.headCount, head);
                    ModelHydraHead headModel = ImmediateModels.HEADS[head];
                    headModel.setupAnim(state);
                    buffers.submitModelPart(headModel.asEntityModel().root(), poseStack, type, lightCoords,
                        LivingEntityRenderer.getOverlayCoords(state, 0.0F), null);

                } finally {
                    poseStack.popPose();
                }
            }
        } finally {
            poseStack.popPose();
        }
    }

    // Kept separate from deferred models, so the legacy path cannot overwrite their poses.
    private static class ImmediateModels {
        private static final ModelHydraHead[] HEADS = new ModelHydraHead[HydraRenderState.MAX_HEADS];

        static {
            for (int i = 0; i < HEADS.length; i++) {
                HEADS[i] = new ModelHydraHead(i);
            }
        }
    }

    public static Identifier getHeadTexture(HydraRenderState state) {
        if (state.stone) {
            return TEXTURE_STONE;
        }
        return switch (state.variant) {
            case 1 -> RenderHydra.TEXUTURE_1;
            case 2 -> RenderHydra.TEXUTURE_2;
            default -> RenderHydra.TEXUTURE_0;
        };
    }

    private static void translateHead(PoseStack stack, int heads, int head) {
        stack.translate(TRANSLATE[heads - 1][head] * 0.5F, 0, 0);
        stack.mulPose(Axis.YP.rotationDegrees(ROTATE[heads - 1][head]));
    }

    protected static void translateToBody(ModelHydraBody model, PoseStack stack) {
        // Preserve the authored head placement: the legacy renderer used rotation Z here,
        // not the body's pivot Z. Changing it moves every neck relative to the body.
        var part = model.BodyUpper;
        stack.translate(part.rotationPointX / 16F, part.rotationPointY / 16F, part.rotateAngleZ / 16F);
        if (part.rotateAngleZ != 0) stack.mulPose(Axis.ZP.rotation(part.rotateAngleZ));
        if (part.rotateAngleY != 0) stack.mulPose(Axis.YP.rotation(part.rotateAngleY));
        if (part.rotateAngleX != 0) stack.mulPose(Axis.XP.rotation(part.rotateAngleX));
    }
}