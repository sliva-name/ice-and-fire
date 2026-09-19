package com.github.alexthe666.iceandfire.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class DragonForgeRecipe implements Recipe<RecipeInput> {
    /**
     * 1.18 datapacks used {@code { "item": "id", "count": n }}. Overlay
     * remaps that to {@code id}. Do not build an {@link ItemStack} while
     * recipes reload: item components are not bound yet.
     */
    public record ResultSpec(Identifier id, int count) {
        public static final Codec<ResultSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("id").forGetter(spec -> Optional.of(spec.id)),
            Identifier.CODEC.optionalFieldOf("item").forGetter(spec -> Optional.empty()),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ResultSpec::count)
        ).apply(instance, (id, item, count) -> new ResultSpec(id.orElseGet(() -> item.orElseThrow()), count)));
    }

    public static final MapCodec<DragonForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("input").forGetter(DragonForgeRecipe::getInput),
        Ingredient.CODEC.fieldOf("blood").forGetter(DragonForgeRecipe::getBlood),
        ResultSpec.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
        Codec.STRING.fieldOf("dragon_type").forGetter(DragonForgeRecipe::getDragonType),
        Codec.INT.fieldOf("cook_time").forGetter(DragonForgeRecipe::getCookTime)
    ).apply(instance, DragonForgeRecipe::new));

    // Same wire order as 1.18 toNetwork: cookTime, dragonType, input, blood, result.
    public static final StreamCodec<RegistryFriendlyByteBuf, DragonForgeRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, DragonForgeRecipe::getCookTime,
        ByteBufCodecs.STRING_UTF8, DragonForgeRecipe::getDragonType,
        Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getInput,
        Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getBlood,
        ItemStack.STREAM_CODEC, DragonForgeRecipe::getResultItem,
        (cookTime, dragonType, input, blood, result) -> new DragonForgeRecipe(input, blood, result, dragonType, cookTime)
    );

    private final Ingredient input;
    private final Ingredient blood;
    private final ResultSpec result;
    private final String dragonType;
    private final int cookTime;

    public DragonForgeRecipe(Ingredient input, Ingredient blood, ResultSpec result, String dragonType, int cookTime) {
        this.input = input;
        this.blood = blood;
        this.result = result;
        this.dragonType = dragonType;
        this.cookTime = cookTime;
    }

    public DragonForgeRecipe(Ingredient input, Ingredient blood, ItemStack result, String dragonType, int cookTime) {
        this(input, blood, new ResultSpec(BuiltInRegistries.ITEM.getKey(result.getItem()), result.getCount()), dragonType, cookTime);
    }

    public Ingredient getInput() {
        return input;
    }

    public Ingredient getBlood() {
        return blood;
    }

    public int getCookTime() {
        return cookTime;
    }

    public String getDragonType() {
        return dragonType;
    }

    public @NotNull ItemStack getResultItem() {
        return new ItemStack(BuiltInRegistries.ITEM.getValue(result.id()), result.count());
    }

    public @NotNull ItemStack getToastSymbol() {
        // 1.18 always used the fire core icon. The recipe is special-only, so
        // the book never showed it; JEI categories supply their own catalysts.
        return getResultItem();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    @Override
    public boolean matches(@NotNull RecipeInput inv, @NotNull Level worldIn) {
        return this.input.test(inv.getItem(0)) && this.blood.test(inv.getItem(1)) && this.dragonType.equals(typeId(inv));
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public boolean isValidBlood(ItemStack bloodStack) {
        return this.blood.test(bloodStack);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput dragonforge) {
        return getResultItem();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(input, blood));
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return IafRecipeSerializers.DRAGONFORGE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<RecipeInput>> getType() {
        return IafRecipeSerializers.DRAGON_FORGE_TYPE.get();
    }

    public static List<DragonForgeRecipe> allOf(RecipeManager manager) {
        return manager.getRecipes().stream()
            .map(RecipeHolder::value)
            .filter(DragonForgeRecipe.class::isInstance)
            .map(DragonForgeRecipe.class::cast)
            .toList();
    }

    public static List<DragonForgeRecipe> allOfAccess(net.minecraft.world.item.crafting.RecipeAccess access) {
        return access instanceof RecipeManager manager ? allOf(manager) : List.of();
    }

    private static String typeId(RecipeInput inv) {
        if (inv instanceof DragonForgeTypeSource source) {
            return source.getTypeID();
        }
        return "";
    }

    public interface DragonForgeTypeSource {
        String getTypeID();
    }
}
