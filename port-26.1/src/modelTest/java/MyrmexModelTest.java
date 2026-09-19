import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexBase;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexLarva;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexPupa;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexQueen;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexRoyal;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSentinel;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSoldier;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexWorker;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState.Caste;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/** Source-only, plain-main regression checks; no entities, singleton, stubs or old mod jar. */
public final class MyrmexModelTest {
    private static final float EPSILON = 0.0002F;
    private static long checks;

    public static void main(String[] args) {
        stateChecks();
        helperChecks();

        ModelMyrmexWorker worker = new ModelMyrmexWorker();
        ModelMyrmexSoldier soldier = new ModelMyrmexSoldier();
        ModelMyrmexSentinel sentinel = new ModelMyrmexSentinel();
        ModelMyrmexRoyal royal = new ModelMyrmexRoyal();
        ModelMyrmexQueen queen = new ModelMyrmexQueen();
        ModelMyrmexLarva larva = new ModelMyrmexLarva();
        ModelMyrmexPupa pupa = new ModelMyrmexPupa();
        Fixture[] fixtures = {
            new Fixture(worker, worker::animate, Caste.WORKER, 2,
                AnimationKind.NONE, AnimationKind.BITE, AnimationKind.STING),
            new Fixture(soldier, soldier::animate, Caste.SOLDIER, 2,
                AnimationKind.NONE, AnimationKind.BITE, AnimationKind.STING),
            new Fixture(sentinel, sentinel::animate, Caste.SENTINEL, 2,
                AnimationKind.NONE, AnimationKind.GRAB, AnimationKind.SENTINEL_STING,
                AnimationKind.NIBBLE, AnimationKind.SLASH),
            new Fixture(royal, royal::animate, Caste.ROYAL, 2,
                AnimationKind.NONE, AnimationKind.BITE, AnimationKind.STING),
            new Fixture(queen, queen::animate, Caste.QUEEN, 2,
                AnimationKind.NONE, AnimationKind.BITE, AnimationKind.STING,
                AnimationKind.EGG, AnimationKind.DIG_NEST),
            new Fixture(larva, larva::animate, Caste.WORKER, 0,
                AnimationKind.NONE, AnimationKind.PUPA_WIGGLE),
            new Fixture(pupa, pupa::animate, Caste.WORKER, 1,
                AnimationKind.NONE, AnimationKind.PUPA_WIGGLE)
        };
        for (Fixture fixture : fixtures) sweep(fixture);
        biteAndSting(fixtures[0], worker.Neck1, worker.HeadBase, worker.MandibleR,
            worker.Body2, worker.legMidR3, 25, -45, -4, 50);
        biteAndSting(fixtures[1], soldier.Neck1, soldier.HeadBase, soldier.MandibleR,
            soldier.Body2, soldier.legMidR3, 35, -50, -4, 50);
        biteAndSting(fixtures[3], royal.Neck1, royal.HeadBase, royal.MandibleR,
            royal.Body2, royal.legMidR3, 35, -50, -4, 50);
        biteAndSting(fixtures[4], queen.Neck1, queen.HeadBase, queen.MandibleR,
            queen.Body2, queen.legMidR3, 35, -50, -10, 30);
        sentinelChecks(fixtures[2], sentinel);
        royalChecks(fixtures[3], royal);
        queenChecks(fixtures[4], queen);
        juvenileChecks(fixtures[5], larva.Body1, larva.Body2, larva.Body4, 8);
        juvenileChecks(fixtures[6], pupa.Body1, pupa.Body2, pupa.Body4, 5);
        mouthAndPassengerChecks(fixtures[0], worker.Neck1, worker.HeadBase);
        mouthAndPassengerChecks(fixtures[1], soldier.Neck1, soldier.HeadBase);
        mouthAndPassengerChecks(fixtures[2], sentinel.Neck1, sentinel.HeadBase);
        mouthAndPassengerChecks(fixtures[3], royal.Neck1, royal.HeadBase);
        mouthAndPassengerChecks(fixtures[4], queen.Neck1, queen.HeadBase);
        for (int i = 5; i < fixtures.length; i++) {
            check(fixtures[i].model.getHeadParts().length == 0, "juvenile has no mouth chain");
            PoseStack poses = new PoseStack();
            fixtures[i].model.postRenderArm(0.0625F, poses);
            matrixNear(poses.last().pose(), new Matrix4f(), "juvenile mouth attachment is a no-op");
        }
        System.out.println("MyrmexModelTest: " + checks + " checks passed across all seven models");
        System.out.println("SCOPE: valid animation timelines/boundaries, fractional timing, all-part finite poses, "
            + "native adapter synchronization/reuse, helpers, mouth chain, passengers, flight, sentinel progress, "
            + "queen egg/dig and legacy juvenile/queen resets");
        System.out.println("LIMITS: no entity extraction, renderer selection, item resolution/submission, "
            + "statue rendering, GPU/texture/visual validation; boundary samples beyond token duration "
            + "exercise declared model timelines, not entity animation lifetime");
    }

