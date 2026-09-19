import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.ICustomStatueModel;
import com.github.alexthe666.iceandfire.client.model.ModelDreadBeast;
import com.github.alexthe666.iceandfire.client.render.entity.DreadBeastRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.DreadBeastRenderState.AnimationKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/** Source-only probe against current Citadel/target APIs; no entity stubs, client singleton or GPU. */
public final class DreadBeastModelTest {
    private static int checks;
    private static final float TAIL_REST = -0.9560913642424937F;

    public static void main(String[] args) {
        ModelDreadBeast model = new ModelDreadBeast();
        // Compile-time contracts for the port, not reflective fallbacks to the old entity API.
        AdvancedEntityModel<DreadBeastRenderState> stateModel = model;
        ICustomStatueModel statue = model;
        check(stateModel == statue, "state model implements statue contract");
        List<AdvancedModelBox> parts = parts(model);
        check(parts.size() == 28, "original 28-part rig");
        check(parts.stream().mapToInt(p -> p.cubeList.size()).sum() == 28, "original 28 cubes");
        check(model.texWidth == 256 && model.texHeight == 128, "original atlas");
        check(model.parts().iterator().next() == model.Body, "body root");
        check(model.LowerBody.childModels.contains(model.Tail), "tail attached to lower body");
        check(model.Tail.childModels.contains(model.Tail2) && model.Tail2.childModels.contains(model.Tail3), "tail chain");
        check(model.Neck1.childModels.contains(model.HeadBase) && model.HeadBase.childModels.contains(model.Jaw), "neck/head/jaw chain");
        near(model.Body.defaultPositionY, 9.3F, "original body pivot");
        near(model.Tail.defaultRotationX, TAIL_REST, "original tail pitch");
        check(AnimationKind.NONE.token() == IAnimatedEntity.NO_ANIMATION, "NONE token identity");
        check(AnimationKind.BITE.token().getDuration() == 15, "bite token duration");
        check(AnimationKind.SPAWN.token().getDuration() == 40, "spawn token remains 40, not model timeline 35");
        check(AnimationKind.BITE.token() != AnimationKind.SPAWN.token(), "distinct animation identities");

        float[] rest = pose(model);
        keyframes(model, rest);
        walking(model);
        adapterAndIsolation(model, rest);
        System.out.println("DreadBeastModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: LayerGenericGlowing is compiled only; no layer submission, entity extraction, "
            + "texture selection, GPU/visual validation or legacy statue-renderer integration. "
            + "SPAWN's zero-target reset from 30 to 35 is pose-indistinguishable from rest.");
    }

    private static void keyframes(ModelDreadBeast model, float[] rest) {
        for (AnimationKind kind : AnimationKind.values()) {
            int end = kind == AnimationKind.BITE ? 16 : 41;
            for (int tick = 0; tick <= end; tick++) {
                for (float partial : new float[] {0, 0.25F, 0.5F, 0.99F}) {
                    DreadBeastRenderState state = state(kind, tick, partial);
                    String context = kind + " tick=" + tick + " partial=" + partial;
                    // animate is deliberately tested separately from procedural idle/walk motion.
                    model.animate(state);
                    float[] expected = rest.clone();
                    if (kind == AnimationKind.BITE) {
                        add(model, expected, model.Neck1, 0, bite(tick, partial, -39, -19));
                        add(model, expected, model.HeadBase, 0, bite(tick, partial, 40, 20));
                        add(model, expected, model.HeadBase, 2, bite(tick, partial, -15, 10));
                        add(model, expected, model.Jaw, 0, bite(tick, partial, -50, 10));
                    } else if (kind == AnimationKind.SPAWN) {
                        // Zero-duration first frame establishes +35 immediately. 30 ticks to zero,
                        // then a five-tick zero-target reset; token remains active through tick 39.
                        add(model, expected, model.Body, 4,
                            tick < 30 ? 35 * (1 - sine((tick + partial) / 30)) : 0);
                    }
                    same(model, expected, context);
                    model.animate(state);
                    same(model, expected, context + " repeat");
                    model.animate(new DreadBeastRenderState());
                    same(model, rest, context + " fresh NONE resets");
                    model.animate(state);
                    same(model, expected, context + " A/B/A isolation");
                    check(state.animation == kind && state.animationTick == tick && state.partialTick == partial,
                        context + " input timing unchanged");
                }
            }
        }
        // No reliance on the previous animation to happen to dirty each part.
        for (AdvancedModelBox part : model.getAllParts()) {
            part.rotateAngleX += 1;
            part.rotateAngleY -= 2;
            part.rotateAngleZ += 3;
            part.rotationPointX += 4;
            part.rotationPointY -= 5;
            part.rotationPointZ += 6;
        }
        model.animate(new DreadBeastRenderState());
        same(model, rest, "animate resets every part's rotations and pivots");
    }

