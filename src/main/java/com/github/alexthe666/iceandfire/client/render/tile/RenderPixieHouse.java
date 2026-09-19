package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.model.ModelPixieHouse;
import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderPixieHouse<T extends TileEntityPixieHouse> implements BlockEntityRenderer<T, PixieHouseRenderState> {
    private final EntityModel<EntityRenderState> model = new ModelPixieHouse().asEntityModel();
    private final EntityModel<PixieRenderState> pixieModel = new ModelPixie().asEntityModel();

    public RenderPixieHouse(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PixieHouseRenderState createRenderState() {
        return new PixieHouseRenderState();
    }

    @Override
    public void extractRenderState(T house, PixieHouseRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(house, state, partialTicks, cameraPosition, breakProgress);
        // Read the current world block to avoid rendering a stale tile after replacement/destruction.
        var blockState = house.getLevel() == null ? house.getBlockState()
            : house.getLevel().getBlockState(house.getBlockPos());
        state.visible = blockState.getBlock() instanceof BlockPixieHouse && blockState.hasProperty(BlockPixieHouse.FACING);
        state.houseType = state.visible ? TileEntityPixieHouse.getHouseTypeFromBlock(blockState.getBlock()) : 0;
        state.rotation = state.visible ? PixieHouseRenderState.rotationFor(blockState.getValue(BlockPixieHouse.FACING)) : 0;
        state.hasPixie = state.visible && house.getLevel() != null && house.hasPixie;
        state.pixie.setContainedPose(PixieRenderState.Mode.HOUSE, house.pixieType, house.ticksExisted + partialTicks, true);
    }

    @Override
    public void submit(PixieHouseRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) {
            return;
        }
        poses.pushPose();
        poses.translate(0.5F, 1.501F, 0.5F);
        poses.mulPose(Axis.XP.rotationDegrees(180));
        poses.mulPose(Axis.YP.rotationDegrees(state.rotation));
        if (state.hasPixie) {
            poses.pushPose();
            poses.translate(0F, 0.95F, 0F);
            poses.scale(0.55F, 0.55F, 0.55F);
            var texture = PixieRenderState.textureFor(state.pixie.color);
            collector.submitModel(pixieModel, state.pixie, poses, RenderTypes.entityCutout(texture, false),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
            collector.order(1).submitModel(pixieModel, state.pixie, poses, RenderTypes.eyes(texture),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
            poses.popPose();
        }
        collector.submitModel(model, state.house, poses, RenderTypes.entityCutout(PixieHouseRenderState.textureFor(state.houseType), false),
            state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poses.popPose();
    }
}
