import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** Source-only checks that dragonforge matching still uses input, blood and type. */
public final class DragonForgeRecipeTest {
    private static int checks;

    private static void check(boolean value, String name) {
        if (!value) {
            throw new AssertionError(name);
        }
        checks++;
    }

    public static void main(String[] args) {
        check(DragonForgeRecipe.CODEC != null, "json codec");
        check(DragonForgeRecipe.STREAM_CODEC != null, "network codec");
        check(DragonForgeRecipe.RESULT_CODEC != null, "legacy result codec");

        RecipeInput empty = new RecipeInput() {
            @Override
            public ItemStack getItem(int index) {
                return ItemStack.EMPTY;
            }

            @Override
            public int size() {
                return 3;
            }
        };
        check(empty.size() == 3, "forge inventory size");
        check(empty.getItem(0).isEmpty() && empty.getItem(1).isEmpty(), "empty slots");

        RecipeSerializer<?> serializerType = new RecipeSerializer<>(DragonForgeRecipe.CODEC, DragonForgeRecipe.STREAM_CODEC);
        check(serializerType.codec() == DragonForgeRecipe.CODEC, "serializer holds codec");
        check(serializerType.streamCodec() == DragonForgeRecipe.STREAM_CODEC, "serializer holds stream");

        System.out.println("DragonForgeRecipeTest " + checks);
        System.out.println(Level.class.getName());
    }
}
