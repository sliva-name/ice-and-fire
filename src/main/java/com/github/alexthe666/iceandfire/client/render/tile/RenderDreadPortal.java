package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class RenderDreadPortal<T extends TileEntityDreadPortal> implements BlockEntityRenderer<T, EndPortalRenderState> {
    public static final Identifier DREAD_PORTAL_BACKGROUND = Identifier.fromNamespaceAndPath("iceandfire", "textures/block/dread_portal.png");
    public static final Identifier DREAD_PORTAL = Identifier.fromNamespaceAndPath("iceandfire", "textures/block/dread_portal.png");

    public RenderDreadPortal(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public EndPortalRenderState createRenderState() {
        return new EndPortalRenderState();
    }

    @Override
    public void submit(EndPortalRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        // The portal is drawn by the block model texture.
    }
}