    private static final class Fixture {
        final ModelMyrmexBase model;
        final Consumer<MyrmexRenderState> animate;
        final EntityModel<MyrmexRenderState> adapter;
        final Caste caste;
        final int growthStage;
        final AnimationKind[] animations;
        final String name;

        Fixture(ModelMyrmexBase model, Consumer<MyrmexRenderState> animate, Caste caste,
                int growthStage, AnimationKind... animations) {
            this.model = model;
            this.animate = animate;
            this.caste = caste;
            this.growthStage = growthStage;
            this.animations = animations;
            this.name = model.getClass().getSimpleName();
            this.adapter = model.asEntityModel();
        }

        MyrmexRenderState state(AnimationKind animation, int tick, float partial) {
            MyrmexRenderState state = new MyrmexRenderState();
            state.caste = caste;
            state.growthStage = growthStage;
            state.animation = animation;
            state.animationTick = tick;
            state.partialTick = partial;
            return state;
        }

        void keyframe(AnimationKind animation, int tick, float partial) {
            // Worker.animate does not reset itself; setupAnim normally owns that reset.
            model.resetToDefaultPose();
            animate.accept(state(animation, tick, partial));
        }
    }

    private static void stateChecks() {
        MyrmexRenderState first = new MyrmexRenderState();
        MyrmexRenderState second = new MyrmexRenderState();
        check(first.mouthItem instanceof ItemStackRenderState, "native mouth item state constructs without client");
        check(first.mouthItem != second.mouthItem, "mouth item state is per snapshot");
        check(first.caste == Caste.WORKER && first.growthStage == 2, "default adult worker");
        check(first.animation == AnimationKind.NONE && first.animationTick == 0, "default animation");
        near(first.modelScale, 1, "default model scale");
        check(first.texture != null, "default texture identifier");
        for (int stage : new int[] {-1, 0, 1, 2, 3}) {
            first.growthStage = stage;
            check(first.usesPupaModel() == (stage == 0 || stage == 1), "both juvenile slots select pupa");
        }
        int[] durations = {0, 20, 15, 15, 25, 15, 10, 25, 20, 45};
        AnimationKind[] kinds = AnimationKind.values();
        check(kinds.length == durations.length, "animation duration table covers all identities");
        for (int i = 0; i < kinds.length; i++) {
            first.animation = kinds[i];
            check(first.isLayingEgg() == (kinds[i] == AnimationKind.EGG), "egg predicate");
            check(first.isDiggingNest() == (kinds[i] == AnimationKind.DIG_NEST), "dig predicate");
            check(kinds[i].token() == kinds[i].token(), "stable animation identity");
            check(kinds[i].token().getDuration() == durations[i], "declared animation duration");
            for (int j = 0; j < i; j++) {
                check(kinds[i].token() != kinds[j].token(), "equal durations do not alias animation tokens");
            }
        }
        first.holdingProgress = 20;
        first.flyProgress = 20;
        first.hiding = true;
        near(second.holdingProgress, 0, "independent holding progress");
        near(second.flyProgress, 0, "independent flight progress");
        check(!second.hiding && second.animation == AnimationKind.NONE, "independent state flags");
    }

