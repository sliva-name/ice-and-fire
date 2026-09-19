package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.inventory.ContainerDragon;
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

public class GuiDragon extends AbstractContainerScreen<ContainerDragon> {
    private static final Identifier texture = Identifier.parse("iceandfire:textures/gui/dragon.png");
    private float mousePosx;
    private float mousePosY;

    public GuiDragon(ContainerDragon dragonInv, Inventory playerInv, Component name) {
        super(dragonInv, playerInv, name, 176, 214);
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
        int k = this.leftPos;
        int l = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, k, l, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        Entity entity = IceAndFire.PROXY.getReferencedMob();
        if (entity instanceof EntityDragonBase dragon) {
            float dragonScale = 1F / Math.max(0.0001F, dragon.getAgeScale());
            int scale = Math.max(1, (int) (dragonScale * 23F));
            int entX = k + 88;
            int entY = l + (int) (0.5F * (dragon.flyProgress)) + 55;
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, entX - scale, entY - scale * 2, entX + scale, entY, scale, this.mousePosx, this.mousePosY, 0.0625F, dragon);
            Font font = this.font;
            String s3 = dragon.getCustomName() == null ? StatCollector.translateToLocal("dragon.unnamed") : StatCollector.translateToLocal("dragon.name") + " " + dragon.getCustomName().getString();
            graphics.text(font, s3, k + this.imageWidth / 2 - font.width(s3) / 2, l + 75, 0xFFFFFF);
            String s2 = StatCollector.translateToLocal("dragon.health") + " " + Math.floor(Math.min(dragon.getHealth(), dragon.getMaxHealth())) + " / " + dragon.getMaxHealth();
            graphics.text(font, s2, k + this.imageWidth / 2 - font.width(s2) / 2, l + 84, 0xFFFFFF);
            String s5 = StatCollector.translateToLocal("dragon.gender") + StatCollector.translateToLocal((dragon.isMale() ? "dragon.gender.male" : "dragon.gender.female"));
            graphics.text(font, s5, k + this.imageWidth / 2 - font.width(s5) / 2, l + 93, 0xFFFFFF);
            String s6 = StatCollector.translateToLocal("dragon.hunger") + dragon.getHunger() + "/100";
            graphics.text(font, s6, k + this.imageWidth / 2 - font.width(s6) / 2, l + 102, 0xFFFFFF);
            String s4 = StatCollector.translateToLocal("dragon.stage") + " " + dragon.getDragonStage() + " " + StatCollector.translateToLocal("dragon.days.front") + dragon.getAgeInDays() + " " + StatCollector.translateToLocal("dragon.days.back");
            graphics.text(font, s4, k + this.imageWidth / 2 - font.width(s4) / 2, l + 111, 0xFFFFFF);
            String s7 = dragon.getOwner() != null ? StatCollector.translateToLocal("dragon.owner") + dragon.getOwner().getName().getString() : StatCollector.translateToLocal("dragon.untamed");
            graphics.text(font, s7, k + this.imageWidth / 2 - font.width(s7) / 2, l + 120, 0xFFFFFF);
        }
    }
}
