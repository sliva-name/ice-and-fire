import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.client.render.entity.HippogryphRenderState;
import java.util.ArrayList;
import java.util.List;

/** Plain-main source-only probe: Hippogryph model/state against current Citadel, not old mod binaries. */
public final class HippogryphModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelHippogryph model = new ModelHippogryph();
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        check(parts.size() == 62, "complete rig");
        check(parts.stream().mapToInt(part -> part.cubeList.size()).sum() == 61, "original cubes");
        check(model.HeadPivot.cubeList.isEmpty(), "head pivot has no cube");
        check(model.texWidth == 256 && model.texHeight == 128, "atlas");
        check(model.Body.childModels.contains(model.Neck) && model.Neck.childModels.contains(model.Neck2), "neck chain");
        check(model.Saddle.childModels.contains(model.ChestL) && model.Saddle.childModels.contains(model.StirrupR),
            "saddle attachments");
        near(model.Body.defaultPositionY, 11, "body pivot");
        near(model.Jaw.defaultRotationX, -0.045553093477052F, "jaw rest");

        HippogryphRenderState state = new HippogryphRenderState();
        model.animate(state);
        float[] rest = pose(model);
        state.animation = HippogryphRenderState.SPEAK;
        state.animationTick = 2;
        state.partialTick = 0.5F;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX,
            radians(20) * (float) Math.sin(Math.PI / 8), "speak partial-tick easing");
        state.animationTick = 10;
        state.partialTick = 0;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(20), "speak jaw peak");
        near(model.Head.rotateAngleX - model.Head.defaultRotationX, radians(-10), "speak head peak");
        state.animationTick = 15;
        model.animate(state);
        same(model, rest, "speak completes at rest");

        state.animation = HippogryphRenderState.BITE;
        state.animationTick = 5;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(20), "bite windup jaw");
        state.animationTick = 10;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(45), "bite strike jaw");
        state.animationTick = 20;
        model.animate(state);
        same(model, rest, "bite completes at rest");

        state.animation = HippogryphRenderState.SCRATCH;
        state.animationTick = 5;
        model.animate(state);
        near(model.Body.rotateAngleX - model.Body.defaultRotationX, radians(-35), "scratch lean");
        state.animation = HippogryphRenderState.EAT;
        state.animationTick = 10;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(20), "eat jaw");
        state.animationTick = 25;
        model.animate(state);
        same(model, rest, "eat completes at rest");

        state = new HippogryphRenderState();
        var adapter = model.asEntityModel();
        state.sitProgress = 20;
        adapter.setupAnim(state);
        near(model.Body.rotationPointY, 17.5F, "adult sit height includes idle bob");
        near(model.WingL.rotateAngleZ, -0.45378560551852565F, "sit wing pose");

        state.sitProgress = 0;
        adapter.setupAnim(state);
        near(model.WingL.rotateAngleZ, model.WingL.defaultRotationZ, "grounded wings");

        state.hoverProgress = 20;
        adapter.setupAnim(state);
        near(model.WingL.rotateAngleZ, -1.3962634015954636F, "hover wing fold");

        state.sitProgress = 0;
        state.hoverProgress = 0;
        state.flying = true;
        state.ageInTicks = 8;
        adapter.setupAnim(state);
        float flyingWing = model.WingL.rotateAngleZ;
        check(Math.abs(flyingWing - model.WingL.defaultRotationZ) > 0.01F, "flight flap");
        state.dodo = true;
        adapter.setupAnim(state);
        check(Math.abs(model.WingL.rotateAngleZ - flyingWing) > 0.01F, "dodo flap amplitude");

        state.dodo = false;
        state.flying = false;
        state.isBaby = true;
        adapter.setupAnim(state);
        near(model.Body.scaleX, 0.5F, "baby body scale");
        near(model.Head.scaleX, 1.5F, "baby head scale");
        near(model.Beak.scaleX, 0.75F, "baby beak scale");
        check(model.Body.scaleChildren, "baby descendant scaling");
        check(!model.Head.scaleChildren, "baby head does not scale children");
        state.isBaby = false;
        adapter.setupAnim(state);
        near(model.Body.scaleX, 1, "adult body scale restored");
        near(model.Head.scaleX, 1, "adult head scale restored");
        near(model.Beak.scaleX, 1, "adult beak scale restored");
        check(!model.Body.scaleChildren, "adult scale policy restored");
        float[] adult = pose(model);
        adapter.setupAnim(state);
        same(model, adult, "adapter reuse");
        var nativeRoot = adapter.root().getChild("part0");
        near(nativeRoot.y, model.Body.rotationPointY, "native pivot synchronized");
        near(nativeRoot.zRot, model.Body.rotateAngleZ, "native rotation synchronized");

        System.out.println("HippogryphModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, GPU, saddle-layer, or statue-renderer integration");
    }

    private static float[] pose(ModelHippogryph model) {
        List<Float> values = new ArrayList<>();
        model.getAllParts().forEach(p -> {
            for (float v : new float[] {p.rotateAngleX, p.rotateAngleY, p.rotateAngleZ,
                p.rotationPointX, p.rotationPointY, p.rotationPointZ, p.offsetX, p.offsetY, p.offsetZ,
                p.scaleX, p.scaleY, p.scaleZ}) values.add(v);
        });
        float[] result = new float[values.size()];
        for (int i = 0; i < result.length; i++) result[i] = values.get(i);
        return result;
    }

    private static void same(ModelHippogryph model, float[] expected, String message) {
        float[] actual = pose(model);
        check(actual.length == expected.length, message + " size");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], message + " component " + i);
    }

    private static float radians(float value) {
        return (float) Math.toRadians(value);
    }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0002F, message + ": " + actual + " != " + expected);
    }

    private static void check(boolean success, String message) {
        checks++;
        if (!success) throw new AssertionError(message);
    }
}
