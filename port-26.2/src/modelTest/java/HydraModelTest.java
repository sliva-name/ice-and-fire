import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.model.ModelHydraHead;
import com.github.alexthe666.iceandfire.client.render.entity.HydraRenderState;

/** Source-only model regression checks; no live entity, old mod binary or GPU. */
public final class HydraModelTest {
    private static int checks;

    public static void main(String[] args) {
        for (int index = 0; index < HydraRenderState.MAX_HEADS; index++) {
            ModelHydraHead head = new ModelHydraHead(index);
            var nativeModel = head.asEntityModel();
            HydraRenderState state = new HydraRenderState();
            for (int count = 1; count <= 9; count++) {
                state.headCount = count;
                state.severedHead = -1;
                nativeModel.setupAnim(state);
                check(head.Neck1.showModel == (index < count), "head count visibility");
                check(head.Neck2.showModel, "intact neck");
                state.severedHead = index;
                nativeModel.setupAnim(state);
                check(!head.Neck2.showModel, "severed neck hides descendants");
                check(head.Neck1.showModel == (index < count), "stump retained");
                state.severedHead = -1;
                nativeModel.setupAnim(state);
                check(head.Neck2.showModel, "regrown head reappears");
            }
            state.headCount = 9;
            state.alive = false;
            nativeModel.setupAnim(state);
            check(!head.Neck2.showModel, "dead head hidden");
            state.alive = true;
            state.ageInTicks = 42;
            state.walkAnimationSpeed = 0.7F;
            state.walkAnimationPos = 8;
            head.setupAnim(state);
            float jaw = head.LowerJaw1.rotateAngleX;
            float tooth = head.TeethTR1.rotationPointX;
            state.strikingProgress[index] = 10;
            head.setupAnim(state);
            near(head.LowerJaw1.rotateAngleX - jaw,
                (float) Math.toRadians(45) - head.LowerJaw1.defaultRotationX, "strike jaw target");
            near(head.TeethTR1.rotationPointX - tooth, 0.5F, "strike tooth offset");
            state.breathProgress[index] = 10;
            head.setupAnim(state);
            near(head.TeethTR1.rotationPointX - tooth, 1, "strike and breath offsets add");
            state.strikingProgress[index] = 0;
            state.breathProgress[index] = 0;
            state.speakingProgress[index] = 0.5F;
            head.setupAnim(state);
            near(head.LowerJaw1.rotateAngleX - jaw,
                (float) Math.toRadians(25) - head.LowerJaw1.defaultRotationX, "speech jaw peak");
            state.speakingProgress[index] = 0;
            head.setupAnim(state);
            near(head.LowerJaw1.rotateAngleX, jaw, "reused pose does not accumulate");
            near(head.TeethTR1.rotationPointX, tooth, "reused tooth resets");
            state.stone = true;
            state.strikingProgress[index] = 10;
            state.breathProgress[index] = 10;
            nativeModel.setupAnim(state);
            head.getAllParts().forEach(part -> {
                near(part.rotateAngleX, part.defaultRotationX, "stone rotation X");
                near(part.rotateAngleY, part.defaultRotationY, "stone rotation Y");
                near(part.rotateAngleZ, part.defaultRotationZ, "stone rotation Z");
                near(part.rotationPointX, part.defaultPositionX, "stone pivot X");
            });
            state.severedHead = index;
            nativeModel.setupAnim(state);
            check(!head.Neck2.showModel, "stone pose still updates severed visibility");
        }
        ModelHydraBody body = new ModelHydraBody();
        HydraRenderState moving = new HydraRenderState();
        moving.walkAnimationPos = 12;
        moving.walkAnimationSpeed = 0.8F;
        moving.ageInTicks = 25;
        var nativeBody = body.asEntityModel();
        nativeBody.setupAnim(moving);
        float tail = body.Tail5.rotateAngleY;
        nativeBody.setupAnim(moving);
        near(body.Tail5.rotateAngleY, tail, "body animation repeatability");
        moving.stone = true;
        nativeBody.setupAnim(moving);
        body.getAllParts().forEach(part -> {
            near(part.rotateAngleX, part.defaultRotationX, "stone body X");
            near(part.rotateAngleY, part.defaultRotationY, "stone body Y");
            near(part.rotateAngleZ, part.defaultRotationZ, "stone body Z");
        });
        for (int invalid : new int[] {-1, 9}) {
            try {
                new ModelHydraHead(invalid);
                throw new AssertionError("invalid index accepted");
            } catch (IllegalArgumentException expected) {
                checks++;
            }
        }
        HydraRenderState first = new HydraRenderState();
        HydraRenderState second = new HydraRenderState();
        first.strikingProgress[0] = 10;
        near(second.strikingProgress[0], 0, "states own independent progress arrays");
        System.out.println("HydraModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no entity extraction, layer submission, statue integration or visual validation");
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
