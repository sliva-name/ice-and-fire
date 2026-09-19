import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Plain-main CPU probe against the real 26.1 API; no entity, client singleton or GPU. */
public final class LightningRenderTest {
    private static int checks;

    public static void main(String[] args) {
        check(LightningRender.withinRange(256 * 256, 2), "inclusive minimum range");
        check(!LightningRender.withinRange(257 * 257, 2), "outside minimum range");
        check(LightningRender.withinRange(512 * 512, 32), "render distance extends range");
        near(LightningRender.boltSize(1), 0.055, "legacy width");
        near(LightningRender.boltSize(10), 0.325, "width is not clamped");
        var renderer = new LightningRender(new Random(1234));
        Object first = new Object();
        Object second = new Object();
        Vec3 origin = new Vec3(30_000_000, 100, -30_000_000);
        Vec3 start = origin.add(1, 2, 3);
        Vec3 end = start.add(30, 20, 10);
        check(renderer.extract(first, 100, start, end, 1, origin).vertices().isEmpty(), "starts faded in");
        var snapshot = renderer.extract(first, 102, start, end, 1, origin);
        check(!snapshot.vertices().isEmpty(), "visible at half life");
        check(snapshot.vertices().size() % 16 == 0, "four double-sided quads per segment");
        check(snapshot.vertices().contains(start.subtract(origin)), "starts at head");
        check(snapshot.vertices().contains(end.subtract(origin)), "reaches target");
        check(snapshot.vertices().stream().allMatch(v -> Double.isFinite(v.x) && Double.isFinite(v.y)
            && Double.isFinite(v.z) && v.length() < 100), "finite origin-relative geometry at world border");
        check(snapshot.equals(renderer.extract(first, 102, start, end, 1, origin)), "repeat extraction is stable");
        check(renderer.extract(second, 102, start, end, 1, origin).vertices().isEmpty(), "owners are isolated");
        var moved = renderer.extract(first, 102, start, end, 1, origin.add(5, 0, 0));
        near(moved.vertices().getFirst().x, snapshot.vertices().getFirst().x - 5, "lingering bolt follows origin subtraction");
        var frozen = List.copyOf(snapshot.vertices());
        renderer.extract(first, 104, start, end, 1, origin);
        check(snapshot.vertices().equals(frozen), "later extraction does not mutate deferred snapshot");
        check(renderer.extract(first, 110, start, end, 1, origin).vertices().isEmpty(), "expired bolts removed");
        check(renderer.extract(first, 90, start, end, 1, origin).vertices().isEmpty(), "time rewind clears history");
        renderer.remove(first);
        check(renderer.extract(first, 92, start, end, 1, origin).vertices().isEmpty(), "target loss clears history");
        renderer.remove(first);
        renderer.extract(first, 100, start, start, 1, origin);
        check(renderer.extract(first, 102, start, start, 1, origin).vertices().isEmpty(), "zero length has no geometry");
        try {
            snapshot.vertices().clear();
            throw new AssertionError("mutable snapshot");
        } catch (UnsupportedOperationException expected) {
            checks++;
        }
        var input = new ArrayList<>(List.of(new Vec3(1, 2, 3)));
        var copied = new LightningRender.Snapshot(input);
        input.clear();
        check(copied.vertices().size() == 1, "snapshot copies input");
        draw(snapshot);
        System.out.println("LightningRenderTest: " + checks + " checks passed");
        System.out.println("LIMITS: no entity extraction, frustum/collector execution or GPU/visual validation");
    }

    private static void draw(LightningRender.Snapshot snapshot) {
        var positions = new ArrayList<Vec3>();
        int[] colors = {0};
        // The proxy implements the real target interface; it is only a recording vertex sink.
        VertexConsumer sink = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
            new Class<?>[] {VertexConsumer.class}, (proxy, method, arguments) -> {
                if (method.isDefault()) return java.lang.reflect.InvocationHandler.invokeDefault(proxy, method, arguments);
                if (method.getName().equals("addVertex")) {
                    positions.add(new Vec3((float) arguments[0], (float) arguments[1], (float) arguments[2]));
                } else if (method.getName().equals("setColor") && arguments.length == 4) {
                    check((int) arguments[0] == 178 && (int) arguments[1] == 114
                        && (int) arguments[2] == 226 && (int) arguments[3] == 204, "electricity RGBA");
                    colors[0]++;
                } else {
                    throw new AssertionError("Unexpected vertex attribute: " + method);
                }
                return proxy;
            });
        var poses = new PoseStack();
        poses.translate(4, 5, 6);
        snapshot.render(poses.last(), sink);
        check(positions.size() == snapshot.vertices().size(), "all vertices emitted");
        check(colors[0] == positions.size(), "every vertex colored");
        near(positions.getFirst().x, snapshot.vertices().getFirst().x + 4, "submitted pose applied");
    }

    private static void near(double actual, double expected, String message) {
        check(Math.abs(actual - expected) < 0.00001, message + ": " + actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
