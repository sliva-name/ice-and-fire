package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.citadel.client.model.container.Transform;
import java.util.List;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;

/** Plain-main regression suite using real model parts; no client singleton or GPU. */
public final class ModelAnimatorTest {
    private static final Animation ACTION = Animation.create(17);
    private static int checks;

    public static void main(String[] args) {
        timeline();
        zeroDuration();
        selectionAndUpdate();
        extractedAndEntityInputs();
        transformHelper();
        System.out.println("ModelAnimatorTest: " + checks + " checks passed");
    }

    private static void timeline() {
        Fixture model = new Fixture();
        ModelAnimator animator = ModelAnimator.create();
        check(animator.getEntity() == null, "new animator has no entity");
        for (int tick = -1; tick <= 19; tick++) {
            for (float partial : new float[] {0, 0.25f, 0.9f, 1}) {
                model.resetToDefaultPose();
                animator.update(ACTION, tick, partial);
                check(animator.setAnimation(ACTION), "same animation identity matches");
                declareTimeline(animator, model);
                float root = 0, child = 0, incoming = 0;
                if (tick >= 0 && tick < 4) {
                    root = child = ease((tick + partial) / 4);
                } else if (tick >= 4 && tick < 7) {
                    root = child = 1;
                } else if (tick >= 7 && tick < 11) {
                    float inc = ease((tick - 7 + partial) / 4);
                    root = 1 - inc;
                    root += -2 * inc;
                    child = 1 - inc;
                    incoming = 3 * inc;
                } else if (tick >= 11 && tick < 13) {
                    root = -2;
                    incoming = 3;
                } else if (tick >= 13 && tick < 17) {
                    float dec = 1 - ease((tick - 13 + partial) / 4);
                    root = -2 * dec;
                    incoming = 3 * dec;
                }
                pose(model.root, root, "root at " + tick + "+" + partial);
                pose(model.child, child, "outgoing part at " + tick + "+" + partial);
                pose(model.incoming, incoming, "incoming part at " + tick + "+" + partial);
            }
        }
        // Independently check the easing shape rather than only duplicating its formula.
        model.resetToDefaultPose();
        animator.update(ACTION, 2, 0);
        animator.setAnimation(ACTION);
        declareTimeline(animator, model);
        near(model.root.rotateAngleX - model.root.defaultRotationX,
                (float) Math.sqrt(0.5), "halfway uses sine, not linear interpolation");
    }

    private static void declareTimeline(ModelAnimator animator, Fixture model) {
        animator.startKeyframe(4);
        // Repeated calls accumulate all six target components.
        target(animator, model.root, 0.25f);
        target(animator, model.root, 0.75f);
        target(animator, model.child, 1);
        animator.endKeyframe();
        animator.setStaticKeyframe(1);
        animator.setStaticKeyframe(2);
        animator.startKeyframe(4);
        target(animator, model.root, -2);
        target(animator, model.incoming, 3);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(4);
    }

    private static void zeroDuration() {
        Fixture model = new Fixture();
        ModelAnimator animator = new ModelAnimator();
        for (int tick = 0; tick <= 4; tick++) {
            model.resetToDefaultPose();
            animator.update(ACTION, tick, 0.5f);
            animator.setAnimation(ACTION);
            animator.startKeyframe(0);
            target(animator, model.root, 2);
            animator.endKeyframe();
            animator.setStaticKeyframe(0);
            animator.setStaticKeyframe(2);
            animator.resetKeyframe(2);
            float factor = tick < 2 ? 2 : tick < 4 ? 2 * (1 - ease((tick - 2 + 0.5f) / 2)) : 0;
            pose(model.root, factor, "zero-duration target at " + tick);
        }
        model.resetToDefaultPose();
        animator.update(ACTION, 0, 0.5f);
        animator.setAnimation(ACTION);
        animator.startKeyframe(0);
        target(animator, model.root, 2);
        animator.endKeyframe();
        animator.resetKeyframe(0);
        animator.setStaticKeyframe(2);
        pose(model.root, 0, "zero-duration reset clears previous target");
    }

