package com.github.alexthe666.iceandfire.client.render.pathfinding;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Pathfinding debug overlay. Immediate-mode MultiBufferSource drawing was removed in 26.2;
 * the overlay is disabled until it is rewritten against SubmitNodeCollector.
 */
public class RenderPath {
    public static void debugDraw(final double frame, final PoseStack matrixStack) {
    }
}
