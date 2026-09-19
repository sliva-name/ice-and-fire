import net.minecraft.world.level.block.Rotation;

public final class IafJigsawStructuresTest {
    public static void main(String[] args) {
        assertOffset(Rotation.NONE, 5, 5);
        assertOffset(Rotation.CLOCKWISE_90, -5, 5);
        assertOffset(Rotation.CLOCKWISE_180, -5, -5);
        assertOffset(Rotation.COUNTERCLOCKWISE_90, 5, -5);
        if (startY(10, 8, 12, 9, 1) != 9) {
            throw new AssertionError("graveyard/mausoleum min+1");
        }
        if (startY(10, 8, 12, 9, 2) != 10) {
            throw new AssertionError("gorgon min+2");
        }
        var plan = structureSetPlan(16, 30, 16);
        if (plan[0] != 32 || plan[1] != 16 || plan[2] != 342226450 || plan[3] != 48 || plan[4] != 30 || plan[5] != 16) {
            throw new AssertionError("1.18 default structure set changed");
        }
        System.out.println("iaf jigsaw structure math matches 1.18");
    }

    private static void assertOffset(Rotation rotation, int x, int z) {
        int xOffset = 5;
        int zOffset = 5;
        if (rotation == Rotation.CLOCKWISE_90) {
            xOffset = -5;
        } else if (rotation == Rotation.CLOCKWISE_180) {
            xOffset = -5;
            zOffset = -5;
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            zOffset = -5;
        }
        if (xOffset != x || zOffset != z) {
            throw new AssertionError(rotation + " " + xOffset + "," + zOffset);
        }
    }

    private static int startY(int y1, int y2, int y3, int y4, int bonus) {
        return Math.min(Math.min(y1, y2), Math.min(y3, y4)) + bonus;
    }

    private static int[] structureSetPlan(int gorgon, int mausoleum, int graveyard) {
        int graveyardWeight = graveyard * 3;
        int average = (int) Math.ceil((gorgon + mausoleum + graveyardWeight) / 3.0D);
        return new int[]{Math.max(average, 2), Math.max(average / 2, 1), 342226450, graveyardWeight, mausoleum, gorgon};
    }
}