    private static void sweep(Fixture fixture) {
        int count = 0;
        Set<AdvancedModelBox> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        for (AdvancedModelBox part : fixture.model.getAllParts()) {
            check(unique.add(part), fixture.name + " unique getAllParts entries");
            count++;
        }
        check(count > 0, fixture.name + " nonempty model");
        for (AnimationKind animation : fixture.animations) {
            // Every integer tick includes both sides of every keyframe boundary, not just endpoints.
            // GRAB declares 10 frames for a 15-tick token; DIG_NEST declares 50 for a 45-tick token.
            int end = animation == AnimationKind.DIG_NEST ? 50 : animation.token().getDuration();
            for (int tick = 0; tick <= end + 1; tick++) {
                for (float partial : new float[] {0, 0.5F, 0.999F}) {
                    for (int profile = 0; profile < 4; profile++) {
                        MyrmexRenderState state = fixture.state(animation, tick, partial);
                        applyProfile(state, profile);
                        String context = fixture.name + " " + animation + " tick=" + tick
                            + "+" + partial + " profile=" + profile;
                        fixture.adapter.setupAnim(state);
                        float[] expected = snapshot(fixture, context);
                        fixture.adapter.setupAnim(state);
                        poseNear(snapshot(fixture, context), expected, context + " repeat");

                        MyrmexRenderState other = fixture.state(
                            fixture.animations[fixture.animations.length - 1], 7, 0.25F);
                        applyProfile(other, (profile + 2) % 4);
                        other.ageInTicks = 93.25F;
                        other.tickCount = 93;
                        fixture.adapter.setupAnim(other);
                        snapshot(fixture, context + " intervening pose");
                        fixture.adapter.setupAnim(state);
                        poseNear(snapshot(fixture, context), expected, context + " reused after other state");
                    }
                }
            }
        }
    }

    private static void applyProfile(MyrmexRenderState state, int profile) {
        if (profile == 0) return;
        state.ageInTicks = 37.25F;
        state.tickCount = 37;
        state.walkAnimationPos = 8.75F;
        state.walkAnimationSpeed = 0.7F;
        state.yRot = 48;
        state.xRot = -24;
        state.hasPassengers = profile == 2;
        if (state.caste == Caste.SENTINEL) {
            state.holding = profile == 2;
            state.hiding = profile == 3;
            state.holdingProgress = profile == 2 ? 20 : profile == 3 ? 7 : 0;
            state.hidingProgress = profile == 3 ? 20 : profile == 2 ? 6 : 0;
        }
        if (state.caste == Caste.ROYAL) {
            state.flying = profile >= 2;
            state.onGround = profile == 3;
            state.flyProgress = profile == 2 ? 20 : profile == 3 ? 9 : 0;
        }
    }

    private static float[] snapshot(Fixture fixture, String context) {
        List<Float> values = new ArrayList<>();
        Set<BasicModelPart> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        int index = 0;
        for (BasicModelPart root : fixture.model.parts()) {
            checkNative(root, fixture.adapter.root().getChild("part" + index++), visited, context);
        }
        for (AdvancedModelBox part : fixture.model.getAllParts()) {
            check(visited.remove(part), context + " part reachable in native tree");
            add(values, part.rotationPointX, part.rotationPointY, part.rotationPointZ,
                part.rotateAngleX, part.rotateAngleY, part.rotateAngleZ,
                part.scaleX, part.scaleY, part.scaleZ, part.offsetX, part.offsetY, part.offsetZ,
                part.showModel ? 1 : 0);
        }
        check(visited.isEmpty(), context + " getAllParts covers tree");
        for (ModelPart part : fixture.adapter.root().getAllParts()) {
            add(values, part.x, part.y, part.z, part.xRot, part.yRot, part.zRot,
                part.xScale, part.yScale, part.zScale, part.visible ? 1 : 0);
        }
        float[] result = new float[values.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = values.get(i);
            check(Float.isFinite(result[i]), context + " finite transform " + i);
        }
        return result;
    }

