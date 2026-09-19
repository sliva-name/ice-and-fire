import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelDreadGhoul;
import com.github.alexthe666.iceandfire.client.model.ModelDreadKnight;
import com.github.alexthe666.iceandfire.client.model.ModelDreadLich;
import com.github.alexthe666.iceandfire.client.model.ModelDreadThrall;
import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

import java.util.ArrayList;
import java.util.List;

/** Source-only probe: dread humanoid models against extracted 26.1 state, not live entities. */
public final class DreadHumanoidModelTest {
    private static int checks;

    public static void main(String[] args) {
        tokens();
        ghoul();
        knight();
        lich();
        thrall();
        skipDraw();
        System.out.println("DreadHumanoidModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no layer submission, GPU, live entity extraction, armor foil, or statue renderer.");
    }

    private static void tokens() {
        check(DreadHumanoidRenderState.SPAWN.getDuration() == 40, "spawn duration");
        check(DreadHumanoidRenderState.SLASH.getDuration() == 25, "slash duration");
        check(DreadHumanoidRenderState.SUMMON.getDuration() == 15, "summon duration");
        check(DreadHumanoidRenderState.SPAWN != DreadHumanoidRenderState.SLASH, "distinct tokens");
        check(IAnimatedEntity.NO_ANIMATION != DreadHumanoidRenderState.SPAWN, "none is not spawn");
    }

    private static void ghoul() {
        ModelDreadGhoul model = new ModelDreadGhoul(0);
        List<AdvancedModelBox> listed = listed(model);
        check(listed.size() == 7, "biped getAllParts remains 7");
        check(model.head.childModels.contains(model.head2), "head2 child");
        check(model.armLeft.childModels.contains(model.clawsLeft), "left claws");
        check(model.armRight.childModels.contains(model.clawsRight), "right claws");
        check(model.texWidth == 128 && model.texHeight == 64, "ghoul atlas");
        check(model.parts().iterator().next() == model.body, "body root");

        model.setupAnim(new DreadHumanoidRenderState());
        float[] rest = pose(model);
        DreadHumanoidRenderState spawn = state(DreadHumanoidRenderState.SPAWN, 0, 0);
        model.setupAnim(spawn);
        near(model.body.rotationPointY, model.body.defaultPositionY + 35, "spawn emerges from +35");
        spawn.animationTick = 30;
        model.setupAnim(spawn);
        near(model.body.rotationPointY, model.body.defaultPositionY, "spawn rise completes at 30");
        spawn.animationTick = 40;
        model.setupAnim(spawn);
        same(model, rest, "spawn complete is rest");

        DreadHumanoidRenderState slash = state(DreadHumanoidRenderState.SLASH, 5, 0);
        model.setupAnim(slash);
        near(model.armRight.rotateAngleX - model.armRight.defaultRotationX, radians(20), "slash first pose");
        near(model.body.rotateAngleY - model.body.defaultRotationY, radians(30), "slash torso");
        slash.animationTick = 10;
        model.setupAnim(slash);
        near(model.armRight.rotateAngleX - model.armRight.defaultRotationX, radians(-80), "slash strike");
        slash.animationTick = 25;
        model.setupAnim(slash);
        same(model, rest, "slash complete is rest");

        DreadHumanoidRenderState walk = new DreadHumanoidRenderState();
        walk.walkAnimationPos = 7.25F;
        walk.walkAnimationSpeed = 1;
        walk.tickCount = 40;
        model.setupAnim(walk);
        float expectedLeg = model.legRight.defaultRotationX
            + walkDelta(model.legRight, 0.6F, 1, false, 0, 0, 7.25F, 1);
        near(model.legRight.rotateAngleX, expectedLeg, "ghoul walk uses custom gait, not biped 0.6662");
        check(Math.abs(model.armRight.rotateAngleZ - (model.armRight.defaultRotationZ
            + Mth.cos(40 * 0.09F) * 0.05F + 0.05F)) > 0.001F, "ghoul does not apply biped idle arm bob");
    }

