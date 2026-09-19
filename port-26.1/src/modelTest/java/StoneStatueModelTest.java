import com.github.alexthe666.iceandfire.client.model.ModelGuardianStatue;
import com.github.alexthe666.iceandfire.client.model.ModelHippocampus;
import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.entity.GuardianStatueRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.StoneStatuePose;
import com.github.alexthe666.iceandfire.client.render.entity.TrollRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/** Source-only statue pose checks against the 1.18 stone-statue contract. */
public final class StoneStatueModelTest {
    private static int checks;

    public static void main(String[] args) {
        scaleAndCrack();
        freezeVanilla();
        guardian();
        troll();
        hippogryphPasses();
        hippocampusPasses();
        System.out.println("StoneStatueModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, GPU, dispatcher lookup, or trapped-entity NBT.");
    }

    private static void scaleAndCrack() {
        near(StoneStatuePose.scale(1.25F), 1.25F, "normal statue scale");
        near(StoneStatuePose.scale(0.0F), 1.0F, "zero scale guard");
        near(StoneStatuePose.scale(0.009F), 1.0F, "sub-0.01 scale guard");
        near(StoneStatuePose.scale(0.01F), 0.01F, "0.01 is kept");
        check(StoneStatuePose.crackTexture(0) == null, "no crack texture below 1");
        check(StoneStatuePose.crackStage(1) == 0, "crack 1 is stage 0");
        check(StoneStatuePose.crackStage(10) == 9, "crack 10 is last stage");
        check(StoneStatuePose.crackStage(99) == 9, "crack clamps to last stage");
        check(StoneStatuePose.crackTexture(3).getPath().equals("textures/block/destroy_stage_2.png"), "crack 3 uses stage 2");
        check(StoneStatuePose.entityPath("iceandfire:troll").equals("troll"), "registry path");
        check(StoneStatuePose.entityPath("minecraft:pig").equals("pig"), "vanilla path");
    }

    private static void freezeVanilla() {
        LivingEntityRenderState state = new LivingEntityRenderState();
        state.walkAnimationPos = 4;
        state.walkAnimationSpeed = 0.8F;
        state.ageInTicks = 40;
        state.yRot = 90;
        state.xRot = 15;
        StoneStatuePose.freezeVanilla(state);
        near(state.walkAnimationPos, 0, "vanilla walk pos frozen");
        near(state.walkAnimationSpeed, 0, "vanilla walk speed frozen");
        near(state.ageInTicks, -0.1F, "vanilla age is the original -0.1 setupAnim tick");
        near(state.yRot, 0, "vanilla yaw frozen");
        near(state.xRot, 0, "vanilla pitch frozen");
    }

    private static void guardian() {
        ModelGuardianStatue model = new ModelGuardianStatue();
        GuardianStatueRenderState state = new GuardianStatueRenderState();
        state.poseAge = 12.5F;
        state.yRot = 30;
        state.xRot = -15;
        state.tailAnimation = 1.25F;
        model.setupAnim(state);
        near(model.body().rotateAngleY, 30 * 0.017453292F, "guardian body yaw");
        near(model.body().rotateAngleX, -15 * 0.017453292F, "guardian body pitch");
        near(model.eye().rotationPointZ, -8.25F, "guardian eye is parked, not aimed");
        check(model.eye().showModel, "guardian eye stays visible");
        near(model.spine(0).rotateAngleX, (float) Math.PI * 1.75F, "spine 0 X");
        float pulse = 1.0F + Mth.cos(12.5F * 1.5F + 2) * 0.01F - 0.55F;
        near(model.spine(2).rotationPointX, 8.0F * pulse, "spine 2 X pulse");
        near(model.spine(2).rotationPointY, 16.0F + -8.0F * pulse, "spine 2 Y pulse");
        near(model.tail(0).rotateAngleY, Mth.sin(1.25F) * (float) Math.PI * 0.05F, "tail 0");
        near(model.tail(1).rotationPointZ, 14.0F, "tail 1 z");
        near(model.tail(2).rotateAngleY, Mth.sin(1.25F) * (float) Math.PI * 0.15F, "tail 2");
        model.setupAnim(state);
        near(model.spine(2).rotationPointX, 8.0F * pulse, "guardian pose does not accumulate");
    }