    private static void checkNative(BasicModelPart source, ModelPart target,
            Set<BasicModelPart> visited, String context) {
        check(visited.add(source), context + " model hierarchy is a tree");
        check(source instanceof AdvancedModelBox, context + " advanced model part");
        AdvancedModelBox part = (AdvancedModelBox) source;
        near(target.x, part.rotationPointX + part.offsetX, context + " native pivot X");
        near(target.y, part.rotationPointY + part.offsetY, context + " native pivot Y");
        near(target.z, part.rotationPointZ + part.offsetZ, context + " native pivot Z");
        near(target.xRot, part.rotateAngleX, context + " native rotation X");
        near(target.yRot, part.rotateAngleY, context + " native rotation Y");
        near(target.zRot, part.rotateAngleZ, context + " native rotation Z");
        check(target.visible == part.showModel, context + " native visibility");
        ModelPart geometry = target.getChild("geometry");
        near(geometry.xScale, part.scaleX, context + " native geometry scale X");
        near(geometry.yScale, part.scaleY, context + " native geometry scale Y");
        near(geometry.zScale, part.scaleZ, context + " native geometry scale Z");
        ModelPart children = target.getChild("children");
        near(children.xScale, part.scaleChildren ? part.scaleX : 1, context + " child scale X");
        near(children.yScale, part.scaleChildren ? part.scaleY : 1, context + " child scale Y");
        near(children.zScale, part.scaleChildren ? part.scaleZ : 1, context + " child scale Z");
        for (int i = 0; i < source.childModels.size(); i++) {
            checkNative(source.childModels.get(i), children.getChild("part" + i), visited, context);
        }
    }

    private static void helperChecks() {
        ModelMyrmexWorker model = new ModelMyrmexWorker();
        AdvancedModelBox part = model.Neck1;
        model.progressRotation(part, 10, 1, 2, 3);
        rotationNear(part, (part.defaultRotationX + 1) / 2, (part.defaultRotationY + 2) / 2,
            (part.defaultRotationZ + 3) / 2, "half rotation uses default-relative target");
        model.resetToDefaultPose();
        model.progressRotationPrev(part, 10, 1, 2, 3);
        rotationNear(part, part.defaultRotationX + 0.5F, part.defaultRotationY + 1,
            part.defaultRotationZ + 1.5F, "previous rotation adds offsets");
        model.resetToDefaultPose();
        model.progressRotationInterp(part, 3, 1, 2, 3, 6);
        rotationNear(part, (part.defaultRotationX + 1) / 2, (part.defaultRotationY + 2) / 2,
            (part.defaultRotationZ + 3) / 2, "custom rotation denominator");
        model.resetToDefaultPose();
        model.progressPosition(part, 10, 4, 6, 8);
        positionNear(part, (part.defaultPositionX + 4) / 2, (part.defaultPositionY + 6) / 2,
            (part.defaultPositionZ + 8) / 2, "position target uses default position");
        model.resetToDefaultPose();
        model.progressPositionPrev(part, 10, 4, 6, 8);
        positionNear(part, part.defaultPositionX + 2, part.defaultPositionY + 3,
            part.defaultPositionZ + 4, "previous position adds offsets");
        model.resetToDefaultPose();
        model.progressPositionInterp(part, 3, 4, 6, 8, 6);
        positionNear(part, part.defaultPositionX + 2, part.defaultPositionY + 3,
            part.defaultPositionZ + 4, "interpolated position is offset, not absolute target");
        model.resetToDefaultPose();
        model.faceTarget(80, -40, 2, part, model.HeadBase);
        near(part.rotateAngleY - part.defaultRotationY, radians(20), "face yaw divided over two parts");
        near(part.rotateAngleX - part.defaultRotationX, radians(-10), "face pitch divided over two parts");
        near(model.HeadBase.rotateAngleY - model.HeadBase.defaultRotationY, radians(20), "head shares yaw");
        for (boolean minus : new boolean[] {false, true}) {
            model.resetToDefaultPose();
            // Distinguish rotateMinus's captured default from the current pose.
            part.rotateAngleX += 0.2F;
            ModelAnimator animator = ModelAnimator.create();
            animator.update(AnimationKind.BITE.token(), 5, 0);
            check(animator.setAnimation(AnimationKind.BITE.token()), "helper animation selected");
            animator.startKeyframe(5);
            if (minus) model.rotateMinus(animator, part, 30, 40, 50);
            else model.rotate(animator, part, 30, 40, 50);
            animator.endKeyframe();
            animator.setStaticKeyframe(1);
            rotationNear(part, (minus ? 0 : part.defaultRotationX) + 0.2F + radians(30),
                (minus ? 0 : part.defaultRotationY) + radians(40),
                (minus ? 0 : part.defaultRotationZ) + radians(50), "degree rotation helper minus=" + minus);
        }
    }

