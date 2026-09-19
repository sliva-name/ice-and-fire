package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.EggRenderState;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderEggInIce<T extends TileEntityEggInIce> implements BlockEntityRenderer<T, RenderEggInIce.FrozenEggRenderState> {
    private final EntityModel<EggRenderState> model = new ModelDragonEgg().asEntityModel();

    public RenderEggInIce(BlockEntityRendererProvider.Context context) {
    }

    public static class FrozenEggRenderState extends BlockEntityRenderState {
        public final EggRenderState egg = new EggRenderState();
        public @Nullable RenderType renderType;
    }

    @Override
    public FrozenEggRenderState createRenderState() {
        return new FrozenEggRenderState();
    }

    @Override
    public void extractRenderState(T egg, FrozenEggRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(egg, state, partialTicks, cameraPosition, breakProgress);
        state.renderType = egg.type == null ? null : RenderPodium.getEggTexture(egg.type);
        state.egg.inverted = true;
        state.egg.wobbleAmount = 0.1F;
        state.egg.ageInTicks = egg.ticksExisted + partialTicks;
    }

    @Override
    public void submit(FrozenEggRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.renderType == null) {
            return;
        }
        poses.pushPose();
        poses.translate(0.5, -0.8F, 0.5F);
        collector.submitModel(model, state.egg, poses, state.renderType, state.lightCoords,
            OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poses.popPose();
    }
}
