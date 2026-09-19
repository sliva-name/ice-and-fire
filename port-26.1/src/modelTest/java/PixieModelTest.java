import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.model.ModelPixieHouse;
import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.github.alexthe666.iceandfire.client.render.tile.JarRenderState;
import com.github.alexthe666.iceandfire.client.render.tile.PixieHouseRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/** Plain-main source-only checks: no old mod entities, tiles, binaries, or GPU. */
public final class PixieModelTest {
    private static int checks;

    public static void main(String[] args) {
        ModelPixie model = new ModelPixie();
        var adapter = model.asEntityModel();
        PixieRenderState state = new PixieRenderState();
        state.walkAnimationPos = 3;
        state.walkAnimationSpeed = 0.8F;
        state.ageInTicks = 12.25F;
        state.yRot = 30;
        state.xRot = 15;
        adapter.setupAnim(state);
        float leftStride = Mth.cos(3 * 0.6662F + (float) Math.PI) * 0.8F * 0.5F;
        float rightStride = Mth.cos(3 * 0.6662F) * 0.8F * 0.5F;
        near(model.Left_Leg.rotateAngleX, leftStride, "left stride");
        near(model.Right_Leg.rotateAngleX, rightStride, "right stride");
        near(model.Right_Arm.rotateAngleX, leftStride, "opposed right arm stride");
        near(model.Left_Arm.rotateAngleX, rightStride, "opposed left arm stride");
        near(model.Body.rotateAngleX, radians(20), "flight lean clamps to twenty degrees");
        near(model.Head.rotateAngleX, -radians(20), "head counters flight lean");
        near(model.Head.rotateAngleY, 0, "empty-handed pixie does not track head yaw");
        checkWings(model, state.ageInTicks);
        checkAdapter(model.Body, adapter.root().getChild("part0"));

        state.holdingItem = true;
        adapter.setupAnim(state);
        near(model.Left_Arm.rotateAngleX, -radians(35), "holding left arm");
        near(model.Right_Arm.rotateAngleX, -radians(35), "holding right arm");
        near(model.Body.rotateAngleX, radians(30), "holding lean adds ten degrees");
        near(model.Head.rotateAngleX, -radians(15), "holding head pitch with lean compensation");
        near(model.Head.rotateAngleY, radians(30), "holding head yaw");
        near(model.Left_Leg.rotateAngleX, leftStride - radians(10), "holding left leg offset");
        near(model.Right_Leg.rotateAngleX, rightStride - radians(10), "holding right leg offset");
        state.sitting = true;
        adapter.setupAnim(state);
        checkSitting(model);
        near(model.Left_Arm.rotateAngleX, -radians(35) - (float) Math.PI / 5, "sitting holding arm offsets add");
        adapter.setupAnim(state);
        checkSitting(model);
        checkAdapter(model.Body, adapter.root().getChild("part0"));

        state.holdingItem = false;
        state.sitting = false;
        state.walkAnimationSpeed = 0;
        adapter.setupAnim(state);
        near(model.Head.rotateAngleY, 0, "head yaw resets after dropping item");
        near(model.Left_Arm.rotateAngleX, 0, "holding arm resets");
        near(model.Dress.rotationPointY, model.Dress.defaultPositionY, "dress pivot resets after sitting");
        near(model.Right_Leg.rotateAngleY, 0, "sitting leg spread resets");
        near(model.Left_Wing.rotateAngleZ, model.Left_Wing.defaultRotationZ, "folded wing resets");
        state.walkAnimationSpeed = -0.5F;
        adapter.setupAnim(state);
        near(model.Body.rotateAngleX, 0, "negative motion does not lean backwards");

        // Modes ignore stale entity-only motion and holding fields even before extraction clears them.
        state.mode = PixieRenderState.Mode.HOUSE;
        state.holdingItem = true;
        state.walkAnimationSpeed = 1;
        adapter.setupAnim(state);
        checkSitting(model);
        near(model.Body.rotateAngleX, 0, "house ignores entity lean");
        near(model.Head.rotateAngleY, 0, "house ignores entity head tracking");
        near(model.Left_Arm.rotateAngleX, -(float) Math.PI / 5, "house ignores held-item pose");
        state.mode = PixieRenderState.Mode.JAR;
        state.ageInTicks = 19.75F;
        adapter.setupAnim(state);
        near(model.Left_Arm.rotateAngleX, 0, "flying jar ignores entity arm swing");
        near(model.Left_Leg.rotateAngleX, 0, "flying jar ignores entity stride");
        checkWings(model, state.ageInTicks);
        state.sitting = true;
        adapter.setupAnim(state);
        checkSitting(model);
        near(model.Left_Wing.rotateAngleX, model.Left_Wing.defaultRotationX, "produced jar stops flapping");

        state.orderedToSit = true;
        state.setContainedPose(PixieRenderState.Mode.JAR, 5, 42.5F, false);
        check(!state.holdingItem && !state.orderedToSit && !state.sitting, "contained extraction clears flags");
        check(state.heldItem.isEmpty(), "contained state has no held item");
        check(state.mode == PixieRenderState.Mode.JAR && state.color == 5, "contained mode and color");
        near(state.ageInTicks, 42.5F, "extracted partial-tick age");
        near(state.walkAnimationSpeed, 0, "contained motion clears");
        near(state.walkAnimationPos, 0, "contained stride clears");
        near(state.yRot, 0, "contained yaw clears");
        near(state.xRot, 0, "contained pitch clears");
        adapter.setupAnim(state);
        checkWings(model, state.ageInTicks);
        checkAdapter(model.Body, adapter.root().getChild("part0"));
        PixieRenderState other = new PixieRenderState();
        check(other.heldItem != state.heldItem, "independent native item states");
        check(other.mode == PixieRenderState.Mode.ENTITY && !other.sitting, "independent pose state");

        // Native reset restores the construction pose; setupAnim then reapplies the extracted state.
        adapter.resetPose();
        near(adapter.root().getChild("part0").xRot, model.Body.defaultRotationX, "native adapter reset");
        adapter.setupAnim(state);
        checkAdapter(model.Body, adapter.root().getChild("part0"));
        checkGeometry(model, 12);
        near(model.Body.defaultPositionY, 16.9F, "pixie geometry root height");

        ModelPixieHouse house = new ModelPixieHouse();
        var houseAdapter = house.asEntityModel();
        checkGeometry(house, 6);
        check(house.stalk.childModels.size() == 3 && house.cap1.childModels.size() == 2, "house hierarchy retained");
        near(house.stalk.defaultPositionY, 24, "house geometry root height");
        near(house.stalk2.defaultRotationZ, -1.2292354F, "house branch rotation retained");
        near(house.grass.defaultRotationY, 0.5462881F, "house grass rotation retained");
        near(house.grass2.defaultRotationY, -2.6406832F, "second grass rotation retained");
        for (AdvancedModelBox part : house.getAllParts()) {
            part.rotateAngleX += 1;
            part.rotateAngleY += 2;
            part.rotateAngleZ += 3;
            part.rotationPointX += 4;
            part.rotationPointY += 5;
            part.rotationPointZ += 6;
        }
        houseAdapter.setupAnim(new EntityRenderState());
        for (AdvancedModelBox part : house.getAllParts()) {
            near(part.rotateAngleX, part.defaultRotationX, "house reset X");
            near(part.rotateAngleY, part.defaultRotationY, "house reset Y");
            near(part.rotateAngleZ, part.defaultRotationZ, "house reset Z");
            near(part.rotationPointX, part.defaultPositionX, "house reset pivot X");
            near(part.rotationPointY, part.defaultPositionY, "house reset pivot Y");
            near(part.rotationPointZ, part.defaultPositionZ, "house reset pivot Z");
        }
        checkAdapter(house.stalk, houseAdapter.root().getChild("part0"));
        for (int color = 0; color < 6; color++) {
            check(PixieRenderState.textureFor(color).toString().equals("iceandfire:textures/models/pixie/pixie_" + color + ".png"), "six pixie colors");
            check(PixieHouseRenderState.textureFor(color).toString().equals("iceandfire:textures/models/pixie/house/pixie_house_" + color + ".png"), "six house textures");
        }
        for (int invalid : new int[]{-1, 6, 99}) {
            check(PixieRenderState.textureFor(invalid).equals(PixieRenderState.textureFor(0)), "invalid pixie color fallback");
            check(PixieHouseRenderState.textureFor(invalid).equals(PixieHouseRenderState.textureFor(0)), "invalid house texture fallback");
        }
        near(PixieHouseRenderState.rotationFor(Direction.NORTH), 180, "north-facing house");
        near(PixieHouseRenderState.rotationFor(Direction.EAST), -90, "east-facing house");
        near(PixieHouseRenderState.rotationFor(Direction.SOUTH), 0, "south-facing house");
        near(PixieHouseRenderState.rotationFor(Direction.WEST), 90, "west-facing house");
        near(JarRenderState.interpolateRotation(170, -170, 0.5F), 180, "jar wraps positive across seam");
        near(JarRenderState.interpolateRotation(-170, 170, 0.5F), -180, "jar wraps negative across seam");
        near(JarRenderState.interpolateRotation(0, 180, 0.5F), -90, "jar preserves negative half-turn tie");
        near(JarRenderState.interpolateRotation(10, 50, 0), 10, "jar previous endpoint");
        near(JarRenderState.interpolateRotation(10, 50, 1), 50, "jar current endpoint");
        System.out.println("PixieModelTest: " + checks + " checks passed");
        System.out.println("LIMITS: no live extraction, item resolution, GPU submission, statue/TEISR integration or visual validation");
    }

