package com.github.alexthe666.iceandfire.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * Electricity specialization of the lightning effect used with permission from aidancbrady.
 * Generation/lifetime advance during extraction only; deferred drawing owns immutable vertices.
 */
public class LightningRender {
    private static final int SEGMENTS = 15;
    private static final double LIFESPAN = 4;
    private final Random random;
    // Entities are weak extraction keys, never retained by a snapshot or deferred callback.
    private final Map<Object, History> owners = new WeakHashMap<>();

    public LightningRender() {
        this(new Random());
    }

    public LightningRender(Random random) {
        this.random = random;
    }

    public static boolean withinRange(double distanceSquared, int renderDistanceChunks) {
        double range = Math.max(256, renderDistanceChunks * 16.0);
        return distanceSquared <= range * range;
    }

    public static float boltSize(float dragonScale) {
        // This was a linear mapping, despite the old getBoundedScale name: do not clamp.
        return 0.05F * (0.5F + 0.4F * dragonScale * 1.5F);
    }

    public void remove(Object owner) {
        owners.remove(owner);
    }

    public Snapshot extract(Object owner, double time, Vec3 start, Vec3 end, float dragonScale, Vec3 origin) {
        History history = owners.computeIfAbsent(owner, ignored -> new History());
        if (time < history.lastTime) history.bolts.clear();
        history.bolts.removeIf(bolt -> time - bolt.created >= LIFESPAN);
        // NO_DELAY spawns on every new frame, but not twice for the same extracted timestamp.
        if (time != history.lastTime || history.bolts.isEmpty()) {
            history.bolts.add(new Bolt(time, generate(start, end, boltSize(dragonScale))));
        }
        history.lastTime = time;
        var vertices = new ArrayList<Vec3>();
        for (Bolt bolt : history.bolts) {
            double life = (time - bolt.created) / LIFESPAN;
            int count = bolt.segments.size();
            int first = life > 0.5 ? (int) (count * (life - 0.5) / 0.5) : 0;
            int last = life < 0.5 ? (int) (count * life / 0.5) : count;
            for (int i = first; i < last; i++) {
                for (Vec3 vertex : bolt.segments.get(i)) vertices.add(vertex.subtract(origin));
            }
        }
        return vertices.isEmpty() ? Snapshot.EMPTY : new Snapshot(vertices);
    }

    public static void submit(Snapshot snapshot, PoseStack poses, SubmitNodeCollector collector) {
        if (!snapshot.vertices.isEmpty()) {
            collector.submitCustomGeometry(poses, RenderTypes.lightning(), snapshot::render);
        }
    }

    public record Snapshot(List<Vec3> vertices) {
        public static final Snapshot EMPTY = new Snapshot(List.of());

        public Snapshot {
            vertices = List.copyOf(vertices);
        }

        public void render(PoseStack.Pose pose, VertexConsumer buffer) {
            for (Vec3 vertex : vertices) {
                buffer.addVertex(pose, (float) vertex.x, (float) vertex.y, (float) vertex.z)
                    .setColor(0.70F, 0.45F, 0.89F, 0.8F);
            }
        }
    }

    private List<List<Vec3>> generate(Vec3 start, Vec3 end, float size) {
        var segments = new ArrayList<List<Vec3>>();
        Vec3 diff = end.subtract(start);
        float distance = (float) diff.length();
        if (distance < 1.0E-6F) return segments;
        var queue = new ArrayDeque<Instruction>();
        queue.add(new Instruction(start, 0, Vec3.ZERO, null, false));
        while (!queue.isEmpty()) {
            Instruction data = queue.remove();
            Vec3 perpendicular = data.perpendicular;
            float progress = data.progress + (1F / SEGMENTS) * (0.5F + random.nextFloat());
            Vec3 segmentEnd;
            if (progress >= 1) {
                segmentEnd = end;
            } else {
                float spread = (float) Math.sin(Math.PI * progress);
                float maxDiff = 0.25F * spread * distance * (float) random.nextGaussian();
                Vec3 rand = new Vec3(-0.5 + random.nextDouble(), -0.5 + random.nextDouble(), -0.5 + random.nextDouble());
                Vec3 next = diff.cross(rand).normalize().scale(maxDiff * (1 - 0.8F));
                if (progress > 0.5F) next = next.add(perpendicular.scale(-(1 - spread)));
                perpendicular = perpendicular.add(next);
                segmentEnd = start.add(diff.scale(progress)).add(perpendicular);
            }
            float width = size * (0.5F + (1 - progress) * 0.5F);
            End cache = addQuads(segments, data.cache, data.start, segmentEnd, width);
            if (progress >= 1) break;
            if (!data.branch || random.nextFloat() < 0.15F) {
                queue.add(new Instruction(segmentEnd, progress, perpendicular, cache, data.branch));
            }
            while (random.nextFloat() < 0.25F * (1 - progress)) {
                queue.add(new Instruction(segmentEnd, progress, perpendicular, cache, true));
            }
        }
        return segments;
    }

    private static End addQuads(List<List<Vec3>> segments, End cache, Vec3 start, Vec3 end, float size) {
        Vec3 diff = end.subtract(start);
        Vec3 right = diff.cross(new Vec3(0.5, 0.5, 0.5));
        // Avoid a collapsed ribbon for a segment parallel to the original reference vector.
        if (right.lengthSqr() < 1.0E-12) {
            right = diff.cross(Math.abs(diff.y) < Math.abs(diff.x) ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0));
        }
        right = right.normalize().scale(size);
        Vec3 back = diff.cross(right).normalize().scale(size);
        Vec3 startPos = cache != null ? cache.position : start;
        Vec3 startRight = cache != null ? cache.right : startPos.add(right);
        Vec3 startBack = cache != null ? cache.back : startPos.add(right.scale(0.5)).add(back);
        Vec3 endRight = end.add(right);
        Vec3 endBack = end.add(right.scale(0.5)).add(back);
        segments.add(List.of(startPos, end, endRight, startRight,
            startRight, endRight, end, startPos,
            startRight, endRight, endBack, startBack,
            startBack, endBack, endRight, startRight));
        return new End(end, endRight, endBack);
    }

    private static class History {
        private double lastTime = Double.NEGATIVE_INFINITY;
        private final List<Bolt> bolts = new ArrayList<>();
    }

    private record Bolt(double created, List<List<Vec3>> segments) {}
    private record End(Vec3 position, Vec3 right, Vec3 back) {}
    private record Instruction(Vec3 start, float progress, Vec3 perpendicular, End cache, boolean branch) {}
}
