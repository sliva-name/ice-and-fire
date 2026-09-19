import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.animator.SeaSerpentTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EnumSeaSerpentAnimations;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState.AnimationKind;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.LinkedHashMap;
import java.util.Map;

/** Plain main; put src/main/resources on the classpath. No mod entity, old binary, stub or GPU. */
public final class SeaSerpentModelTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        EnumSeaSerpentAnimations.initializeSerpentModels();
        var container = TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/seaserpent/seaserpent");
        TabulaModel<SeaSerpentRenderState> model = new TabulaModel<>(container, new SeaSerpentTabulaModelAnimator());
        check(model.textureWidth == 256 && model.textureHeight == 128, "original texture dimensions");
        for (var pose : EnumSeaSerpentAnimations.values()) {
            check(pose.seaserpent_model != null, "real asset loaded: " + pose);
            check(pose.seaserpent_model.getCubes().keySet().equals(model.getCubes().keySet()), "compatible pose names: " + pose);
        }
        check(AnimationKind.SPEAK.token() != AnimationKind.BITE.token(), "equal-duration animations have distinct identities");
        var adapter = model.asEntityModel();
        SeaSerpentRenderState idle = new SeaSerpentRenderState();
        adapter.setupAnim(idle);
        var baseline = snapshot(model);
        near(model.getCube("BodyUpper").rotationPointY, model.getCube("BodyUpper").defaultPositionY + 9, "legacy model height correction");

        // Includes the phase-3 -> phase-0 wrap and fractional render ticks.
        EnumSeaSerpentAnimations[] swim = {EnumSeaSerpentAnimations.SWIM1, EnumSeaSerpentAnimations.SWIM3,
            EnumSeaSerpentAnimations.SWIM4, EnumSeaSerpentAnimations.SWIM6};
        for (int cycle = 0; cycle < 40; cycle += 2) {
            for (float partial : new float[] {0, 0.25F, 0.9F}) {
                for (float speed : new float[] {0, 0.2F, 0.5F, 1}) {
                    SeaSerpentRenderState state = new SeaSerpentRenderState();
                    state.swimCycle = cycle;
                    state.partialTick = partial;
                    state.walkAnimationSpeed = speed;
                    adapter.setupAnim(state);
                    int current = cycle / 10;
                    float delta = cycle / 10.0F % 1.0F + partial / 10.0F;
                    for (var box : model.getCubes().values()) {
                        var previous = swim[(current + 3) % 4].seaserpent_model.getCube(box.boxName);
                        var next = swim[current].seaserpent_model.getCube(box.boxName);
                        near(box.rotateAngleX, box.defaultRotationX + Math.min(speed * 2, 1) * distance(box.defaultRotationX,
                            previous.rotateAngleX + delta * distance(previous.rotateAngleX, next.rotateAngleX)), "swim X");
                        near(box.rotateAngleY, box.defaultRotationY + Math.min(speed * 2, 1) * distance(box.defaultRotationY,
                            previous.rotateAngleY + delta * distance(previous.rotateAngleY, next.rotateAngleY)), "swim Y");
                        near(box.rotateAngleZ, box.defaultRotationZ + Math.min(speed * 2, 1) * distance(box.defaultRotationZ,
                            previous.rotateAngleZ + delta * distance(previous.rotateAngleZ, next.rotateAngleZ)), "swim Z");
                    }
                    nativePose(model, adapter.root());
                }
            }
        }

        for (AnimationKind kind : AnimationKind.values()) {
            int duration = kind == AnimationKind.ROAR ? 40 : 15;
            for (int tick = 0; tick <= duration; tick++) {
                for (float partial : new float[] {0, 0.5F, 0.99F}) {
                    SeaSerpentRenderState state = new SeaSerpentRenderState();
                    state.animation = kind;
                    state.animationTick = tick;
                    state.partialTick = partial;
                    adapter.setupAnim(state);
                    var first = snapshot(model);
                    adapter.setupAnim(idle);
                    compare(model, baseline, "return to idle");
                    adapter.setupAnim(state);
                    compare(model, first, "repeated keyframe");
                    nativePose(model, adapter.root());
                    check(model.llibAnimator.getEntity() == null, "animator retains no live entity");
                    if (kind == AnimationKind.SPEAK) {
                        float weight = tick < 5 ? sine((tick + partial) / 5)
                            : tick < 10 ? 1 : tick < 15 ? 1 - sine((tick - 10 + partial) / 5) : 0;
                        near(model.getCube("Jaw").rotateAngleX,
                            model.getCube("Jaw").defaultRotationX + weight * radians(25), "speech 5/5/5 timeline");
                    }
                    if (kind == AnimationKind.NONE || tick == duration) compare(model, baseline, "animation endpoint");
                }
            }
        }
        // First pose endpoints verify asset selection, shortest-angle math and pivot deltas.
        keyframePose(model, AnimationKind.BITE, 5, EnumSeaSerpentAnimations.BITE1);
        keyframePose(model, AnimationKind.ROAR, 10, EnumSeaSerpentAnimations.ROAR1);

        for (float progress : new float[] {0.5F, 5, 10}) {
            for (boolean want : new boolean[] {false, true}) {
                SeaSerpentRenderState state = new SeaSerpentRenderState();
                state.jumpProgress = want ? 0 : progress;
                state.wantJumpProgress = want ? progress : 0;
                adapter.setupAnim(state);
                var target = (want ? EnumSeaSerpentAnimations.JUMPING1 : EnumSeaSerpentAnimations.JUMPING2).seaserpent_model;
                float divisor = want ? 10 : 5; // Deliberate legacy overshoot: jumpProgress can reach 10.
                for (var box : model.getCubes().values()) {
                    var to = target.getCube(box.boxName);
                    float[] original = baseline.get(box.boxName);
                    boolean changed = to.rotateAngleX != box.defaultRotationX || to.rotateAngleY != box.defaultRotationY
                        || to.rotateAngleZ != box.defaultRotationZ;
                    near(box.rotateAngleX, original[0] + (changed ? distance(original[0], to.rotateAngleX) / divisor * progress : 0), "jump X");
                    near(box.rotateAngleY, original[1] + (changed ? distance(original[1], to.rotateAngleY) / divisor * progress : 0), "jump Y");
                    near(box.rotateAngleZ, original[2] + (changed ? distance(original[2], to.rotateAngleZ) / divisor * progress : 0), "jump Z");
                    near(box.rotationPointY, original[4] + (changed ? (to.rotationPointY - original[4]) / divisor * progress : 0), "jump pivot Y");
                }
            }
        }
        SeaSerpentRenderState breath = new SeaSerpentRenderState();
        breath.breathProgress = 20;
        adapter.setupAnim(breath);
        near(model.getCube("Head").rotateAngleX, radians(-15), "breathing head");
        near(model.getCube("HeadFront").rotateAngleX, radians(-20), "breathing front");
        near(model.getCube("Jaw").rotateAngleX, radians(60), "breathing jaw");

        for (boolean jumping : new boolean[] {false, true}) {
            for (boolean water : new boolean[] {false, true}) {
                SeaSerpentRenderState state = new SeaSerpentRenderState();
                state.jumpingOutOfWater = jumping;
                state.inWater = water;
                state.hasJumpRotation = true;
                state.jumpRotation = 0.65F;
                state.verticalVelocity = -0.35F;
                state.xRot = 17;
                state.tailBodyYaw = 179; // Do not wrap segment/body differences.
                for (int i = 0; i < 4; i++) {
                    state.pieceYaw[i] = -179 + i * 9;
                    state.piecePitch[i] = 11 + i * 4;
                }
                adapter.setupAnim(state);
                float turn = state.verticalVelocity * -4;
                near(model.getCube("BodyUpper").rotateAngleX, model.getCube("BodyUpper").defaultRotationX
                    + radians(22.5F * turn) * state.jumpRotation - state.xRot * ((float) Math.PI / 180), "jump body and pitch");
                for (int i = 0; i < 4; i++) {
                    var box = model.getCube("Tail" + (i + 1));
                    near(box.rotateAngleX, box.defaultRotationX - radians(turn) * state.jumpRotation
                        - (!jumping || water ? state.piecePitch[i] * ((float) Math.PI / 180) : 0), "tail pitch gate");
                    near(box.rotateAngleY, box.defaultRotationY
                        + (state.pieceYaw[i] - state.tailBodyYaw) * ((float) Math.PI / 180), "tail yaw unwrapped");
                }
                nativePose(model, adapter.root());
                state.hasJumpRotation = false;
                adapter.setupAnim(state);
                near(model.getCube("BodyUpper").rotateAngleX, model.getCube("BodyUpper").defaultRotationX
                    - state.xRot * ((float) Math.PI / 180), "current jump rotation gates interpolated rotation");
            }
        }
        SeaSerpentRenderState other = new SeaSerpentRenderState();
        other.pieceYaw[0] = 100;
        near(idle.pieceYaw[0], 0, "independent snapshot arrays");
        adapter.setupAnim(idle);
        compare(model, baseline, "final reuse reset");
        for (var pose : EnumSeaSerpentAnimations.values()) {
            for (var box : pose.seaserpent_model.getCubes().values()) {
                near(box.rotateAngleX, box.defaultRotationX, "pose asset remains immutable X");
                near(box.rotationPointY, box.defaultPositionY, "pose asset remains immutable Y");
            }
        }
        System.out.println("SeaSerpentModelTest: " + checks + " checks passed using all 16 real poses");
        System.out.println("LIMITS: no entity extraction, texture selection, layer submission, shadows or visual validation");
    }

    private static void keyframePose(TabulaModel<SeaSerpentRenderState> model, AnimationKind animation,
                                     int tick, EnumSeaSerpentAnimations pose) {
        SeaSerpentRenderState state = new SeaSerpentRenderState();
        state.animation = animation;
        state.animationTick = tick;
        model.setupAnim(state);
        for (var box : model.getCubes().values()) {
            var to = pose.seaserpent_model.getCube(box.boxName);
            boolean changed = to.rotateAngleX != box.defaultRotationX || to.rotateAngleY != box.defaultRotationY
                || to.rotateAngleZ != box.defaultRotationZ;
            near(box.rotateAngleX, box.defaultRotationX + (changed ? distance(box.defaultRotationX, to.rotateAngleX) : 0), "first keyframe pose X");
            near(box.rotateAngleY, box.defaultRotationY + (changed ? distance(box.defaultRotationY, to.rotateAngleY) : 0), "first keyframe pose Y");
            near(box.rotateAngleZ, box.defaultRotationZ + (changed ? distance(box.defaultRotationZ, to.rotateAngleZ) : 0), "first keyframe pose Z");
            boolean moved = to.rotationPointX != box.defaultPositionX || to.rotationPointY != box.defaultPositionY
                || to.rotationPointZ != box.defaultPositionZ;
            near(box.rotationPointY, moved ? to.rotationPointY : box.defaultPositionY + (box.boxName.equals("BodyUpper") ? 9 : 0), "first keyframe pivot Y");
        }
    }

    private static Map<String, float[]> snapshot(TabulaModel<?> model) {
        Map<String, float[]> result = new LinkedHashMap<>();
        model.getCubes().forEach((name, box) -> result.put(name, values(box)));
        return result;
    }

    private static float[] values(AdvancedModelBox box) {
        return new float[] {box.rotateAngleX, box.rotateAngleY, box.rotateAngleZ,
            box.rotationPointX, box.rotationPointY, box.rotationPointZ};
    }

    private static void compare(TabulaModel<?> model, Map<String, float[]> expected, String message) {
        model.getCubes().forEach((name, box) -> {
            float[] actual = values(box);
            for (int i = 0; i < actual.length; i++) near(actual[i], expected.get(name)[i], message + " " + name);
        });
    }

    private static void nativePose(TabulaModel<?> model, ModelPart root) {
        int index = 0;
        for (var part : model.parts()) nativePart(part, root.getChild("part" + index++));
    }

    private static void nativePart(BasicModelPart box, ModelPart part) {
        near(part.xRot, box.rotateAngleX, "native X rotation");
        near(part.yRot, box.rotateAngleY, "native Y rotation");
        near(part.zRot, box.rotateAngleZ, "native Z rotation");
        AdvancedModelBox advanced = (AdvancedModelBox) box;
        near(part.x, box.rotationPointX + advanced.offsetX, "native X pivot");
        near(part.y, box.rotationPointY + advanced.offsetY, "native Y pivot");
        near(part.z, box.rotationPointZ + advanced.offsetZ, "native Z pivot");
        for (int i = 0; i < box.childModels.size(); i++) {
            nativePart(box.childModels.get(i), part.getChild("children").getChild("part" + i));
        }
    }

    // Independent copy of the legacy approximation, not Math.atan2 (which changes the poses).
    private static float distance(float from, float to) {
        double y = Mth.sin(to - from), x = Mth.cos(to - from);
        boolean negative = y < 0;
        y = Math.abs(y);
        if (y == 0) y = 1.0E-25;
        double r = x > 0 ? (x - y) / (x + y) : (x + y) / (y - x);
        double result = 0.1963 * r * r * r - 0.9817 * r + (x > 0 ? Math.PI / 4 : Math.PI * 3 / 4);
        return (float) (negative ? -result : result);
    }

    private static float sine(float phase) { return Mth.sin((float) (phase * Math.PI / 2)); }
    private static float radians(float degrees) { return (float) Math.toRadians(degrees); }
    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001F, message + ": " + actual + " != " + expected);
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
