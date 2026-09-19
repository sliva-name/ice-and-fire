package com.github.alexthe666.iceandfire.client.gui.bestiary;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class ChangePageButton extends Button {
    private static final Identifier WIDGETS = Identifier.parse("iceandfire:textures/gui/bestiary/widgets.png");
    private final boolean right;
    public int lastpage = 1;
    private final int color;

    public ChangePageButton(int x, int y, boolean right, int color, OnPress press) {
        super(x, y, 23, 10, Component.literal(""), press, DEFAULT_NARRATION);
        this.right = right;
        this.color = color;
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!this.active) {
            return;
        }
        boolean flag = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        int i = 0;
        int j = 64;
        if (flag) {
            i += 23;
        }
        if (!this.right) {
            j += 13;
        }
        j += color * 23;
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, this.getX(), this.getY(), i, j, width, height, 256, 256);
    }
}