    private static void troll() {
        ModelTroll model = new ModelTroll();
        TrollRenderState live = new TrollRenderState();
        live.statue = true;
        model.setupAnim(live);
        float[] viaState = pose(model);
        ModelTroll direct = new ModelTroll();
        StoneStatuePose.poseCitadel(direct, false, false);
        same(direct, viaState, "statue pose helper matches troll state.statue");
        check(!direct.log1.showModel, "troll statue hides weapon");
        StoneStatuePose.poseCitadel(direct, true, false);
        same(direct, viaState, "troll crack pass does not accumulate");
        check(direct instanceof com.github.alexthe666.iceandfire.client.model.ICustomStatueModel,
            "troll keeps the original statue interface");
    }

    private static void hippogryphPasses() {
        ModelHippogryph model = new ModelHippogryph();
        StoneStatuePose.poseCitadel(model, false, true);
        near(model.Body.scaleX, 1.0F, "first statue pass keeps adult hippogryph scale");
        check(model.Saddle.showModel, "first pass still has tack, as renderStatue hid it after drawing");
        StoneStatuePose.poseCitadel(model, true, true);
        near(model.Body.scaleX, 0.5F, "crack pass applies the after-draw baby scale");
        near(model.Head.scaleX, 1.5F, "crack pass baby head");
        near(model.Body.rotationPointY, 18.0F, "crack pass baby body pivot");
        check(!model.Saddle.showModel && !model.ReinL.showModel, "crack pass hides tack");
        StoneStatuePose.poseCitadel(model, false, false);
        near(model.Body.scaleX, 1.0F, "later first pass restores scale");
        check(!model.Saddle.showModel, "later first passes keep tack hidden on the cached model");
        ModelHippogryph fresh = new ModelHippogryph();
        StoneStatuePose.poseCitadel(fresh, false, true);
        check(fresh.Saddle.showModel, "the first statue of a type still has tack");
    }

    private static void hippocampusPasses() {
        ModelHippocampus model = new ModelHippocampus();
        StoneStatuePose.poseCitadel(model, false, false);
        check(model.Saddle.showModel, "first hippocampus pass still has tack");
        StoneStatuePose.poseCitadel(model, true, false);
        check(!model.Saddle.showModel && !model.NoseBand.showModel, "crack pass hides hippocampus tack");
    }

    private static float[] pose(ModelTroll model) {
        var parts = new java.util.ArrayList<com.github.alexthe666.citadel.client.model.AdvancedModelBox>();
        model.getAllParts().forEach(parts::add);
        float[] values = new float[parts.size() * 6];
        int i = 0;
        for (var part : parts) {
            values[i++] = part.rotateAngleX;
            values[i++] = part.rotateAngleY;
            values[i++] = part.rotateAngleZ;
            values[i++] = part.rotationPointX;
            values[i++] = part.rotationPointY;
            values[i++] = part.rotationPointZ;
        }
        return values;
    }

    private static void same(ModelTroll model, float[] expected, String name) {
        float[] actual = pose(model);
        check(actual.length == expected.length, name + " length");
        for (int i = 0; i < actual.length; i++) {
            near(actual[i], expected[i], name + " [" + i + "]");
        }
    }

    private static void near(float actual, float expected, String name) {
        check(Math.abs(actual - expected) < 0.0001F, name + " expected " + expected + " got " + actual);
    }

    private static void check(boolean condition, String name) {
        checks++;
        if (!condition) {
            throw new AssertionError(name);
        }
    }
}
