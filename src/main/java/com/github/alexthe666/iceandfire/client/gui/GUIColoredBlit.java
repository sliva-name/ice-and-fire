package com.github.alexthe666.iceandfire.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class GUIColoredBlit {
    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texW, int texH, float alpha) {
        int color = Mth.floor(Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F) << 24 | 0xFFFFFF;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, uWidth, vHeight, texW, texH, color);
    }

    public static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, float u, float v, int width, int height, int texW, int texH, float alpha) {
        blit(graphics, texture, x, y, width, height, u, v, width, height, texW, texH, alpha);
    }
}
