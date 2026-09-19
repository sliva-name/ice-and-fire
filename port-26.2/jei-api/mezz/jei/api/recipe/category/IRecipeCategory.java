package mezz.jei.api.recipe.category;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public interface IRecipeCategory<T> {
    Identifier getUid();

    Class<? extends T> getRecipeClass();

    Component getTitle();

    IDrawable getBackground();

    IDrawable getIcon();

    void setIngredients(T recipe, IIngredients ingredients);

    void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients);
}
