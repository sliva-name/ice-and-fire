package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.level.block.Blocks;

public class RenderDragonFireCharge extends EntityRenderer<Fireball, DragonFireChargeRenderState> {
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelResolver blockModelResolver;
    public boolean isFire;

    public RenderDragonFireCharge(EntityRendererProvider.Context context, boolean isFire) {
        super(context);
        this.blockModelResolver = context.getBlockModelResolver();
        this.isFire = isFire;
    }

    @Override
    public DragonFireChargeRenderState createRenderState() {
        return new DragonFireChargeRenderState();
    }

    @Override
    public void extractRenderState(Fireball entity, DragonFireChargeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        blockModelResolver.update(state.block,
            isFire ? Blocks.MAGMA_BLOCK.defaultBlockState() : IafBlockRegistry.DRAGON_ICE.get().defaultBlockState(),
            BLOCK_DISPLAY_CONTEXT);
    }

    @Override
    public void submit(DragonFireChargeRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.translate(0.0D, 0.5D, 0.0D);
        poses.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poses.translate(-0.5D, -0.5D, 0.5D);
        poses.mulPose(Axis.YP.rotationDegrees(90.0F));
        state.block.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poses.popPose();
    }
}
