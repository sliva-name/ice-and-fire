package mezz.jei.api.registration;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IRecipeRegistration {
    void addRecipes(List<?> recipes, Identifier recipeCategoryUid);

    void addIngredientInfo(ItemStack itemStack, Object ingredientType, Component... description);

    Map<Identifier, List<?>> getRecipes();

    List<ItemStack> getDescribedItems();

    final class Recording implements IRecipeRegistration {
        private final Map<Identifier, List<?>> recipes = new LinkedHashMap<>();
        private final List<ItemStack> described = new ArrayList<>();

        @Override
        public void addRecipes(List<?> recipes, Identifier recipeCategoryUid) {
            this.recipes.put(recipeCategoryUid, List.copyOf(recipes));
        }

        @Override
        public void addIngredientInfo(ItemStack itemStack, Object ingredientType, Component... description) {
            described.add(itemStack);
        }

        @Override
        public Map<Identifier, List<?>> getRecipes() {
            return Map.copyOf(recipes);
        }

        @Override
        public List<ItemStack> getDescribedItems() {
            return List.copyOf(described);
        }
    }
}