    private static void biteAndSting(Fixture fixture, AdvancedModelBox neck, AdvancedModelBox head,
            AdvancedModelBox mandible, AdvancedModelBox body, AdvancedModelBox rearMid,
            float open, float close, float lift, float rearAngle) {
        fixture.keyframe(AnimationKind.BITE, 0, 0);
        near(neck.rotateAngleX, neck.defaultRotationX, fixture.name + " bite starts at default");
        fixture.keyframe(AnimationKind.BITE, 2, 0.5F);
        near(neck.rotateAngleX, neck.defaultRotationX + radians(-50) * Mth.sin((float) Math.PI / 4),
            fixture.name + " bite uses extracted partial tick and sine easing");
        fixture.keyframe(AnimationKind.BITE, 5, 0);
        near(neck.rotateAngleX, neck.defaultRotationX + radians(-50), fixture.name + " bite first boundary");
        near(head.rotateAngleX, head.defaultRotationX + radians(50), fixture.name + " bite head counter-rotation");
        near(mandible.rotateAngleY, mandible.defaultRotationY + radians(open), fixture.name + " bite opens mandible");
        fixture.keyframe(AnimationKind.BITE, 10, 0);
        near(neck.rotateAngleX, neck.defaultRotationX + radians(30), fixture.name + " bite second boundary");
        near(mandible.rotateAngleY, mandible.defaultRotationY + radians(close), fixture.name + " bite closes mandible");
        fixture.keyframe(AnimationKind.BITE, 15, 0);
        near(neck.rotateAngleX, neck.defaultRotationX, fixture.name + " bite reset complete");
        fixture.keyframe(AnimationKind.STING, 5, 0);
        near(body.rotationPointY, body.defaultPositionY + lift, fixture.name + " sting lift");
        near(rearMid.rotateAngleZ, radians(rearAngle), fixture.name + " sting absolute leg target");
        fixture.keyframe(AnimationKind.STING, 10, 0);
        near(body.rotationPointY, body.defaultPositionY + lift * (1 - Mth.sin((float) Math.PI / 4)),
            fixture.name + " ten-tick sting reset interpolation");
        fixture.keyframe(AnimationKind.STING, 15, 0);
        near(body.rotationPointY, body.defaultPositionY, fixture.name + " sting reset complete");
    }

