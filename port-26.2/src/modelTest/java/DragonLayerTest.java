import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.animator.*;
import com.github.alexthe666.iceandfire.client.model.util.*;
import com.github.alexthe666.iceandfire.client.render.entity.DragonEyeCropper;
import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState;
import com.github.alexthe666.iceandfire.client.texture.ArrayLayeredTexture;
import net.minecraft.client.model.geom.ModelPart;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/** Plain main using real Tabula assets and source-built models; no live entity or GPU. */
public final class DragonLayerTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        DragonAnimationsLibrary.register(EnumDragonPoses.values(), EnumDragonModelTypes.values());
        for (var type : EnumDragonModelTypes.values()) testDragon(type);
        check(ArrayLayeredTexture.blend(0xFF123456, 0x00112233) == 0xFF123456, "transparent overlay");
        check(ArrayLayeredTexture.blend(0xFF123456, 0xFFABCDEF) == 0xFFABCDEF, "opaque overlay");
        check(ArrayLayeredTexture.blend(0xFF000000, 0x80FFFFFF) == 0xBF808080, "legacy squared alpha");
        var first = new DragonRenderState();
        var second = new DragonRenderState();
        second.prevAnimationProgresses[0] = 20;
        near(first.prevAnimationProgresses[0], 0, "independent state arrays");
        check(first.riders != second.riders && first.banner != second.banner, "independent layer state");
        System.out.println("DragonLayerTest: " + checks + " checks passed");
        System.out.println("LIMITS: no entity extraction, collector execution, rider suppression, GPU upload or visual validation");
    }

    private static void testDragon(EnumDragonModelTypes type) throws Exception {
        String kind = type.getModelType();
        String path = "/assets/iceandfire/models/tabula/" + kind + "dragon/" + kind + "dragon_Ground";
        DragonTabulaModelAnimator animator = switch (type) {
            case FIRE_DRAGON_MODEL -> new FireDragonTabulaModelAnimator();
            case ICE_DRAGON_MODEL -> new IceDragonTabulaModelAnimator();
            case LIGHTNING_DRAGON_MODEL -> new LightningTabulaDragonAnimator();
        };
        var container = TabulaModelHandlerHelper.loadTabulaModel(path);
        TabulaModel<DragonRenderState> model = new TabulaModel<>(container, animator);
        var nativeModel = model.asEntityModel();
        var idle = new DragonRenderState();
        idle.dragonType = type.ordinal();
        nativeModel.setupAnim(idle);
        var baseline = snapshot(model);
        for (var animation : DragonRenderState.AnimationKind.values()) {
            for (int tick = 0; tick <= animation.token().getDuration(); tick++) {
                var state = new DragonRenderState();
                state.dragonType = type.ordinal();
                state.animation = animation;
                state.animationTick = tick;
                state.partialTick = 0.5F;
                nativeModel.setupAnim(state);
                var expected = snapshot(model);
                nativePose(model, nativeModel.root());
                nativeModel.setupAnim(idle);
                compare(model, baseline);
                nativeModel.setupAnim(state);
                compare(model, expected);
                check(model.llibAnimator.getEntity() == null, "animator has no live entity");
            }
        }
        for (int mode = 0; mode < 3; mode++) {
            for (int cycle = 0; cycle <= 60; cycle++) {
                var state = new DragonRenderState();
                state.dragonType = type.ordinal();
                state.walkCycle = state.flightCycle = state.swimCycle = cycle;
                state.walkAnimationPos = cycle;
                state.walkAnimationSpeed = 0.6F;
                state.partialTick = 0.75F;
                state.ageInTicks = cycle + state.partialTick;
                state.flying = mode == 1;
                state.flyProgress = mode == 1 ? 20 : 0;
                state.isInWater = mode == 2;
                state.swimProgress = mode == 2 ? 20 : 0;
                nativeModel.setupAnim(state);
                nativePose(model, nativeModel.root());
                nativeModel.setupAnim(idle);
                compare(model, baseline);
            }
        }
        for (int transition = 0; transition < 9; transition++) {
            for (float progress : new float[] {0.5F, 5, 10, 20}) {
                var state = new DragonRenderState();
                state.partialTick = 0.5F;
                switch (transition) {
                    case 0 -> state.sleepProgress = progress;
                    case 1 -> state.sitProgress = progress;
                    case 2 -> state.hoverProgress = progress;
                    case 3 -> state.flyProgress = progress;
                    case 4 -> state.tackleProgress = progress;
                    case 5 -> state.diveProgress = progress;
                    case 6 -> state.fireBreathProgress = progress;
                    case 7 -> state.modelDeadProgress = progress;
                    case 8 -> state.ridingProgress = progress;
                }
                nativeModel.setupAnim(state);
                nativePose(model, nativeModel.root());
                nativeModel.setupAnim(idle);
                compare(model, baseline);
            }
        }
        var buffered = new DragonRenderState();
        buffered.turn = -0.4F;
        buffered.tail = 0.5F;
        buffered.tailPitch = 0.25F;
        buffered.bodyPitch = 0.3F;
        buffered.roll = -0.2F;
        nativeModel.setupAnim(buffered);
        near(model.getCube("Neck1").rotateAngleY, baseline.get("Neck1")[1] - 0.1F, "reversed neck swing");
        near(model.getCube("Tail1").rotateAngleY, baseline.get("Tail1")[1] + 0.1F, "tail swing");
        near(model.getCube("Tail1").rotateAngleX, baseline.get("Tail1")[0] - 0.05F, "tail pitch");
        near(model.getCube("BodyUpper").rotateAngleZ, baseline.get("BodyUpper")[2] - 0.2F, "body roll");
        nativeModel.setupAnim(idle);
        compare(model, baseline);

        TabulaModel<DragonRenderState> cropped = new TabulaModel<>(container);
        var chain = new HashSet<AdvancedModelBox>();
        for (var box = cropped.getCube("HeadFront"); box != null; box = box.getParent()) chain.add(box);
        DragonEyeCropper.retainChain(cropped, "HeadFront");
        for (var box : cropped.getAllParts()) {
            check(chain.contains(box) || box.cubeList.isEmpty(), "no off-chain eye geometry");
            for (var child : box.childModels) check(chain.contains(child), "cropped child chain");
        }
        var eyeModel = cropped.asEntityModel();
        eyeModel.setupAnim(idle);
        nativePose(cropped, eyeModel.root());
        check(!cropped.getCube("HeadFront").cubeList.isEmpty(), "head-front geometry retained");
        check(!model.getCube("Tail1").cubeList.isEmpty(), "cropping does not alter main model");
    }

    private static Map<String, float[]> snapshot(TabulaModel<?> model) {
        var result = new LinkedHashMap<String, float[]>();
        model.getCubes().forEach((name, box) -> result.put(name, values(box)));
        return result;
    }
    private static float[] values(AdvancedModelBox box) {
        return new float[] {box.rotateAngleX, box.rotateAngleY, box.rotateAngleZ,
            box.rotationPointX, box.rotationPointY, box.rotationPointZ, box.offsetX, box.offsetY, box.offsetZ};
    }
    private static void compare(TabulaModel<?> model, Map<String, float[]> expected) {
        model.getCubes().forEach((name, box) -> {
            float[] actual = values(box);
            for (int i = 0; i < actual.length; i++) near(actual[i], expected.get(name)[i], "deterministic pose " + name);
        });
    }
    private static void nativePose(TabulaModel<?> model, ModelPart root) {
        int index = 0;
        for (var box : model.parts()) nativePart(box, root.getChild("part" + index++));
    }
    private static void nativePart(BasicModelPart box, ModelPart part) {
        near(part.xRot, box.rotateAngleX, "native X rotation");
        near(part.yRot, box.rotateAngleY, "native Y rotation");
        near(part.zRot, box.rotateAngleZ, "native Z rotation");
        var advanced = (AdvancedModelBox) box;
        near(part.x, box.rotationPointX + advanced.offsetX, "native X pivot");
        near(part.y, box.rotationPointY + advanced.offsetY, "native Y pivot");
        near(part.z, box.rotationPointZ + advanced.offsetZ, "native Z pivot");
        for (int i = 0; i < box.childModels.size(); i++) nativePart(box.childModels.get(i), part.getChild("children").getChild("part" + i));
    }
    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001F, message + ": " + actual + " != " + expected);
    }
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