    private static float bite(int tick, float partial, float first, float second) {
        if (tick < 5) return radians(first) * sine((tick + partial) / 5);
        if (tick < 10) {
            float weight = sine((tick - 5 + partial) / 5);
            return radians(first) * (1 - weight) + radians(second) * weight;
        }
        if (tick < 15) return radians(second) * (1 - sine((tick - 10 + partial) / 5));
        return 0;
    }

    private static void walking(ModelDreadBeast model) {
        float threshold = radians(-20) - TAIL_REST;
        for (float amount : new float[] {0, 0.2F, threshold - 0.001F, threshold, threshold + 0.001F, 1, 2}) {
            for (float age : new float[] {0, 19.5F, 103}) {
                for (float walk : new float[] {0, 7.25F, 21}) {
                    DreadBeastRenderState state = new DreadBeastRenderState();
                    state.ageInTicks = age;
                    state.walkAnimationPos = walk;
                    state.walkAnimationSpeed = amount;
                    model.setupAnim(state);
                    near(model.Tail.rotateAngleX, Math.min(TAIL_REST + amount, radians(-20)),
                        "tail pitch overwrites waves and clamps at -20 degrees");
                    near(model.Tail2.rotateAngleX, model.Tail2.defaultRotationX
                        + Mth.cos(age * 0.05F) * 0.075F
                        + Mth.cos(walk * 0.45F + (float) (Math.PI / 3)) * amount * 0.15F,
                        "second tail keeps idle and walking chain waves");
                    near(model.Tail.rotateAngleY, Mth.cos(age * 0.05F) * 0.375F, "tail idle swing remains");
                    near(model.Body.rotationPointY, model.Body.defaultPositionY
                        + (float) (Math.sin(walk * 0.45F) * amount * 1.15F) - amount * 1.15F, "walking body bob");
                    float leg = Mth.cos(walk * 0.45F) * 0.75F * amount;
                    near(model.BackLegR1.rotateAngleX, model.BackLegR1.defaultRotationX + leg, "rear walking leg");
                    near(model.LegR1.rotateAngleX, model.LegR1.defaultRotationX - leg, "front walking leg");
                    float[] first = pose(model);
                    model.setupAnim(state);
                    same(model, first, "procedural animation does not accumulate");
                }
            }
        }
    }

    private static void adapterAndIsolation(ModelDreadBeast model, float[] rest) {
        model.animate(new DreadBeastRenderState());
        var adapter = model.asEntityModel();
        var root = adapter.root();
        check(adapter.allParts().size() == 28 * 3 + 1, "complete cached native tree");
        ModelDreadBeast reference = new ModelDreadBeast();
        DreadBeastRenderState idle = new DreadBeastRenderState();
        adapter.setupAnim(idle);
        float[] idlePose = pose(model);
        for (AnimationKind kind : AnimationKind.values()) {
            for (int tick : new int[] {0, 2, 5, 7, 10, 12, 15, 29, 30, 34, 35, 39, 40}) {
                DreadBeastRenderState active = state(kind, tick, 0.5F);
                active.ageInTicks = 27.5F;
                active.walkAnimationPos = 8.25F;
                active.walkAnimationSpeed = 0.8F;
                active.variant = 1;
                active.yRot = 32;
                active.xRot = -17;
                adapter.setupAnim(active);
                reference.setupAnim(active);
                float[] expected = pose(reference);
                same(model, expected, "adapter matches independent model");
                // setupAnim must actually include the keyframes, not just match another instance.
                float jawIdle = -(Mth.cos(active.ageInTicks * 0.05F + 1) * 0.175F - 0.1F);
                near(model.Jaw.rotateAngleX, model.Jaw.defaultRotationX + jawIdle
                    + (kind == AnimationKind.BITE ? bite(tick, 0.5F, -50, 10) : 0),
                    "setupAnim combines bite and procedural jaw motion");
                float bob = (float) (Math.sin(active.walkAnimationPos * 0.45F)
                    * active.walkAnimationSpeed * 1.15F) - active.walkAnimationSpeed * 1.15F;
                near(model.Body.rotationPointY, model.Body.defaultPositionY + bob
                    + (kind == AnimationKind.SPAWN && tick < 30 ? 35 * (1 - sine((tick + 0.5F) / 30)) : 0),
                    "setupAnim combines spawn and walking body motion");
                nativePart(model.Body, root.getChild("part0"));
                geometry(model, root);
                adapter.setupAnim(idle);
                same(model, idlePose, "adapter clears active state");
                nativePart(model.Body, root.getChild("part0"));
                adapter.setupAnim(active);
                same(model, expected, "adapter A/B/A state isolation");
                check(adapter.root() == root, "native root reused");
                active.variant = 0;
                adapter.setupAnim(active);
                same(model, expected, "variant does not alter model pose");
                check(active.animation == kind && active.animationTick == tick && active.partialTick == 0.5F
                    && active.ageInTicks == 27.5F && active.walkAnimationPos == 8.25F
                    && active.walkAnimationSpeed == 0.8F && active.yRot == 32 && active.xRot == -17,
                    "setupAnim leaves captured state unchanged");
            }
        }
        model.animate(idle);
        same(model, rest, "animate clears procedural motion after adapter use");
    }

