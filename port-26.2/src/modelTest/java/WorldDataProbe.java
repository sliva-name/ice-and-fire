import com.github.alexthe666.iceandfire.world.DragonPosWorldData;
import com.github.alexthe666.iceandfire.world.IafWorldData;
import net.minecraft.world.level.saveddata.SavedDataType;

/** Source-only: 26.1 SavedDataType codecs keep the 1.18 field names. */
public final class WorldDataProbe {
    private static int checks;

    private static void check(boolean value, String name) {
        if (!value) {
            throw new AssertionError(name);
        }
        checks++;
    }

    public static void main(String[] args) {
        SavedDataType<IafWorldData> world = IafWorldData.TYPE;
        SavedDataType<DragonPosWorldData> dragons = DragonPosWorldData.TYPE;
        check(world.codec() == IafWorldData.CODEC, "iaf world codec");
        check(dragons.codec() == DragonPosWorldData.CODEC, "dragon pos codec");
        check(world.id().getPath().equals("general"), "iaf world id");
        check(dragons.id().getPath().equals("dragon_positions"), "dragon pos id");
        System.out.println("WorldDataProbe compiled " + checks + " saved-data types");
    }
}