    private static void checkSitting(ModelPixie model) {
        near(model.Right_Leg.rotateAngleX, -1.4137167F, "right sitting leg");
        near(model.Left_Leg.rotateAngleX, -1.4137167F, "left sitting leg");
        near(model.Right_Leg.rotateAngleY, (float) Math.PI / 10, "right sitting spread");
        near(model.Left_Leg.rotateAngleY, -(float) Math.PI / 10, "left sitting spread");
        near(model.Right_Leg.rotateAngleZ, 0.07853982F, "right sitting roll");
        near(model.Left_Leg.rotateAngleZ, -0.07853982F, "left sitting roll");
        near(model.Dress.rotateAngleX, -radians(50), "sitting dress");
        near(model.Dress.rotationPointY, model.Dress.defaultPositionY + 0.35F, "sitting dress Y");
        near(model.Dress.rotationPointZ, model.Dress.defaultPositionZ + 0.25F, "sitting dress Z");
        near(model.Left_Wing.rotateAngleZ, -radians(28), "left wing fold");
        near(model.Right_Wing.rotateAngleZ, radians(28), "right wing fold");
        near(model.Left_Wing2.rotateAngleZ, -radians(8), "second left wing fold");
        near(model.Right_Wing2.rotateAngleZ, radians(8), "second right wing fold");
    }

