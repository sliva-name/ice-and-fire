import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Compile-only probe: list the 26.1 Explosion interface so custom explosions can implement it. */
public final class ExplosionApiProbe implements Explosion {
    @Override
    public boolean shouldAffectBlocklikeEntities() {
        return true;
    }

    @Override
    public boolean canTriggerBlocks() {
        return true;
    }

    @Override
    public ServerLevel level() {
        return null;
    }

    @Override
    public @Nullable Entity getDirectSourceEntity() {
        return null;
    }

    @Override
    public @Nullable LivingEntity getIndirectSourceEntity() {
        return null;
    }

    @Override
    public float radius() {
        return 0;
    }

    @Override
    public Vec3 center() {
        return Vec3.ZERO;
    }

    @Override
    public Explosion.BlockInteraction getBlockInteraction() {
        return Explosion.BlockInteraction.DESTROY;
    }

    public static void main(String[] args) {
        System.out.println("Explosion interface methods compiled");
        System.out.println("BlockInteraction: " + java.util.Arrays.toString(Explosion.BlockInteraction.values()));
    }
}