    private static void nativePart(BasicModelPart box, ModelPart part) {
        near(part.xRot, box.rotateAngleX, "native rotation X");
        near(part.yRot, box.rotateAngleY, "native rotation Y");
        near(part.zRot, box.rotateAngleZ, "native rotation Z");
        AdvancedModelBox advanced = (AdvancedModelBox) box;
        near(part.x, box.rotationPointX + advanced.offsetX, "native pivot X");
        near(part.y, box.rotationPointY + advanced.offsetY, "native pivot Y");
        near(part.z, box.rotationPointZ + advanced.offsetZ, "native pivot Z");
        for (int i = 0; i < box.childModels.size(); i++) {
            nativePart(box.childModels.get(i), part.getChild("children").getChild("part" + i));
        }
    }

    private static void geometry(ModelDreadBeast model, ModelPart root) {
        PoseStack stack = new PoseStack();
        stack.translate(0.25F, -0.5F, 0.75F);
        Matrix4f before = new Matrix4f(stack.last().pose());
        Capture nativeVertices = new Capture();
        root.render(stack, nativeVertices, 123, 456, -1);
        check(stack.last().pose().equals(before), "native render restores pose stack");
        Capture directVertices = new Capture();
        model.renderToBuffer(stack, directVertices, 123, 456, -1);
        check(stack.last().pose().equals(before), "direct render restores pose stack");
        check(nativeVertices.count == 28 * 24, "native adapter emits all cube faces");
        check(directVertices.count == nativeVertices.count, "direct/native vertex counts agree");
    }

    private static DreadBeastRenderState state(AnimationKind kind, int tick, float partial) {
        DreadBeastRenderState state = new DreadBeastRenderState();
        state.animation = kind;
        state.animationTick = tick;
        state.partialTick = partial;
        return state;
    }

    private static List<AdvancedModelBox> parts(ModelDreadBeast model) {
        List<AdvancedModelBox> result = new ArrayList<>();
        model.getAllParts().forEach(result::add);
        return result;
    }

    private static float[] pose(ModelDreadBeast model) {
        List<AdvancedModelBox> parts = parts(model);
        float[] result = new float[parts.size() * 6];
        int i = 0;
        for (AdvancedModelBox part : parts) {
            result[i++] = part.rotateAngleX;
            result[i++] = part.rotateAngleY;
            result[i++] = part.rotateAngleZ;
            result[i++] = part.rotationPointX;
            result[i++] = part.rotationPointY;
            result[i++] = part.rotationPointZ;
        }
        return result;
    }

    private static void add(ModelDreadBeast model, float[] pose, AdvancedModelBox part, int component, float delta) {
        int index = parts(model).indexOf(part);
        check(index >= 0, "keyframed part belongs to rig");
        pose[index * 6 + component] += delta;
    }

    private static void same(ModelDreadBeast model, float[] expected, String message) {
        float[] actual = pose(model);
        check(actual.length == expected.length, message + " size");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], message + " component " + i);
    }

    private static float sine(float phase) { return Mth.sin((float) (phase * Math.PI / 2)); }
    private static float radians(float degrees) { return (float) Math.toRadians(degrees); }
    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0002F, message + ": " + actual + " != " + expected);
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    /** Recording sink implements the real target VertexConsumer, not a model/entity substitute. */
    private static final class Capture implements VertexConsumer {
        private int count;
        @Override public void addVertex(float x, float y, float z, int color, float u, float v,
                                        int overlay, int light, float nx, float ny, float nz) {
            check(Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(z), "finite vertex position");
            check(Float.isFinite(u) && Float.isFinite(v), "finite vertex UV");
            check(Float.isFinite(nx) && Float.isFinite(ny) && Float.isFinite(nz), "finite vertex normal");
            check(color == -1 && overlay == 456 && light == 123, "render attributes preserved");
            count++;
        }
        @Override public VertexConsumer addVertex(float x, float y, float z) { throw new AssertionError("Expected full vertex"); }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { throw new AssertionError(); }
        @Override public VertexConsumer setColor(int color) { throw new AssertionError(); }
        @Override public VertexConsumer setUv(float u, float v) { throw new AssertionError(); }
        @Override public VertexConsumer setUv1(int u, int v) { throw new AssertionError(); }
        @Override public VertexConsumer setUv2(int u, int v) { throw new AssertionError(); }
        @Override public VertexConsumer setNormal(float x, float y, float z) { throw new AssertionError(); }
        @Override public VertexConsumer setLineWidth(float width) { throw new AssertionError(); }
    }
}
