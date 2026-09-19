package mezz.jei.api;

import net.minecraft.resources.Identifier;

/**
 * 1.18 JEI plugin surface used by Ice and Fire. No 26.1 JEI exists yet;
 * this compile-time host keeps the original categories, slots and recipe lists.
 */
public interface IModPlugin {
    Identifier getPluginUid();

    default void registerCategories(mezz.jei.api.registration.IRecipeCategoryRegistration registry) {
    }

    default void registerRecipes(mezz.jei.api.registration.IRecipeRegistration registry) {
    }

    default void registerRecipeCatalysts(mezz.jei.api.registration.IRecipeCatalystRegistration registry) {
    }
}
