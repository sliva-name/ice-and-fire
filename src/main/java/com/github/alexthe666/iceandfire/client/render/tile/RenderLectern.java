package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockLectern;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderLectern<T extends TileEntityLectern> implements BlockEntityRenderer<T, RenderLectern.LecternBookRenderState> {
    private static final RenderType ENCHANTMENT_TABLE_BOOK_TEXTURE = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath("iceandfire", "textures/models/lectern_book.png"));
    private final BookModel bookModel;

    public RenderLectern(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    public static class LecternBookRenderState extends BlockEntityRenderState {
        public float rotation;
        public BookModel.State book = BookModel.State.forAnimation(0, 0, 0, 1.29F);
    }

    @Override
    public LecternBookRenderState createRenderState() {
        return new LecternBookRenderState();
    }

    @Override
    public void extractRenderState(T lectern, LecternBookRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(lectern, state, partialTicks, cameraPosition, breakProgress);
        state.rotation = switch (lectern.getBlockState().getValue(BlockLectern.FACING)) {
            case EAST -> 90;
            case WEST -> -90;
            case SOUTH -> 0;
            default -> 180;
        };
        float pageFlip = Mth.lerp(partialTicks, lectern.pageFlipPrev, lectern.pageFlip);
        float page1 = pageFlip + 0.25F;
        float page2 = pageFlip + 0.75F;
        page1 = Mth.clamp((page1 - Mth.floor(page1)) * 1.6F - 0.3F, 0.0F, 1.0F);
        page2 = Mth.clamp((page2 - Mth.floor(page2)) * 1.6F - 0.3F, 0.0F, 1.0F);
        state.book = BookModel.State.forAnimation(partialTicks, page1, page2, 1.29F);
    }

    @Override
    public void submit(LecternBookRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        poses.translate(0.5F, 1.1F, 0.5F);
        poses.scale(0.8F, 0.8F, 0.8F);
        poses.mulPose(Axis.YP.rotationDegrees(state.rotation));
        poses.mulPose(Axis.XP.rotationDegrees(112));
        poses.mulPose(Axis.YP.rotationDegrees(90));
        collector.submitModel(bookModel, state.book, poses, ENCHANTMENT_TABLE_BOOK_TEXTURE,
            state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poses.popPose();
    }
}
