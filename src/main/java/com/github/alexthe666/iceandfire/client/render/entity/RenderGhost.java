package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.entity.EntityGhost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.EntityModel;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.client.event.ForgeEventFactoryClient;

public class RenderGhost extends MobRenderer<EntityGhost, GhostRenderState, EntityModel<GhostRenderState>> {
    public static final Identifier TEXTURE_0 = texture("ghost_white");
    public static final Identifier TEXTURE_1 = texture("ghost_blue");
    public static final Identifier TEXTURE_2 = texture("ghost_green");
    public static final Identifier TEXTURE_SHOPPING_LIST = texture("haunted_shopping_list");


    public RenderGhost(EntityRendererProvider.Context context) {
        super(context, new ModelGhost(0.0F).asEntityModel(), 0);
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/ghost/" + name + ".png");
    }


    @Override
    public GhostRenderState createRenderState() {
        return new GhostRenderState();
    }

    @Override
    public void extractRenderState(EntityGhost entity, GhostRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var animation = entity.getAnimation();
        state.animation = animation == EntityGhost.ANIMATION_SCARE ? GhostRenderState.SCARE
            : animation == EntityGhost.ANIMATION_HIT ? GhostRenderState.HIT : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.color = entity.getColor();
        state.daytime = entity.isDaytimeMode();
        state.shoppingList = entity.isHauntedShoppingList();
        state.spectator = entity.isSpectator();
        state.alpha = getAlphaForRender(entity, partialTick);
    }

    public static Identifier getGhostOverlayForType(int ghost) {
        return switch (ghost) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case -1 -> TEXTURE_SHOPPING_LIST;
            default -> TEXTURE_0;
        };
    }

    @Override
    public Identifier getTextureLocation(GhostRenderState state) {
        return getGhostOverlayForType(state.color);
    }

    @Override
    protected float getFlipDegrees() {
        return 0;
    }

    public float getAlphaForRender(EntityGhost entity, float partialTick) {
        if (entity.isDaytimeMode()) {
            return Mth.clamp((101 - Math.min(entity.getDaytimeCounter(), 100)) / 100F, 0, 1);
        }
        return Mth.clamp((Mth.sin((entity.tickCount + partialTick) * 0.1F) + 1F) * 0.5F + 0.1F, 0F, 1F);
    }

    @Override
    public void submit(GhostRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (ForgeEventFactoryClient.onRenderLivingPre(state, this, poses, collector, camera)) return;
        poses.pushPose();
        if (state.hasPose(Pose.SLEEPING)) {
            Direction direction = state.bedOrientation;
            if (direction != null) {
                float offset = state.eyeHeight - 0.1F;
                poses.translate(-direction.getStepX() * offset, 0, -direction.getStepZ() * offset);
            }
        }
        poses.scale(state.scale, state.scale, state.scale);
        setupRotations(state, poses, state.bodyRot, state.scale);
        poses.scale(-1, -1, 1);
        scale(state, poses);
        poses.translate(0, -1.501F, 0);
        if (!state.isInvisible) {
            RenderType type = state.daytime ? IafRenderType.getGhostDaytime(getTextureLocation(state))
                : IafRenderType.getGhost(getTextureLocation(state));
            int overlay = getOverlayCoords(state, getWhiteOverlayProgress(state));
            int color = ((int) (state.alpha * 255) << 24) | 0xFFFFFF;
            if (state.shoppingList) {
                poses.pushPose();
                poses.translate(0, 0.8F + Mth.sin(state.ageInTicks * 0.15F) * 0.1F, 0);
                poses.scale(0.6F, 0.6F, 0.6F);
                poses.mulPose(Axis.YP.rotationDegrees(180));
                collector.submitCustomGeometry(poses, type, (pose, buffer) -> drawPage(pose, buffer, overlay, color, 1F));
                poses.mulPose(Axis.YP.rotationDegrees(180));
                collector.submitCustomGeometry(poses, type, (pose, buffer) -> drawPage(pose, buffer, overlay, color, 0F));
                poses.popPose();
            } else {
                collector.submitModel(model, state, poses, type, 240, overlay, color, null, state.outlineColor, null);
            }
        }
        if (!state.spectator && !layers.isEmpty()) {
            model.setupAnim(state);
            for (RenderLayer<GhostRenderState, EntityModel<GhostRenderState>> layer : layers) {
                layer.submit(poses, collector, state.lightCoords, state, state.yRot, state.xRot);
            }
        }
        poses.popPose();
        submitNameDisplay(state, poses, collector, camera);
        ForgeEventFactoryClient.onRenderLivingPost(state, this, poses, collector, camera);
    }

    private static void drawPage(PoseStack.Pose pose, VertexConsumer buffer, int overlay, int color, float leftU) {
        drawVertex(pose, buffer, overlay, color, -1, -2, leftU, 0);
        drawVertex(pose, buffer, overlay, color, 1, -2, 0.5F, 0);
        drawVertex(pose, buffer, overlay, color, 1, 2, 0.5F, 1);
        drawVertex(pose, buffer, overlay, color, -1, 2, leftU, 1);
    }

    private static void drawVertex(PoseStack.Pose pose, VertexConsumer buffer, int overlay, int color, int x, int y, float u, float v) {
        buffer.addVertex(pose, x, y, 0).setColor(color).setUv(u, v).setOverlay(overlay).setLight(240).setNormal(pose, 0, 0, 1);
    }
}
