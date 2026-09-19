import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelCyclops;
import com.github.alexthe666.iceandfire.client.render.entity.CyclopsRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.CyclopsRenderState.AnimationKind;
import java.util.ArrayList;
import java.util.List;

/** Plain-main source-only probe: compile current model/state and port Citadel sources, not old mod binaries. */
public final class CyclopsModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelCyclops model = new ModelCyclops();
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        check(parts.size() == 26, "complete rig");
        check(parts.stream().mapToInt(p -> p.cubeList.size()).sum() == 26, "original cubes");
        check(model.texWidth == 128 && model.texHeight == 128, "atlas");
        check(model.Loin.childModels.contains(model.LoinBack), "loin hierarchy");
        check(model.Head.childModels.contains(model.Jaw), "jaw hierarchy");
        near(model.body.defaultPositionY, -3.5F, "root pivot");
        CyclopsRenderState state = new CyclopsRenderState();
        model.animate(state);
        float[] rest = pose(model);
        state.animation = AnimationKind.STOMP;
        state.animationTick = 3;
        state.partialTick = 0.5F;
        model.animate(state);
        near(model.rightleg.rotateAngleX, radians(-62) * (float) Math.sin(Math.PI / 4), "stomp easing and degrees");
        state.animationTick = 7;
        state.partialTick = 0;
        model.animate(state);
        near(model.rightleg.rotateAngleX, radians(-62), "stomp windup");
        near(model.rightleg2.rotationPointY - model.rightleg2.defaultPositionY, 1.1F, "stomp shin move");
        float[] held = pose(model);
        state.animationTick = 11;
        state.partialTick = 0.75F;
        model.animate(state);
        same(model, held, "stomp static hold");
        state.animationTick = 17;
        state.partialTick = 0;
        model.animate(state);
        near(model.body.rotationPointY - model.body.defaultPositionY, 1, "stomp impact movement");
        near(model.rightleg.rotateAngleX, radians(-46), "stomp impact rotation");
        state.animation = AnimationKind.KICK;
        state.animationTick = 10;
        model.animate(state);
        near(model.leftleg2.rotationPointY - model.leftleg2.defaultPositionY, 2.2F, "duplicate kick moves accumulate");
        near(model.leftleg2.rotationPointZ - model.leftleg2.defaultPositionZ, -2, "kick shin offset");
        state.animationTick = 15;
        model.animate(state);
        near(model.rightleg.rotateAngleX, radians(-55), "kick extension");
        state.animation = AnimationKind.EATPLAYER;
        state.animationTick = 10;
        model.animate(state);
        near(model.body.rotationPointY - model.body.defaultPositionY, 7, "eat crouch");
        near(model.rightarm.rotateAngleX - model.rightarm.defaultRotationX, radians(-80), "eat reach");
        state.animationTick = 25;
        model.animate(state);
        near(model.rightarm2.rotateAngleX - model.rightarm2.defaultRotationX, radians(-120), "eat lift");
        near(model.Head.rotationPointY - model.Head.defaultPositionY, -0.5F, "eat head lift");
        state.animationTick = 30;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(57), "eat jaw opening");
        state.animation = AnimationKind.ROAR;
        state.animationTick = 10;
        model.animate(state);
        near(model.Head.rotateAngleY, radians(20), "roar left");
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(45), "roar jaw");
        near(model.Loin.rotateAngleX, radians(-20), "roar loin follows front leg");
        near(model.LoinBack.rotateAngleX, 0, "roar rear loin leg difference");
        state.animationTick = 15;
        model.animate(state);
        near(model.Head.rotateAngleY, radians(-20), "roar right");
        for (AnimationKind kind : AnimationKind.values()) {
            state.animation = kind;
            state.animationTick = 40;
            model.animate(state);
            same(model, rest, kind + " completes");
            state.animationTick = 3;
            state.partialTick = 0.4F;
            model.animate(state);
            float[] active = pose(model);
            model.animate(state);
            same(model, active, kind + " keyframe reuse");
        }

        state = new CyclopsRenderState();
        state.ageInTicks = 32;
        state.walkAnimationPos = 8;
        state.walkAnimationSpeed = 0.7F;
        state.yRot = 30;
        state.xRot = 15;
        var adapter = model.asEntityModel();
        adapter.setupAnim(state);
        near(model.Head.rotateAngleY, radians(30), "head tracking yaw");
        near(model.Head.rotateAngleX, radians(15), "head tracking pitch");
        float walk = (float) Math.cos(8 * 0.2F) * 0.75F * 0.75F * 0.7F;
        near(model.rightleg.rotateAngleX, walk, "inverted negative-degree walk");
        near(model.leftleg.rotateAngleX, -walk, "opposite leg walk");
        near(model.rightleg2.rotateAngleX,
            -((float) Math.cos(8 * 0.2F + 1) * -0.375F * 0.7F - 0.3F * 0.7F),
            "inversion includes walk weight");
        near(model.UpperBody.rotateAngleX,
            -((float) Math.cos(32 * 0.05F) * -0.05F - 0.1F), "idle weighted walk");
        float[] moving = pose(model);
        adapter.setupAnim(state);
        same(model, moving, "adapter reuse");
        var root = adapter.root().getChild("part0");
        near(root.yRot, model.body.rotateAngleY, "native root rotation");
        near(root.y, model.body.rotationPointY, "native root pivot");
        state.animation = AnimationKind.EATPLAYER;
        state.animationTick = 25;
        adapter.setupAnim(state);
        float[] eating = pose(model);
        state.xRot = -60;
        state.yRot = -80;
        adapter.setupAnim(state);
        same(model, eating, "eat suppresses head tracking");
        near(model.Head.rotateAngleX, radians(-25), "eat retains keyframed head");
        state.animation = AnimationKind.NONE;
        state.ageInTicks = 0;
        state.walkAnimationPos = 0;
        state.walkAnimationSpeed = 0;
        state.xRot = 0;
        state.yRot = 0;
        adapter.setupAnim(state);
        near(model.body.rotationPointY, -3.5F, "root resets after eating");
        near(model.leftleg2.rotationPointY, model.leftleg2.defaultPositionY, "shin resets after kick");
        near(model.Loin.rotateAngleX, model.Loin.defaultRotationX, "loin resets after roar");
        float[] idle = pose(model);
        state.deathTime = 20;
        adapter.setupAnim(state);
        same(model, idle, "death roll remains renderer-owned");
        for (CyclopsRenderState.Variant variant : CyclopsRenderState.Variant.values()) {
            state.variant = variant;
            state.blinded = false;
            state.blinking = false;
            check(state.bodyTexture().equals(variant.texture), "normal texture");
            state.blinking = true;
            check(state.bodyTexture().equals(variant.blinkTexture), "blink texture");
            state.blinded = true;
            check(state.bodyTexture().equals(variant.blindedTexture), "blinded takes precedence");
        }
        model.animate(new CyclopsRenderState());
        same(model, rest, "different state clears animation bookkeeping");
        System.out.println("CyclopsModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, GPU, or legacy statue-renderer integration");
    }

    private static float[] pose(ModelCyclops model) {
        List<Float> values = new ArrayList<>();
        model.getAllParts().forEach(p -> {
            for (float v : new float[] {p.rotateAngleX, p.rotateAngleY, p.rotateAngleZ,
                p.rotationPointX, p.rotationPointY, p.rotationPointZ}) values.add(v);
        });
        float[] result = new float[values.size()];
        for (int i = 0; i < result.length; i++) result[i] = values.get(i);
        return result;
    }

    private static void same(ModelCyclops model, float[] expected, String message) {
        float[] actual = pose(model);
        check(actual.length == expected.length, message + " size");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], message + " component " + i);
    }

    private static float radians(float value) { return (float) Math.toRadians(value); }
    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0002F, message + ": " + actual + " != " + expected);
    }
    private static void check(boolean success, String message) {
        checks++;
        if (!success) throw new AssertionError(message);
    }
}
