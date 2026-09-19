package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GuiDragonForge extends AbstractContainerScreen<ContainerDragonForge> {
    private static final Identifier TEXTURE_FIRE = Identifier.parse("iceandfire:textures/gui/dragonforge_fire.png");
    private static final Identifier TEXTURE_ICE = Identifier.parse("iceandfire:textures/gui/dragonforge_ice.png");
    private static final Identifier TEXTURE_LIGHTNING = Identifier.parse("iceandfire:textures/gui/dragonforge_lightning.png");
    private final ContainerDragonForge tileFurnace;
    private final int dragonType;

    public GuiDragonForge(ContainerDragonForge container, Inventory inv, Component name) {
        super(container, inv, name);
        this.tileFurnace = container;
        this.dragonType = tileFurnace.fireType;
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Font font = this.font;
        if (tileFurnace != null) {
            String s = I18n.get("block.iceandfire.dragonforge_" + DragonType.getNameFromInt(dragonType) + "_core");
            graphics.text(font, s, this.imageWidth / 2 - font.width(s) / 2, 6, 4210752);
        }
        graphics.text(font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        Identifier texture = dragonType == 0 ? TEXTURE_FIRE : dragonType == 1 ? TEXTURE_ICE : TEXTURE_LIGHTNING;
        int k = this.leftPos;
        int l = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, k, l, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        int i1 = this.getCookTime(126);
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, k + 12, l + 23, 0, 166, i1, 38, 256, 256);
    }

    private int getCookTime(int p_175381_1_) {
        BlockEntity te = IceAndFire.PROXY.getRefrencedTE();
        int j = 0;
        var connection = this.minecraft.getConnection();
        List<DragonForgeRecipe> recipes = connection == null ? java.util.List.of() : DragonForgeRecipe.allOfAccess(connection.recipes())
            .stream().filter(item ->
                item.isValidInput(tileFurnace.getSlot(0).getItem()) && item.isValidBlood(tileFurnace.getSlot(1).getItem())).collect(Collectors.toList());
        int maxCookTime = recipes.isEmpty() ? 100 : recipes.get(0).getCookTime();
        if (te instanceof TileEntityDragonforge) {
            j = Math.min(((TileEntityDragonforge) te).cookTime, maxCookTime);
        }
        return j != 0 ? j * p_175381_1_ / maxCookTime : 0;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY);
    }
}
