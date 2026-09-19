import com.github.alexthe666.iceandfire.entity.util.IafListeningContainer;
import net.minecraft.world.item.ItemStack;

/** Source-only: inventory listener fires after setChanged, not during construction. */
public final class IafListeningContainerTest {
    private static int checks;

    private static void check(boolean value, String name) {
        if (!value) {
            throw new AssertionError(name);
        }
        checks++;
    }

    public static void main(String[] args) {
        int[] fires = {0};
        IafListeningContainer inventory = new IafListeningContainer(5);
        check(inventory.getContainerSize() == 5, "dragon inventory size");
        inventory.setItem(0, ItemStack.EMPTY);
        check(fires[0] == 0, "copy-before-listen does not fire");
        inventory.setListener(() -> fires[0]++);
        inventory.setChanged();
        check(fires[0] == 1, "setChanged after listen fires once");
        inventory.setListener(null);
        inventory.setChanged();
        check(fires[0] == 1, "cleared listener is silent");
        System.out.println("IafListeningContainerTest passed " + checks + " checks");
    }
}
