package mezz.jei.api.gui.drawable;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IDrawable {
    int getWidth();

    int getHeight();

    void draw(GuiGraphicsExtractor graphics, int xOffset, int yOffset);
}
