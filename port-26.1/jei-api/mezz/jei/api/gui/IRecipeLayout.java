package mezz.jei.api.gui;

import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;

public interface IRecipeLayout {
    IGuiItemStackGroup getItemStacks();

    final class Recording implements IRecipeLayout {
        private final IGuiItemStackGroup.Recording stacks = new IGuiItemStackGroup.Recording();

        @Override
        public IGuiItemStackGroup getItemStacks() {
            return stacks;
        }

        public IGuiItemStackGroup.Recording recorded() {
            return stacks;
        }
    }
}
