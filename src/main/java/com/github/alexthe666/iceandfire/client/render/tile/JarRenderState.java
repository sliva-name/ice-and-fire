package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.util.Mth;

public class JarRenderState extends BlockEntityRenderState {
    public final PixieRenderState pixie = new PixieRenderState();
    public boolean hasPixie;
    public float rotation;

    public static float interpolateRotation(float previous, float current, float partialTick) {
        return previous + partialTick * Mth.wrapDegrees(current - previous);
    }
}
