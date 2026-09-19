package mezz.jei.api.ingredients;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public interface IIngredients {
    void setInputIngredients(List<Ingredient> inputs);

    void setOutput(Object ingredientType, ItemStack output);

    List<Ingredient> getInputs();

    ItemStack getOutput();

    final class Recording implements IIngredients {
        private final List<Ingredient> inputs = new ArrayList<>();
        private ItemStack output = ItemStack.EMPTY;

        @Override
        public void setInputIngredients(List<Ingredient> inputs) {
            this.inputs.clear();
            this.inputs.addAll(inputs);
        }

        @Override
        public void setOutput(Object ingredientType, ItemStack output) {
            this.output = output;
        }

        @Override
        public List<Ingredient> getInputs() {
            return List.copyOf(inputs);
        }

        @Override
        public ItemStack getOutput() {
            return output;
        }
    }
}