    private static void selectionAndUpdate() {
        Fixture model = new Fixture();
        ModelAnimator animator = ModelAnimator.create();
        Animation other = Animation.create(ACTION.getDuration());
        animator.update(ACTION, 0, 0.5f);
        check(!animator.setAnimation(other), "equal duration is not equal identity");
        ignored(animator, model.root);
        pose(model.root, 0, "mismatched sequence is inert");
        check(animator.setAnimation(ACTION), "selection after mismatch rewinds timeline");
        animator.startKeyframe(1);
        target(animator, model.root, 1);
        animator.endKeyframe();
        pose(model.root, ease(0.5f), "matching sequence starts at zero");
        check(!animator.setAnimation(other), "mismatch after match");
        ignored(animator, model.root);
        pose(model.root, ease(0.5f), "mismatch does not add transforms");

        model.resetToDefaultPose();
        animator.setAnimation(ACTION);
        animator.setStaticKeyframe(1);
        pose(model.root, 1, "setAnimation preserves previous target as upstream does");

        // Leave both a completed target and a pending target, then update.
        animator.startKeyframe(2);
        target(animator, model.child, 99);
        animator.update(ACTION, 0, 0.5f);
        pose(model.root, 1, "update does not restore the model pose");
        model.resetToDefaultPose();
        ignored(animator, model.root);
        pose(model.root, 0, "update disables frame declaration until selection");
        animator.setAnimation(ACTION);
        animator.setStaticKeyframe(1);
        pose(model.root, 0, "update clears previous target map");
        animator.startKeyframe(0);
        animator.endKeyframe();
        animator.setAnimation(ACTION);
        animator.setStaticKeyframe(1);
        pose(model.child, 0, "update clears pending target map");

        animator.update(IAnimatedEntity.NO_ANIMATION, 0, 0.5f);
        check(!animator.setAnimation(ACTION), "no-animation token does not match action");
        ignored(animator, model.root);
        pose(model.root, 0, "idle state has no effect");
    }

    private static void ignored(ModelAnimator animator, AdvancedModelBox box) {
        animator.startKeyframe(50);
        target(animator, box, 100);
        animator.endKeyframe();
        animator.setStaticKeyframe(50);
        animator.resetKeyframe(50);
    }

    private static void extractedAndEntityInputs() {
        Fixture model = new Fixture();
        ModelAnimator animator = ModelAnimator.create();
        AnimatedInput entity = new AnimatedInput();
        entity.setAnimation(ACTION);
        entity.setAnimationTick(2);
        animator.update(entity, 0.25f);
        check(animator.getEntity() == entity, "explicit entity overload retains accessor");
        entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        entity.setAnimationTick(99);
        check(animator.setAnimation(ACTION), "entity overload snapshots identity");
        declareTimeline(animator, model);
        pose(model.root, ease(2.25f / 4), "entity overload snapshots tick and partial tick");
        model.resetToDefaultPose();
        animator.update(ACTION, 2, 0.25f);
        check(animator.getEntity() == null, "extracted update releases live entity");
        animator.setAnimation(ACTION);
        declareTimeline(animator, model);
        pose(model.root, ease(2.25f / 4), "extracted input matches explicit entity input");
    }

