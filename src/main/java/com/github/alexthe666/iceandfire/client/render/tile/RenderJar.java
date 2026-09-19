package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderJar<T extends TileEntityJar> implements BlockEntityRenderer<T, JarRenderState> {
    private final EntityModel<PixieRenderState> model = new ModelPixie().asEntityModel();

    public RenderJar(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public JarRenderState createRenderState() {
        return new JarRenderState();
    }

    @Override
    public void extractRenderState(T jar, JarRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(jar, state, partialTicks, cameraPosition, breakProgress);
        state.hasPixie = jar.getLevel() != null && jar.hasPixie;
        state.rotation = JarRenderState.interpolateRotation(jar.prevRotationYaw, jar.rotationYaw, partialTicks);
        state.pixie.setContainedPose(PixieRenderState.Mode.JAR, jar.pixieType,
            jar.ticksExisted + partialTicks, jar.hasProduced);
    }

    @Override
    public void submit(JarRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.hasPixie) {
            return;
        }
        poses.pushPose();
        poses.translate(0.5F, 1.501F, 0.5F);
        poses.mulPose(Axis.XP.rotationDegrees(180));
        poses.translate(0F, state.pixie.sitting ? 0.90F : 0.60F, 0F);
        poses.mulPose(Axis.YP.rotationDegrees(state.rotation));
        poses.scale(0.50F, 0.50F, 0.50F);
        // The original jar renderer used color 0 for every value outside 1–4, including 5.
        int color = state.pixie.color;
        var texture = PixieRenderState.textureFor(color >= 1 && color <= 4 ? color : 0);
        collector.submitModel(model, state.pixie, poses, RenderTypes.entityCutout(texture, false),
            state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        collector.order(1).submitModel(model, state.pixie, poses, RenderTypes.eyes(texture),
            state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poses.popPose();
    }
}
