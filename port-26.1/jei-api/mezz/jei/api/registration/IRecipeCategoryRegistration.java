package mezz.jei.api.registration;

import mezz.jei.api.recipe.category.IRecipeCategory;

import java.util.ArrayList;
import java.util.List;

public interface IRecipeCategoryRegistration {
    void addRecipeCategories(IRecipeCategory<?>... categories);

    List<IRecipeCategory<?>> getCategories();

    final class Recording implements IRecipeCategoryRegistration {
        private final List<IRecipeCategory<?>> categories = new ArrayList<>();

        @Override
        public void addRecipeCategories(IRecipeCategory<?>... categories) {
            this.categories.addAll(List.of(categories));
        }

        @Override
        public List<IRecipeCategory<?>> getCategories() {
            return List.copyOf(categories);
        }
    }
}
