import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/** Compile-only probe of the 26.1 recipe record/serializer API. */
public final class RecipeApiProbe implements Recipe<RecipeInput> {
    public static final RecipeType<RecipeApiProbe> TYPE = RecipeType.simple(Identifier.fromNamespaceAndPath("iceandfire", "dragonforge"));

    public static final Codec<ItemStack> LEGACY_STACK = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("item").forGetter(stack -> net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem())),
        Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount)
    ).apply(instance, (id, count) -> new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(id), count)));

    public static final Codec<ItemStack> RESULT_CODEC = Codec.withAlternative(ItemStack.CODEC, LEGACY_STACK);

    public static final MapCodec<RecipeApiProbe> CODEC = MapCodec.unit(new RecipeApiProbe());
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeApiProbe> STREAM_CODEC = StreamCodec.unit(new RecipeApiProbe());
    public static final RecipeSerializer<RecipeApiProbe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return input.size() >= 2 && !input.getItem(0).isEmpty();
    }

    @Override
    public ItemStack assemble(RecipeInput input) {
        return ItemStack.EMPTY;
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
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<RecipeInput>> getType() {
        return TYPE;
    }

    public static void main(String[] args) {
        System.out.println(TYPE);
        System.out.println(SERIALIZER.codec());
        System.out.println(ByteBufCodecs.STRING_UTF8);
        System.out.println(Ingredient.CODEC);
        System.out.println(RESULT_CODEC);
        System.out.println("recipe api compiled");
    }
}
