package mezz.jei.api.gui.ingredient;

import mezz.jei.api.ingredients.IIngredients;

import java.util.ArrayList;
import java.util.List;

public interface IGuiItemStackGroup {
    void init(int slotIndex, boolean input, int xPosition, int yPosition);

    void set(IIngredients ingredients);

    List<Slot> slots();

    record Slot(int index, boolean input, int x, int y) {
    }

    final class Recording implements IGuiItemStackGroup {
        private final List<Slot> slots = new ArrayList<>();

        @Override
        public void init(int slotIndex, boolean input, int xPosition, int yPosition) {
            slots.add(new Slot(slotIndex, input, xPosition, yPosition));
        }

        @Override
        public void set(IIngredients ingredients) {
        }

        @Override
        public List<Slot> slots() {
            return List.copyOf(slots);
        }
    }
}
