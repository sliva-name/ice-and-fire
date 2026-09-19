import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.entity.TrollRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.TrollRenderState.AnimationKind;
import java.util.ArrayList;
import java.util.List;

/** Plain-main source-only checks: compile with the port's Citadel sources, not old mod binaries. */
public final class TrollModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelTroll model = new ModelTroll();
        List<AdvancedModelBox> parts = new ArrayList<>();
        model.getAllParts().forEach(parts::add);
        check(parts.size() == 33, "complete rig");
        check(parts.stream().mapToInt(part -> part.cubeList.size()).sum() == 33, "original cubes");
        check(model.rightarm2.childModels.contains(model.log1), "weapon follows forearm");
        check(model.log1.childModels.contains(model.column) && model.log1.childModels.contains(model.handle),
            "weapon atlas alternatives retained");
        near(model.body.defaultPositionY, -2.4F, "body pivot");
        near(model.log1.defaultRotationX, -1.5934856F, "weapon rest angle");
        check(model.texWidth == 256 && model.texHeight == 128, "atlas dimensions");

        TrollRenderState state = new TrollRenderState();
        model.animate(state);
        float[] rest = pose(model);
        state.animation = AnimationKind.SPEAK;
        state.animationTick = 2;
        state.partialTick = 0.5F;
        model.animate(state);
        near(model.jaw.rotateAngleX - model.jaw.defaultRotationX,
            radians(25) * (float) Math.sin(Math.PI / 4), "speech partial-tick easing");
        state.animationTick = 5;
        state.partialTick = 0;
        model.animate(state);
        near(model.jaw.rotateAngleX - model.jaw.defaultRotationX, radians(25), "speech peak");

        state.animation = AnimationKind.ROAR;
        model.animate(state);
        near(model.leftleg.rotateAngleX, radians(-31), "roar absolute leg target");
        near(model.rightarm2.rotateAngleX, radians(-73), "roar rotateMinus forearm");
        near(model.body.rotationPointY, model.body.defaultPositionY + 2, "roar crouch");
        state.animationTick = 10;
        model.animate(state);
        near(model.head.rotateAngleY, radians(-28), "roar looks left");
        state.animationTick = 15;
        model.animate(state);
        near(model.head.rotateAngleY, radians(28), "roar looks right before recovery");

        state.animation = AnimationKind.STRIKE_HORIZONTAL;
        state.animationTick = 10;
        model.animate(state);
        near(model.rightarm.rotateAngleZ - model.rightarm.defaultRotationZ, radians(65), "horizontal windup");
        state.animationTick = 15;
        model.animate(state);
        near(model.rightarm.rotateAngleX - model.rightarm.defaultRotationX, radians(-60), "horizontal strike");
        near(model.log1.rotateAngleX - model.log1.defaultRotationX, radians(15), "horizontal weapon angle");

        state.animation = AnimationKind.STRIKE_VERTICAL;
        state.animationTick = 7;
        model.animate(state);
        near(model.leftarm.rotateAngleX - model.leftarm.defaultRotationX, radians(-203), "vertical windup");
        near(model.log1.rotationPointX - model.log1.defaultPositionX, 5, "vertical weapon windup pivot");
        state.animationTick = 12;
        model.animate(state);
        float[] impact = pose(model);
        near(model.log1.rotationPointX - model.log1.defaultPositionX, 2, "vertical impact pivot");
        near(model.log1.rotateAngleX - model.log1.defaultRotationX, radians(90), "vertical impact angle");
        state.animationTick = 14;
        state.partialTick = 0.75F;
        model.animate(state);
        samePose(model, impact, "vertical impact hold");

        for (AnimationKind kind : AnimationKind.values()) {
            state.animation = kind;
            state.animationTick = kind == AnimationKind.ROAR ? 25 : kind == AnimationKind.SPEAK ? 10 : 20;
            state.partialTick = 0;
            model.animate(state);
            samePose(model, rest, kind + " completes at rest");
            state.animationTick = 3;
            state.partialTick = 0.4F;
            model.animate(state);
            float[] animated = pose(model);
            model.animate(state);
            samePose(model, animated, kind + " does not accumulate");
        }

        state = new TrollRenderState();
        state.ageInTicks = 32;
        state.walkAnimationPos = 8;
        state.walkAnimationSpeed = 0.7F;
        state.yRot = 30;
        state.xRot = 15;
        var adapter = model.asEntityModel();
        adapter.setupAnim(state);
        float[] moving = pose(model);
        near(model.head.rotateAngleY, radians(30), "head tracking yaw");
        near(model.head.rotateAngleX - model.head.defaultRotationX, radians(15), "head tracking pitch");
        check(Math.abs(model.rightleg.rotateAngleX - model.rightleg.defaultRotationX) > 0.01F, "walking pose");
        adapter.setupAnim(state);
        samePose(model, moving, "adapter reevaluation");
        float jaw = model.jaw.rotateAngleX;
        float arm = model.leftarm.rotateAngleY;
        state.stoneProgress = 10;
        adapter.setupAnim(state);
        near(model.jaw.rotateAngleX - jaw, (radians(54) - model.jaw.defaultRotationX) / 2, "half stone jaw");
        near(model.leftarm.rotateAngleY - arm, radians(-73) / 2, "half stone arm");
        state.stoneProgress = 20;
        adapter.setupAnim(state);
        near(model.jaw.rotateAngleX - jaw, radians(54) - model.jaw.defaultRotationX, "full stone progress");

        state.statue = true;
        state.animation = AnimationKind.STRIKE_VERTICAL;
        state.animationTick = 7;
        adapter.setupAnim(state);
        float[] statue = pose(model);
        near(model.head.rotateAngleX, radians(-31), "statue head");
        near(model.jaw.rotateAngleX, radians(54), "statue jaw");
        near(model.leftarm.rotateAngleZ, radians(-60), "statue arm");
        near(model.rightarm2.rotateAngleX, radians(-40), "statue forearm");
        near(model.body.rotationPointY, model.body.defaultPositionY, "statue ignores attack");
        check(!model.log1.showModel, "statue hides entire weapon subtree");
        check(adapter.allParts().stream().anyMatch(part -> !part.visible), "native adapter receives visibility");
        model.animateStatue();
        samePose(model, statue, "direct statue pose matches state pose");
        model.animateStatue();
        samePose(model, statue, "statue and crack passes do not accumulate");

        TrollRenderState live = new TrollRenderState();
        adapter.setupAnim(live);
        check(model.log1.showModel, "weapon restored after statue");
        float[] livePose = pose(model);
        live.deathTime = 12.5F;
        adapter.setupAnim(live);
        samePose(model, livePose, "death roll remains renderer-owned, no new local death pose");
        live.weapon = null;
        adapter.setupAnim(live);
        check(model.log1.showModel && !live.hasWeapon(), "absent weapon suppresses layer, not body rig");
        for (TrollRenderState.Weapon weapon : TrollRenderState.Weapon.values()) {
            live.weapon = weapon;
            adapter.setupAnim(live);
            check(model.log1.showModel && live.hasWeapon(), "weapon selection " + weapon);
            check(weapon.texture.getPath().startsWith("textures/models/troll/weapon/weapon_"), "weapon asset path");
        }
        for (TrollRenderState.Variant variant : TrollRenderState.Variant.values()) {
            live.variant = variant;
            live.stone = false;
            check(live.bodyTexture().equals(variant.texture) && live.hasEyes(), "living texture and eyes");
            live.stone = true;
            adapter.setupAnim(live);
            check(live.bodyTexture().equals(variant.texture), "stone entity retains normal body atlas");
            check(!live.hasEyes() && !live.hasWeapon() && model.log1.showModel, "stone suppresses layers, not body rig");
            live.statue = true;
            adapter.setupAnim(live);
            check(live.bodyTexture().equals(variant.stoneTexture) && !model.log1.showModel, "statue atlas and hidden weapon");
            live.statue = false;
        }
        model.resetToDefaultPose();
        samePose(model, rest, "explicit full reset");
        check(model.log1.showModel, "explicit reset restores weapon visibility");
        System.out.println("TrollModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, GPU/layer submission, or legacy statue-renderer integration");
    }

    private static float[] pose(ModelTroll model) {
        List<Float> values = new ArrayList<>();
        model.getAllParts().forEach(part -> {
            values.add(part.rotateAngleX);
            values.add(part.rotateAngleY);
            values.add(part.rotateAngleZ);
            values.add(part.rotationPointX);
            values.add(part.rotationPointY);
            values.add(part.rotationPointZ);
        });
        float[] result = new float[values.size()];
        for (int i = 0; i < result.length; i++) result[i] = values.get(i);
        return result;
    }

    private static void samePose(ModelTroll model, float[] expected, String message) {
        float[] actual = pose(model);
        check(actual.length == expected.length, message + " part count");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], message + " component " + i);
    }

    private static float radians(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001F,
            message + ": " + actual + " != " + expected);
    }

    private static void check(boolean success, String message) {
        checks++;
        if (!success) throw new AssertionError(message);
    }
}