    private static void checkWings(ModelPixie model, float age) {
        float first = Mth.cos(age * 1.1F) * 0.75F;
        float second = Mth.cos(age * 1.1F + (float) (Math.PI / 4)) * 0.75F;
        near(model.Left_Wing.rotateAngleX, model.Left_Wing.defaultRotationX + first, "left wing phase");
        near(model.Right_Wing.rotateAngleX, model.Right_Wing.defaultRotationX + first, "right wing phase");
        near(model.Left_Wing2.rotateAngleX, model.Left_Wing2.defaultRotationX + second, "second left wing phase");
        near(model.Right_Wing2.rotateAngleX, model.Right_Wing2.defaultRotationX + second, "second right wing phase");
    }

    private static void checkAdapter(BasicModelPart source, ModelPart nativePart) {
        near(nativePart.x, source.rotationPointX, "adapter pivot X");
        near(nativePart.y, source.rotationPointY, "adapter pivot Y");
        near(nativePart.z, source.rotationPointZ, "adapter pivot Z");
        near(nativePart.xRot, source.rotateAngleX, "adapter rotation X");
        near(nativePart.yRot, source.rotateAngleY, "adapter rotation Y");
        near(nativePart.zRot, source.rotateAngleZ, "adapter rotation Z");
        check(nativePart.visible == source.showModel, "adapter visibility");
        for (int i = 0; i < source.childModels.size(); i++) {
            checkAdapter(source.childModels.get(i), nativePart.getChild("children").getChild("part" + i));
        }
    }

    private static void checkGeometry(AdvancedEntityModel<?> model, int expected) {
        int count = 0;
        for (AdvancedModelBox part : model.getAllParts()) {
            check(part.cubeList.size() == 1, "one preserved cube per model part");
            near(part.textureWidth, model.texWidth, "texture width");
            near(part.textureHeight, model.texHeight, "texture height");
            count++;
        }
        check(count == expected, "all model parts enumerated");
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
