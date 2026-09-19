import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;

/** Source-only: 26.1 spawn API is 4-arg EntitySpawnReason, no CompoundTag. */
public final class SpawnApiProbe {
    private static int checks;

    private static void check(boolean value, String name) {
        if (!value) {
            throw new AssertionError(name);
        }
        checks++;
    }

    public static void main(String[] args) {
        check(EntitySpawnReason.NATURAL != null, "NATURAL");
        check(EntitySpawnReason.SPAWN_ITEM_USE != null, "SPAWN_ITEM_USE was SPAWN_EGG");
        check(EntitySpawnReason.CHUNK_GENERATION != null, "CHUNK_GENERATION");
        check(EntitySpawnReason.COMMAND != null, "COMMAND");
        check(EntitySpawnReason.SPAWNER != null, "SPAWNER");
        check(EntitySpawnReason.MOB_SUMMONED != null, "MOB_SUMMONED");
        System.out.println("SpawnApiProbe compiled " + checks + " spawn reasons");
    }

    @SuppressWarnings("unused")
    private static SpawnGroupData callFinalize(Mob mob, ServerLevelAccessor world, DifficultyInstance difficulty, SpawnGroupData data) {
        return mob.finalizeSpawn(world, difficulty, EntitySpawnReason.NATURAL, data);
    }
}
