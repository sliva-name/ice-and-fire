package com.github.alexthe666.iceandfire.compat.jei.firedragonforge;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class FireDragonForgeDrawable implements IDrawable {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/gui/dragonforge_fire.png");

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 120;
    }

    @Override
    public void draw(@NotNull GuiGraphicsExtractor graphics, int xOffset, int yOffset) {
        drawTexturedModalRect(graphics, xOffset, yOffset, 3, 4, 170, 79);
        var player = Minecraft.getInstance().player;
        int scaledProgress = player == null ? 0 : (player.tickCount % 100) * 128 / 100;
        drawTexturedModalRect(graphics, xOffset + 9, yOffset + 19, 0, 166, scaledProgress, 38);
    }

    public void drawTexturedModalRect(GuiGraphicsExtractor graphics, int x, int y, int textureX, int textureY, int width, int height) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, textureX, textureY, width, height, 256, 256);
    }
}
