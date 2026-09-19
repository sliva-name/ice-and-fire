package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

/**
 * 26.1 {@link ChestRenderer} no longer has a per-tile material hook
 * ({@code ChestRenderState.material} is an enum). Ghost chest models stay
 * registered on {@code IafClientSetup} atlas ids for a later custom BER;
 * until then this uses the vanilla chest renderer.
 */
public class RenderGhostChest extends ChestRenderer<TileEntityGhostChest> {

    public RenderGhostChest(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}