    private static void sentinelChecks(Fixture fixture, ModelMyrmexSentinel model) {
        fixture.keyframe(AnimationKind.GRAB, 5, 0);
        near(model.Body1.rotateAngleX, radians(-65), "sentinel grab absolute body target");
        near(model.legMidR1.rotateAngleX, radians(-41), "sentinel grab arm target");
        fixture.keyframe(AnimationKind.GRAB, 10, 0);
        near(model.Body1.rotateAngleX, model.Body1.defaultRotationX, "grab pose ends before 15-tick token");
        fixture.keyframe(AnimationKind.SENTINEL_STING, 5, 0);
        near(model.Body2.rotateAngleY, radians(-31), "sentinel sting left boundary");
        fixture.keyframe(AnimationKind.SENTINEL_STING, 15, 0);
        near(model.Body2.rotateAngleY, radians(31), "sentinel sting right boundary");
        fixture.keyframe(AnimationKind.NIBBLE, 5, 0);
        near(model.MandibleR.rotateAngleY, model.MandibleR.defaultRotationY + radians(35), "nibble first target");
        fixture.keyframe(AnimationKind.NIBBLE, 10, 0);
        near(model.MandibleR.rotateAngleY, model.MandibleR.defaultRotationY, "nibble has no hold beyond timeline");
        fixture.keyframe(AnimationKind.SLASH, 5, 0);
        near(model.legMidR1.rotateAngleX, radians(-98), "slash right arm first target");
        fixture.keyframe(AnimationKind.SLASH, 10, 0);
        near(model.legMidR1_1.rotateAngleX, radians(-98), "slash left arm second target");

        MyrmexRenderState state = fixture.state(AnimationKind.NONE, 0, 0);
        fixture.adapter.setupAnim(state);
        float arm = model.legTopR1.rotateAngleX;
        float[] baseline = snapshot(fixture, "sentinel baseline");
        state.holding = true;
        fixture.adapter.setupAnim(state);
        poseNear(snapshot(fixture, "holding flag only"), baseline, "holding flag alone does not drive progress");
        for (float progress : new float[] {0, 5, 10, 20}) {
            state.holdingProgress = progress;
            fixture.adapter.setupAnim(state);
            near(model.legTopR1.rotateAngleX, arm + progress / 20 * (radians(35) - model.legTopR1.defaultRotationX),
                "holding progress adds to idle pose");
        }
        state.hidingProgress = 10;
        fixture.adapter.setupAnim(state);
        near(model.legTopR1.rotateAngleX, arm + radians(35) - model.legTopR1.defaultRotationX
            + 0.5F * (radians(70) - model.legTopR1.defaultRotationX), "holding and hiding progress add");
        near(model.Body2.rotationPointY, (model.Body2.defaultPositionY + 17) / 2, "half hiding body position");
        state.holdingProgress = 0;
        state.hidingProgress = 20;
        state.hiding = true;
        fixture.adapter.setupAnim(state);
        near(model.Body2.rotationPointY, 17, "full hiding body target");
        near(model.HeadBase.rotationPointZ, -4.4F, "full hiding head position");
        near(model.Tail1.rotateAngleX, radians(-20) + 0.1F * 0.15F, "hiding flag lowers idle amplitude");
        state.hidingProgress = 0;
        state.ageInTicks = 37;
        fixture.adapter.setupAnim(state);
        near(model.Tail1.rotateAngleX, model.Tail1.defaultRotationX + Mth.cos(37 * 0.015F) * 0.1F * 0.15F,
            "hiding flag changes idle speed independently of progress");
    }

    private static void royalChecks(Fixture fixture, ModelMyrmexRoyal model) {
        MyrmexRenderState state = fixture.state(AnimationKind.NONE, 0, 0);
        state.walkAnimationSpeed = 1;
        fixture.adapter.setupAnim(state);
        float groundedLeg = model.legTopR1.rotateAngleX;
        state.flying = true;
        fixture.adapter.setupAnim(state);
        near(model.wingL.rotateAngleX, model.wingL.defaultRotationX + 0.75F, "airborne first wing wave");
        near(model.wingR.rotateAngleX, model.wingR.defaultRotationX + 0.75F, "airborne mirrored wing wave");
        near(model.legTopR1.rotateAngleX, model.legTopR1.defaultRotationX, "airborne skips walking legs");
        near(model.Body2.rotationPointY, model.Body2.defaultPositionY, "legacy flight bob has zero amount");
        state.onGround = true;
        fixture.adapter.setupAnim(state);
        near(model.wingL.rotateAngleX, model.wingL.defaultRotationX, "onGround suppresses flight wave");
        near(model.legTopR1.rotateAngleX, groundedLeg, "onGround restores walking even if flying");
        state.walkAnimationSpeed = 0;
        state.flying = false;
        for (float progress : new float[] {0, 5, 10, 20}) {
            state.flyProgress = progress;
            fixture.adapter.setupAnim(state);
            near(model.Body2.rotationPointY, model.Body2.defaultPositionY
                + progress / 20 * (-8 - model.Body2.defaultPositionY), "flight progress absolute body target");
            near(model.wingL.rotateAngleY, model.wingL.defaultRotationY
                + progress / 20 * (radians(60) - model.wingL.defaultRotationY), "flight progress independent of flying flag");
        }
    }

