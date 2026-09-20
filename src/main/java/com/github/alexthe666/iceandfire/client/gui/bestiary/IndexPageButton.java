package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class IndexPageButton extends Button {
    private static final Identifier WIDGETS = Identifier.parse("iceandfire:textures/gui/bestiary/widgets.png");

    public IndexPageButton(int x, int y, Component buttonText, Button.OnPress butn) {
        super(x, y, 160, 32, buttonText, butn, DEFAULT_NARRATION);
        this.width = 160;
        this.height = 32;
    }

    @Override
    protected void extractContents(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partial) {
        if (!this.active) {
            return;
        }
        Font font = IafConfig.useVanillaFont || !Minecraft.getInstance().options.languageCode.equalsIgnoreCase("en_us")
            ? Minecraft.getInstance().font
            : (Font) IceAndFire.PROXY.getFontRenderer();
        boolean flag = isHoveredOrFocused();
        graphics.blit(RenderPipelines.GUI_TEXTURED, WIDGETS, this.getX(), this.getY(), 0, flag ? 32 : 0, this.width, this.height, 256, 256);
        int color = flag ? 0xFFB8860B : 0xFF6B4E3A;
        graphics.text(font, this.getMessage().getVisualOrderText(), (this.getX() + this.width / 2 - font.width(this.getMessage().getString()) / 2), this.getY() + (this.height - 8) / 2, color, false);
    }
}
