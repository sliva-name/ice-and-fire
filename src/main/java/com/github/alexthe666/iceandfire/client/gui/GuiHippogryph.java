package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.inventory.ContainerHippogryph;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuiHippogryph extends AbstractContainerScreen<ContainerHippogryph> {
    private static final Identifier TEXTURE = Identifier.parse("iceandfire:textures/gui/hippogryph.png");
    private float mousePosx;
    private float mousePosY;

    public GuiHippogryph(ContainerHippogryph dragonInv, Inventory playerInv, Component name) {
        super(dragonInv, playerInv, name);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Entity entity = IceAndFire.PROXY.getReferencedMob();
        Font font = this.font;
        if (entity instanceof EntityHippogryph hippo) {
            graphics.text(font, hippo.getDisplayName().getString(), 8, 6, 4210752);
        }
        graphics.text(font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.mousePosx = mouseX;
        this.mousePosY = mouseY;
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        Entity entity = IceAndFire.PROXY.getReferencedMob();
        if (entity instanceof EntityHippogryph hippo) {
            if (hippo.isChested()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 79, j + 17, 0, this.imageHeight, 5 * 18, 54, 256, 256);
            }
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, i + 26, j + 18, i + 78, j + 70, 17, this.mousePosx, this.mousePosY, 0.0625F, hippo);
        }
    }
}