    private static void queenChecks(Fixture fixture, ModelMyrmexQueen model) {
        fixture.keyframe(AnimationKind.DIG_NEST, 15, 0);
        near(model.Body2.rotateAngleX, radians(26), "queen dig first target");
        for (int[] point : new int[][] {{25, 5}, {30, 30}, {35, 80}, {45, 80}, {50, 0}}) {
            fixture.keyframe(AnimationKind.DIG_NEST, point[0], 0);
            near(model.Body2.rotationPointY, model.Body2.defaultPositionY + point[1], "queen declared dig pivot boundary " + point[0]);
        }
        MyrmexRenderState egg = fixture.state(AnimationKind.EGG, 10, 0);
        fixture.keyframe(AnimationKind.EGG, 10, 0);
        near(model.Tail1.scaleX, 1.75F, "egg animate alone swells tail");
        fixture.adapter.setupAnim(egg);
        for (AdvancedModelBox part : new AdvancedModelBox[] {model.Body5, model.Tail1, model.Tail2, model.Tail3}) {
            near(part.scaleX, 1, "legacy setup discards egg scale X");
            near(part.scaleY, 1, "legacy setup discards egg scale Y");
            near(part.scaleZ, 1, "legacy setup discards egg scale Z");
        }
        near(model.Stinger.rotationPointZ, model.Stinger.defaultPositionZ
            + 10 * Math.abs(Mth.sin((float) Math.PI / 2 - 1)), "egg stinger movement survives scale reset");
        MyrmexRenderState idle = fixture.state(AnimationKind.NONE, 0, 0);
        idle.tickCount = 13;
        idle.ageInTicks = 99.75F;
        fixture.adapter.setupAnim(idle);
        float swell1 = -0.05F + 0.2F * Math.abs(Mth.sin(13 * 0.15F + 1));
        float swell3 = -0.05F + 0.2F * Math.abs(Mth.sin(13 * 0.15F));
        near(model.Tail1.scaleX, 1 + swell1, "queen idle scale uses integer tickCount");
        near(model.Tail3.scaleZ, 1 + swell3, "queen third tail swell phase");
        near(model.Stinger.rotationPointZ, model.Stinger.defaultPositionZ + 20 * swell3, "queen idle stinger swell");
        idle.ageInTicks = 0;
        fixture.adapter.setupAnim(idle);
        near(model.Tail1.scaleX, 1 + swell1, "ageInTicks does not replace tickCount for scale");
        model.increaseScale(model.Body5, 0.25F);
        near(model.Body5.scaleX, 1.25F, "increaseScale adds rather than multiplies");
        near(model.Body5.scaleY, 1.25F, "increaseScale Y");
        near(model.Body5.scaleZ, 1.25F, "increaseScale Z");
        fixture.adapter.setupAnim(idle);
        near(model.Body5.scaleX, 1, "queen setup resets prior explicit scaling");
    }

    private static void juvenileChecks(Fixture fixture, AdvancedModelBox body1,
            AdvancedModelBox body2, AdvancedModelBox body4, int chainLength) {
        for (int[] point : new int[][] {{5, -15}, {10, 15}, {15, -15}, {20, 0}}) {
            fixture.keyframe(AnimationKind.PUPA_WIGGLE, point[0], 0);
            near(body1.rotateAngleY, body1.defaultRotationY + radians(point[1]), fixture.name + " raw wiggle boundary");
            near(body4.rotateAngleY, body4.defaultRotationY - radians(point[1]), fixture.name + " raw opposite wiggle");
        }
        MyrmexRenderState idle = fixture.state(AnimationKind.NONE, 0, 0);
        idle.ageInTicks = 23.5F;
        fixture.adapter.setupAnim(idle);
        float[] baseline = snapshot(fixture, fixture.name + " idle");
        for (int tick : new int[] {0, 4, 5, 9, 10, 14, 15, 19, 20}) {
            MyrmexRenderState wiggle = fixture.state(AnimationKind.PUPA_WIGGLE, tick, 0.5F);
            wiggle.ageInTicks = idle.ageInTicks;
            fixture.adapter.setupAnim(wiggle);
            poseNear(snapshot(fixture, fixture.name + " setup wiggle"), baseline,
                fixture.name + " legacy setup resets keyframe before idle motion");
        }
        near(body1.rotateAngleY, body1.defaultRotationY + Mth.cos(23.5F * 0.025F) * 0.25F * 0.15F,
            fixture.name + " idle first chain swing");
        near(body2.rotateAngleZ, body2.defaultRotationZ
            + Mth.cos(23.5F * 0.025F + (float) (Math.PI / (2 * chainLength))) * 0.25F * 0.15F,
            fixture.name + " idle chain flap phase");
        near(body2.rotationPointY, body2.defaultPositionY - Math.abs(Mth.sin(23.5F * 0.025F) * 0.625F),
            fixture.name + " idle bouncing body");
    }