    private static void transformHelper() {
        Transform transform = new Transform();
        transform.addRotation(1, 2, 3);
        transform.addRotation(3, 2, 1);
        transform.addOffset(4, 5, 6);
        near(transform.getRotationX(), 4, "helper accumulates X");
        near(transform.getRotationY(), 4, "helper accumulates Y");
        near(transform.getRotationZ(), 4, "helper accumulates Z");
        transform.setRotation(7, 8, 9);
        transform.setOffset(10, 11, 12);
        near(transform.getRotationX(), 7, "helper replaces rotation X");
        near(transform.getRotationY(), 8, "helper replaces rotation Y");
        near(transform.getRotationZ(), 9, "helper replaces rotation Z");
        near(transform.getOffsetX(), 10, "helper replaces offset X");
        near(transform.getOffsetY(), 11, "helper replaces offset Y");
        near(transform.getOffsetZ(), 12, "helper replaces offset Z");
        transform.resetRotation();
        near(transform.getRotationX(), 0, "helper resets rotation X");
        near(transform.getRotationY(), 0, "helper resets rotation Y");
        near(transform.getRotationZ(), 0, "helper resets rotation Z");
        near(transform.getOffsetX(), 10, "rotation reset preserves offset");
        transform.resetOffset();
        near(transform.getOffsetX(), 0, "helper resets offset X");
        near(transform.getOffsetY(), 0, "helper resets offset Y");
        near(transform.getOffsetZ(), 0, "helper resets offset Z");
    }

    private static void target(ModelAnimator animator, AdvancedModelBox box, float factor) {
        animator.rotate(box, factor, 2 * factor, 3 * factor);
        animator.move(box, 4 * factor, 5 * factor, 6 * factor);
    }

    private static float ease(float progress) {
        return Mth.sin((float) (progress * Math.PI / 2.0F));
    }

    private static void pose(AdvancedModelBox box, float factor, String label) {
        near(box.rotateAngleX, box.defaultRotationX + factor, label + " rotation X");
        near(box.rotateAngleY, box.defaultRotationY + 2 * factor, label + " rotation Y");
        near(box.rotateAngleZ, box.defaultRotationZ + 3 * factor, label + " rotation Z");
        near(box.rotationPointX, box.defaultPositionX + 4 * factor, label + " pivot X");
        near(box.rotationPointY, box.defaultPositionY + 5 * factor, label + " pivot Y");
        near(box.rotationPointZ, box.defaultPositionZ + 6 * factor, label + " pivot Z");
        near(box.offsetX, box.defaultOffsetX, label + " offset X unchanged");
        near(box.offsetY, box.defaultOffsetY, label + " offset Y unchanged");
        near(box.offsetZ, box.defaultOffsetZ, label + " offset Z unchanged");
        near(box.scaleX, 2, label + " scale X unchanged");
        near(box.scaleY, 3, label + " scale Y unchanged");
        near(box.scaleZ, 4, label + " scale Z unchanged");
    }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001f,
                message + ": " + actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static final class Fixture extends AdvancedEntityModel<EntityRenderState> {
        final AdvancedModelBox root = new AdvancedModelBox(this, "root");
        final AdvancedModelBox child = new AdvancedModelBox(this, "child");
        final AdvancedModelBox incoming = new AdvancedModelBox(this, "incoming");

        Fixture() {
            root.addChild(child);
            root.addChild(incoming);
            for (AdvancedModelBox box : getAllParts()) {
                box.addBox(0, 0, 0, 1, 1, 1);
                box.rotateAngleX = 0.125f;
                box.rotateAngleY = -0.25f;
                box.rotateAngleZ = 0.5f;
                box.setPos(7, -8, 9);
                box.offsetX = 10;
                box.offsetY = 11;
                box.offsetZ = 12;
                box.setScale(2, 3, 4);
            }
            updateDefaultPose();
        }

        @Override public Iterable<BasicModelPart> parts() { return List.of(root); }
        @Override public Iterable<AdvancedModelBox> getAllParts() { return List.of(root, child, incoming); }
    }

    private static final class AnimatedInput implements IAnimatedEntity {
        private Animation animation = NO_ANIMATION;
        private int tick;
        @Override public int getAnimationTick() { return tick; }
        @Override public void setAnimationTick(int tick) { this.tick = tick; }
        @Override public Animation getAnimation() { return animation; }
        @Override public void setAnimation(Animation animation) { this.animation = animation; }
        @Override public Animation[] getAnimations() { return new Animation[] {ACTION}; }
    }
}
