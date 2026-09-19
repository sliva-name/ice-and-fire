import com.github.alexthe666.iceandfire.pathfinding.IafPathTypes;
import net.minecraft.world.level.pathfinder.PathType;

/** Source-only: 26.1 PathType danger set matches 1.18 BlockPathTypes.getDanger() != null. */
public final class IafPathTypesTest {
    private static int checks;

    private static void check(boolean value, String name) {
        if (!value) {
            throw new AssertionError(name);
        }
        checks++;
    }

    public static void main(String[] args) {
        check(IafPathTypes.hasDanger(PathType.FIRE), "DAMAGE_FIRE -> FIRE");
        check(IafPathTypes.hasDanger(PathType.FIRE_IN_NEIGHBOR), "DANGER_FIRE -> FIRE_IN_NEIGHBOR");
        check(IafPathTypes.hasDanger(PathType.DAMAGING), "DAMAGE_OTHER -> DAMAGING");
        check(IafPathTypes.hasDanger(PathType.DAMAGING_IN_NEIGHBOR), "DANGER_OTHER -> DAMAGING_IN_NEIGHBOR");
        check(IafPathTypes.hasDanger(PathType.LAVA), "LAVA");
        check(!IafPathTypes.hasDanger(PathType.WALKABLE), "WALKABLE is not danger");
        check(!IafPathTypes.hasDanger(PathType.OPEN), "OPEN is not danger");
        check(!IafPathTypes.hasDanger(PathType.DAMAGE_CAUTIOUS), "DAMAGE_CAUTIOUS had null getDanger");
        check(!IafPathTypes.hasDanger(PathType.WATER), "WATER is not danger");
        System.out.println("IafPathTypesTest passed " + checks + " checks");
    }
}
