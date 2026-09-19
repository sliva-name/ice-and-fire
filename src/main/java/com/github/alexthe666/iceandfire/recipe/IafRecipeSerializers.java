package com.github.alexthe666.iceandfire.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IafRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "iceandfire");
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "iceandfire");

    public static final RegistryObject<RecipeType<DragonForgeRecipe>> DRAGON_FORGE_TYPE = RECIPE_TYPES.register(
        "dragonforge",
        () -> RecipeType.simple(Identifier.fromNamespaceAndPath("iceandfire", "dragonforge"))
    );
    public static final RegistryObject<RecipeSerializer<DragonForgeRecipe>> DRAGONFORGE_SERIALIZER = SERIALIZERS.register(
        "dragonforge",
        () -> new RecipeSerializer<>(DragonForgeRecipe.CODEC, DragonForgeRecipe.STREAM_CODEC)
    );
}
