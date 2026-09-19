package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.entity.EntityGhostSword;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderGhostSword extends EntityRenderer<EntityGhostSword, GhostSwordRenderState> {
    private final ItemModelResolver itemModelResolver;

    public RenderGhostSword(EntityRendererProvider.Context context) {
        super(context);
        itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public GhostSwordRenderState createRenderState() {
        return new GhostSwordRenderState();
    }

    @Override
    public void extractRenderState(EntityGhostSword entity, GhostSwordRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        itemModelResolver.updateForNonLiving(state.item, new ItemStack(IafItemRegistry.GHOST_SWORD.get()), ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void submit(GhostSwordRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poses.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        poses.translate(0, 0.5F, 0);
        poses.scale(2F, 2F, 2F);
        poses.mulPose(Axis.ZN.rotationDegrees(state.ageInTicks * 30F));
        poses.translate(0, -0.15F, 0);
        state.item.submit(poses, collector, 240, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poses.popPose();
    }
}
