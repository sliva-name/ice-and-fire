import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelDreadScuttler;
import com.github.alexthe666.iceandfire.client.render.entity.DreadScuttlerRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.DreadScuttlerRenderState.AnimationKind;

import java.util.ArrayList;
import java.util.List;

public final class DreadScuttlerModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelDreadScuttler model = new ModelDreadScuttler();
        List<AdvancedModelBox> parts = parts(model);
        check(parts.size() == 33, "original scuttler rig");
        check(model.parts().iterator().next() == model.Body2, "body2 root");
        check(AnimationKind.NONE.token() == IAnimatedEntity.NO_ANIMATION, "none token");
        check(AnimationKind.BITE.token().getDuration() == 15, "bite duration");
        check(AnimationKind.SPAWN.token().getDuration() == 40, "spawn duration");

        float[] rest = pose(model);
        DreadScuttlerRenderState spawn = new DreadScuttlerRenderState();
        spawn.animation = AnimationKind.SPAWN;
        spawn.animationTick = 0;
        model.animate(spawn);
        near(model.Body2.rotationPointY, model.Body2.defaultPositionY + 35, "spawn start +35");
        spawn.animationTick = 30;
        model.animate(spawn);
        near(model.Body2.rotationPointY, model.Body2.defaultPositionY, "spawn rise done");
        spawn.animationTick = 40;
        model.animate(spawn);
        same(model, rest, "spawn complete rest");

        DreadScuttlerRenderState bite = new DreadScuttlerRenderState();
        bite.animation = AnimationKind.BITE;
        bite.animationTick = 5;
        model.animate(bite);
        near(model.Neck1.rotateAngleX - model.Neck1.defaultRotationX, radians(-30), "bite windup neck");
        bite.animationTick = 10;
        model.animate(bite);
        near(model.Neck1.rotateAngleX - model.Neck1.defaultRotationX, radians(-70), "bite strike neck");
        bite.animationTick = 15;
        model.animate(bite);
        same(model, rest, "bite complete rest");

        model.setupAnim(new DreadScuttlerRenderState());
        float[] idle = pose(model);
        var adapter = model.asEntityModel();
        adapter.setupAnim(new DreadScuttlerRenderState());
        same(model, idle, "adapter idle rest");
        System.out.println("DreadScuttlerModelTest: " + checks + " checks passed");
    }

    private static List<AdvancedModelBox> parts(ModelDreadScuttler model) {
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        return parts;
    }

    private static float[] pose(ModelDreadScuttler model) {
        List<AdvancedModelBox> parts = parts(model);
        float[] pose = new float[parts.size() * 6];
        for (int i = 0; i < parts.size(); i++) {
            AdvancedModelBox part = parts.get(i);
            pose[i * 6] = part.rotateAngleX;
            pose[i * 6 + 1] = part.rotateAngleY;
            pose[i * 6 + 2] = part.rotateAngleZ;
            pose[i * 6 + 3] = part.rotationPointX;
            pose[i * 6 + 4] = part.rotationPointY;
            pose[i * 6 + 5] = part.rotationPointZ;
        }
        return pose;
    }

    private static void same(ModelDreadScuttler model, float[] expected, String message) {
        float[] actual = pose(model);
        check(actual.length == expected.length, message + " size");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], message + " component " + i);
    }

    private static float radians(float degrees) { return (float) Math.toRadians(degrees); }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0002F, message + ": " + actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
