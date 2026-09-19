package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Set;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class RenderDreadPortal<T extends TileEntityDreadPortal> implements BlockEntityRenderer<T, EndPortalRenderState> {
    public static final Identifier DREAD_PORTAL_BACKGROUND = Identifier.fromNamespaceAndPath("iceandfire", "textures/environment/dread_portal_background.png");
    public static final Identifier DREAD_PORTAL = Identifier.fromNamespaceAndPath("iceandfire", "textures/environment/dread_portal.png");

    public RenderDreadPortal(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public EndPortalRenderState createRenderState() {
        return new EndPortalRenderState();
    }

    @Override
    public void extractRenderState(T portal, EndPortalRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(portal, state, partialTicks, cameraPosition, breakProgress);
        state.facesToShow.clear();
        for (Direction direction : Direction.values()) {
            if (portal.shouldRenderFace(direction)) {
                state.facesToShow.add(direction);
            }
        }
    }

    @Override
    public void submit(EndPortalRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.facesToShow.isEmpty()) {
            Set<Direction> faces = Set.copyOf(state.facesToShow);
            collector.submitCustomGeometry(poses, renderType(), (pose, consumer) -> renderCube(faces, pose.pose(), consumer));
        }
    }

    private static void renderCube(Set<Direction> faces, Matrix4f matrix, VertexConsumer consumer) {
        renderFace(faces, matrix, consumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
        renderFace(faces, matrix, consumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
        renderFace(faces, matrix, consumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
        renderFace(faces, matrix, consumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
        // Preserve the original portal's two oppositely wound faces at y = 1.
        renderFace(faces, matrix, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
        renderFace(faces, matrix, consumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private static void renderFace(Set<Direction> faces, Matrix4f matrix, VertexConsumer consumer,
                                   float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, Direction direction) {
        if (faces.contains(direction)) {
            consumer.addVertex(matrix, x1, y1, z1).setColor(255, 255, 255, 255);
            consumer.addVertex(matrix, x2, y1, z2).setColor(255, 255, 255, 255);
            consumer.addVertex(matrix, x2, y2, z3).setColor(255, 255, 255, 255);
            consumer.addVertex(matrix, x1, y2, z4).setColor(255, 255, 255, 255);
        }
    }

    protected RenderType renderType() {
        return IafRenderType.getDreadlandsPortal();
    }
}
