import net.minecraft.world.level.Explosion;

public final class ExplosionInteractionProbe {
    public static void main(String[] args) {
        for (Explosion.BlockInteraction value : Explosion.BlockInteraction.values()) {
            System.out.println(value);
        }
    }
}
