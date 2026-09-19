package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.inventory.ContainerPodium;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiPodium extends AbstractContainerScreen<ContainerPodium> {

    public static final Identifier PODUIM_TEXTURE = Identifier.parse("iceandfire:textures/gui/podium.png");

    public GuiPodium(ContainerPodium container, Inventory inv, Component name) {
        super(container, inv, name, 176, 133);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        if (menu != null) {
            String s = I18n.get("block.iceandfire.podium");
            graphics.text(this.font, s, this.imageWidth / 2 - this.font.width(s) / 2, 6, 4210752);
        }
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, PODUIM_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
