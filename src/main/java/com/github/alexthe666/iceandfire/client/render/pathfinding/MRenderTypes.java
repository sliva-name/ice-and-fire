package com.github.alexthe666.iceandfire.client.render.pathfinding;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Path-debug buffers. 1.18 used custom CompositeState RenderTypes;
 * 26.1 maps those to the closest vanilla debug/line pipelines.
 */
public final class MRenderTypes {
    private MRenderTypes() {
    }

    public static RenderType customTexRenderer(final Identifier resourceLocation) {
        return RenderTypes.entityCutout(resourceLocation);
    }

    public static RenderType customLineRenderer() {
        return RenderTypes.lines();
    }

    public static RenderType customPathRenderer() {
        return RenderTypes.debugQuads();
    }

    public static RenderType customPathTextRenderer() {
        return RenderTypes.debugQuads();
    }
}