    private static void knight() {
        ModelDreadKnight model = new ModelDreadKnight(0);
        check(model.body.childModels.contains(model.cloak), "cloak on body");
        check(model.head.childModels.contains(model.crown), "crown on head");
        DreadHumanoidRenderState spawn = state(DreadHumanoidRenderState.SPAWN, 0, 0);
        model.setupAnim(spawn);
        near(model.body.rotationPointY, model.body.defaultPositionY, "knight has no spawn rise");
        DreadHumanoidRenderState walk = new DreadHumanoidRenderState();
        walk.walkAnimationPos = 3;
        walk.walkAnimationSpeed = 0.5F;
        walk.ageInTicks = 10;
        model.setupAnim(walk);
        near(model.armRight.rotateAngleZ, model.armRight.defaultRotationZ
            + Mth.cos(10 * 0.09F) * 0.05F + 0.05F, "knight keeps biped idle bob");
        walk.isPassenger = true;
        model.setupAnim(walk);
        near(model.legRight.rotateAngleX, -1.4137167F, "passenger sit legs");
        walk.isPassenger = false;
        walk.isCrouching = true;
        model.setupAnim(walk);
        near(model.body.rotateAngleX, 0.5F, "sneak torso");
        walk.isCrouching = false;
        walk.attackTime = 0.5F;
        walk.attackArm = HumanoidArm.RIGHT;
        model.setupAnim(walk);
        check(model.armRight.rotateAngleX < model.armRight.defaultRotationX, "attack swings the attacking arm");
    }

    private static void lich() {
        ModelDreadLich model = new ModelDreadLich(0);
        DreadHumanoidRenderState summon = state(DreadHumanoidRenderState.SUMMON, 2, 0);
        summon.ageInTicks = 12;
        model.setupAnim(summon);
        near(model.armRight.rotationPointX, -5.0F, "summon resets right arm pivot");
        near(model.armLeft.rotationPointX, 5.0F, "summon resets left arm pivot");
        near(model.armRight.rotateAngleZ, 2.3561945F, "summon right arm raise");
        near(model.armLeft.rotateAngleZ, -2.3561945F, "summon left arm raise");
        near(model.armRight.rotateAngleX, Mth.cos(12 * 0.6662F) * 0.25F, "summon pulse");
    }

    private static void thrall() {
        ModelDreadThrall model = new ModelDreadThrall(0, false);
        DreadHumanoidRenderState walk = new DreadHumanoidRenderState();
        walk.walkAnimationPos = 4;
        walk.walkAnimationSpeed = 1;
        walk.ageInTicks = 8;
        model.setupAnim(walk);
        float withoutFlap = walkPoseWithoutThrallFlap(model, walk);
        check(Math.abs(model.body.rotateAngleZ - withoutFlap) > 0.0001F, "thrall adds body flap on top of biped walk");

        ModelDreadThrall armor = new ModelDreadThrall(0.5F, true);
        armor.body.rotateAngleX = 1.25F;
        armor.setupAnim(walk);
        near(armor.body.rotateAngleX, 1.25F, "armor overlay setupAnim does not reset copied poses");
    }

    private static float walkPoseWithoutThrallFlap(ModelDreadThrall model, DreadHumanoidRenderState walk) {
        ModelDreadKnight knight = new ModelDreadKnight(0);
        knight.setupAnim(walk);
        return knight.body.rotateAngleZ;
    }

    private static void skipDraw() {
        ModelDreadThrall model = new ModelDreadThrall(0, true);
        model.setVisible(false);
        model.head.invisible = false;
        check(model.body.invisible, "hidden body");
        check(!model.head.invisible, "visible head");
        var adapter = model.asEntityModel();
        adapter.setupAnim(new DreadHumanoidRenderState());
        var body = adapter.root().getChild("part0");
        check(body.getChild("geometry").skipDraw, "invisible body skips native cubes");
        check(body.visible, "invisible body still transforms children");
    }

    private static float walkDelta(AdvancedModelBox box, float speed, float degree, boolean invert,
                                   float offset, float weight, float time, float amount) {
        // Citadel walk: cos(time * speed * scale + offset) * amount * degree, inverted optionally, plus weight.
        float scale = 1;
        float rotation = Mth.cos(time * speed * scale + offset) * amount * degree * scale;
        if (invert) rotation = -rotation;
        return rotation + weight * amount;
    }

    private static DreadHumanoidRenderState state(com.github.alexthe666.citadel.animation.Animation animation, int tick, float partial) {
        DreadHumanoidRenderState state = new DreadHumanoidRenderState();
        state.animation = animation;
        state.animationTick = tick;
        state.partialTick = partial;
        return state;
    }

    private static List<AdvancedModelBox> listed(com.github.alexthe666.iceandfire.client.model.ModelBipedBase model) {
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        return parts;
    }

    private static float[] pose(com.github.alexthe666.iceandfire.client.model.ModelBipedBase model) {
        List<AdvancedModelBox> parts = listed(model);
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

    private static void same(com.github.alexthe666.iceandfire.client.model.ModelBipedBase model, float[] expected, String message) {
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