    private static void mouthAndPassengerChecks(Fixture fixture, AdvancedModelBox neck, AdvancedModelBox head) {
        BasicModelPart[] chain = fixture.model.getHeadParts();
        check(chain.length == 2 && chain[0] == neck && chain[1] == head,
            fixture.name + " legacy mouth chain is neck then head, not body ancestors");
        MyrmexRenderState state = fixture.state(AnimationKind.NONE, 0, 0);
        state.ageInTicks = 11;
        state.yRot = 80;
        state.xRot = -40;
        state.hasPassengers = true;
        fixture.adapter.setupAnim(state);
        float neckX = neck.rotateAngleX;
        float neckY = neck.rotateAngleY;
        float headX = head.rotateAngleX;
        float headY = head.rotateAngleY;
        state.hasPassengers = false;
        fixture.adapter.setupAnim(state);
        near(neck.rotateAngleX - neckX, radians(-10), fixture.name + " passenger gates neck pitch");
        near(neck.rotateAngleY - neckY, radians(20), fixture.name + " passenger gates neck yaw");
        near(head.rotateAngleX - headX, radians(-10), fixture.name + " passenger gates head pitch");
        near(head.rotateAngleY - headY, radians(20), fixture.name + " passenger gates head yaw");
        Matrix4f expected = new Matrix4f();
        // Explicit ordered local transforms: postRenderArm intentionally does not walk parents or scale.
        for (AdvancedModelBox part : new AdvancedModelBox[] {neck, head}) {
            expected.translate(part.offsetX / 16, part.offsetY / 16, part.offsetZ / 16);
            expected.translate(part.rotationPointX / 16, part.rotationPointY / 16, part.rotationPointZ / 16);
            expected.rotate(new Quaternionf().rotationZYX(part.rotateAngleZ, part.rotateAngleY, part.rotateAngleX));
        }
        for (float ignoredScale : new float[] {0.0625F, 2}) {
            PoseStack poses = new PoseStack();
            fixture.model.postRenderArm(ignoredScale, poses);
            matrixNear(poses.last().pose(), expected, fixture.name + " mouth transform ignores scale argument");
        }
    }

    private static void add(List<Float> values, float... components) {
        for (float component : components) values.add(component);
    }

    private static void poseNear(float[] actual, float[] expected, String context) {
        check(actual.length == expected.length, context + " stable pose size");
        for (int i = 0; i < actual.length; i++) near(actual[i], expected[i], context + " component " + i);
    }

    private static void matrixNear(Matrix4f actual, Matrix4f expected, String context) {
        poseNear(actual.get(new float[16]), expected.get(new float[16]), context);
    }

    private static void rotationNear(AdvancedModelBox part, float x, float y, float z, String context) {
        near(part.rotateAngleX, x, context + " X");
        near(part.rotateAngleY, y, context + " Y");
        near(part.rotateAngleZ, z, context + " Z");
    }

    private static void positionNear(AdvancedModelBox part, float x, float y, float z, String context) {
        near(part.rotationPointX, x, context + " X");
        near(part.rotationPointY, y, context + " Y");
        near(part.rotationPointZ, z, context + " Z");
    }

    private static float radians(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    private static void near(float actual, float expected, String context) {
        checks++;
        if (!Float.isFinite(actual) || !Float.isFinite(expected) || Math.abs(actual - expected) > EPSILON) {
            throw new AssertionError(context + ": " + actual + " != " + expected);
        }
    }

    private static void check(boolean success, String context) {
        checks++;
        if (!success) throw new AssertionError(context);
    }
}
