import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelAmphithere;
import com.github.alexthe666.iceandfire.client.render.entity.AmphithereRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.AmphithereRenderState.AnimationKind;
import java.util.ArrayList;
import java.util.List;

/** Plain-main source-only probe: compile current model/state and port Citadel sources, not old mod binaries. */
public final class AmphithereModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelAmphithere model = new ModelAmphithere();
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        check(parts.size() == 44, "complete rig");
        check(parts.stream().mapToInt(p -> p.cubeList.size()).sum() == 44, "original cubes");
        check(model.texWidth == 256 && model.texHeight == 128, "atlas");
        check(model.Tail4.childModels.contains(model.Club), "tail hierarchy");
        check(model.WingR3.childModels.contains(model.FingerR4), "wing hierarchy");
        near(model.BodyUpper.defaultPositionY, 15.8F, "root pivot");
        near(model.Jaw.scaleX, 0.99F, "jaw rest scale");
        AmphithereRenderState state = new AmphithereRenderState();
        model.animate(state);
        float[] rest = pose(model);
        state.animation = AnimationKind.BITE;
        state.animationTick = 2;
        state.partialTick = 0.5F;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX,
            radians(41) * (float) Math.sin(Math.PI / 4), "partial tick bite easing");
        state.partialTick = 0;
        state.animationTick = 5;
        model.animate(state);
        near(model.Neck1.rotateAngleX - model.Neck1.defaultRotationX, radians(-39), "bite windup degrees");
        state.animationTick = 10;
        model.animate(state);
        near(model.Neck2.rotateAngleX - model.Neck2.defaultRotationX, radians(23), "bite strike");
        state.animation = AnimationKind.BITE_RIDER;
        state.animationTick = 5;
        model.animate(state);
        near(model.Head.rotateAngleZ - model.Head.defaultRotationZ, radians(95), "rider bite twist");
        state.animation = AnimationKind.WING_BLAST;
        model.animate(state);
        near(model.WingR.rotateAngleZ, radians(170), "rotateMinus absolute wing target");
        near(model.BodyUpper.rotationPointY, model.BodyUpper.defaultPositionY - 36, "wing blast root movement");
        near(model.Tail1.rotateAngleX, 2 * radians(-10) - model.Tail1.defaultRotationX,
            "duplicate legacy Tail1 rotateMinus calls remain additive");
        state.animationTick = 10;
        model.animate(state);
        near(model.WingL.rotateAngleZ, radians(-45), "wing blast downstroke");
        state.animation = AnimationKind.TAIL_WHIP;
        model.animate(state);
        near(model.Tail2.rotateAngleY, radians(20), "duplicate tail whip rotation retained");
        state.animationTick = 15;
        model.animate(state);
        float[] whip = pose(model);
        near(model.BodyUpper.rotateAngleY, radians(-214), "unwrapped tail whip spin");
        state.animationTick = 17;
        state.partialTick = 0.7F;
        model.animate(state);
        same(model, whip, "static whip hold");
        state.animation = AnimationKind.SPEAK;
        state.animationTick = 5;
        state.partialTick = 0;
        model.animate(state);
        near(model.Jaw.rotateAngleX - model.Jaw.defaultRotationX, radians(31), "speech peak");
        for (AnimationKind kind : AnimationKind.values()) {
            state.animation = kind;
            state.animationTick = 40;
            model.animate(state);
            same(model, rest, kind + " completed");
            state.animationTick = 3;
            state.partialTick = 0.4F;
            model.animate(state);
            float[] active = pose(model);
            model.animate(state);
            same(model, active, kind + " repeatable");
        }

        state = new AmphithereRenderState();
        state.ageInTicks = 19;
        state.walkAnimationPos = 7;
        state.walkAnimationSpeed = 0.8F;
        var adapter = model.asEntityModel();
        adapter.setupAnim(state);
        float tail = model.Tail2.rotateAngleY;
        near(tail, (float) Math.cos(19 * 0.05F + Math.PI / 4) * 0.05F, "idle chain phase");
        float neck = model.Neck2.rotateAngleX;
        state.groundProgress = 10;
        adapter.setupAnim(state);
        near(model.Neck2.rotateAngleX - neck,
            (-0.4553564018453205F - model.Neck2.defaultRotationX) / 2, "half progress absolute radians");
        near(model.BodyUpper.rotationPointY, 16.9F, "half absolute position target");
        near(model.BodyUpper.rotationPointZ, -5, "position target subtracts rest pivot");
        near(model.Tail2.rotateAngleY - tail,
            (float) Math.cos(7 * 0.4F + Math.PI / 2) * 0.125F * 0.8F, "ground chain phase");
        state.groundProgress = 0;
        state.diveProgress = 20;
        adapter.setupAnim(state);
        near(model.WingR3.rotateAngleX, 1.48352986419518F, "full dive target");
        state.diveProgress = 0;
        state.sitProgress = 20;
        adapter.setupAnim(state);
        near(model.Tail2.rotateAngleY - tail, 0.36425021489121656F, "sit adds to idle");
        state.sitProgress = 0;
        state.flapProgress = 10;
        adapter.setupAnim(state);
        near(model.WingL.rotateAngleZ - model.WingL.defaultRotationZ,
            (float) Math.cos(19 * 0.2F) * 0.5F, "flap amplitude");
        float[] beforeBuffers = pose(model);
        float rootX = model.BodyUpper.rotateAngleX;
        float rootZ = model.BodyUpper.rotateAngleZ;
        state.captureBufferRotations(10, 30, -20, 20, 40, 80, 0.25F);
        adapter.setupAnim(state);
        near(model.BodyUpper.rotateAngleZ - rootZ, radians(15), "captured roll interpolation");
        near(model.BodyUpper.rotateAngleX - rootX, radians(-10), "captured wave interpolation");
        near(model.Tail2.rotateAngleY - tail, radians(50) / 4, "captured tail distributed over four parts");
        AmphithereRenderState other = new AmphithereRenderState();
        near(other.tailYaw[0], 0, "state arrays not shared");
        state.onGround = true;
        adapter.setupAnim(state);
        same(model, beforeBuffers, "ground suppresses stale buffers");
        state.onGround = false;
        state.animation = AnimationKind.WING_BLAST;
        state.animationTick = 5;
        adapter.setupAnim(state);
        float[] blast = pose(model);
        state.captureBufferRotations(0, 0, 0, 0, 0, 0, 0);
        adapter.setupAnim(state);
        same(model, blast, "wing blast suppresses buffers");
        state.animation = AnimationKind.NONE;
        state.isBaby = true;
        adapter.setupAnim(state);
        near(model.BodyUpper.scaleX, 0.5F, "baby root scale");
        near(model.Head.scaleX, 1.5F, "baby head scale");
        near(model.Jaw.offsetZ, -4.5F, "baby jaw offset");
        check(model.BodyUpper.scaleChildren, "baby descendant scaling");
        state.isBaby = false;
        adapter.setupAnim(state);
        near(model.BodyUpper.scaleX, 1, "adult scale restored");
        near(model.Jaw.offsetZ, 0, "adult jaw offset restored");
        near(model.HeadFront.offsetZ, 0, "adult face offset restored");
        check(!model.BodyUpper.scaleChildren, "adult scale policy restored");
        float[] adult = pose(model);
        adapter.setupAnim(state);
        same(model, adult, "adapter reuse");
        var nativeRoot = adapter.root().getChild("part0");
        near(nativeRoot.y, model.BodyUpper.rotationPointY, "native pivot synchronized");
        near(nativeRoot.zRot, model.BodyUpper.rotateAngleZ, "native rotation synchronized");
        for (AmphithereRenderState.Variant variant : AmphithereRenderState.Variant.values()) {
            state.variant = variant;
            state.blinking = false;
            check(state.bodyTexture().equals(variant.texture), "normal texture");
            state.blinking = true;
            check(state.bodyTexture().equals(variant.blinkTexture), "blink texture");
        }
        System.out.println("AmphithereModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, GPU, or legacy statue-renderer integration");
    }

    private static float[] pose(ModelAmphithere model) {
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

    private static void same(ModelAmphithere model, float[] expected, String message) {
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
