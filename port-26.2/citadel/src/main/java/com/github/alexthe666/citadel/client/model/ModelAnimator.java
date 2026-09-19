/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Author: Alexthe666, since 1.0.0; LLibrary: iLexiconn and Gegy1000.
 * See citadel/NOTICE.md for attribution and distribution restrictions.
 */
package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.container.Transform;
import java.util.HashMap;
import net.minecraft.util.Mth;

/**
 * Legacy additive keyframes with explicit, extracted animation timing.
 * Reset the model's default pose before each update, then declare the complete
 * keyframe sequence on every evaluation. Rotation is in radians; movement is in
 * model pixels and changes pivots, not {@link AdvancedModelBox#offsetX}.
 *
 * <p>Unlike upstream, timing never comes from the Minecraft singleton. Both
 * update overloads snapshot their inputs for the entire evaluation.
 */
public class ModelAnimator {
    private int tempTick;
    private int prevTempTick;
    private boolean correctAnimation;
    private Animation animation;
    private int animationTick;
    private float partialTick;
    private IAnimatedEntity entity;
    private final HashMap<AdvancedModelBox, Transform> transformMap = new HashMap<>();
    private final HashMap<AdvancedModelBox, Transform> prevTransformMap = new HashMap<>();

    public ModelAnimator() {}

    public static ModelAnimator create() {
        return new ModelAnimator();
    }

    /** Returns the last entity supplied, or null before update or after extracted-state update. */
    public IAnimatedEntity getEntity() {
        return this.entity;
    }

    /**
     * Starts an evaluation using an identity-based animation token, its integer
     * tick and the caller's render partial tick (normally 0..1). This clears
     * keyframe bookkeeping, but does not restore or otherwise change model parts.
     */
    public void update(Animation animation, int animationTick, float partialTick) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnimation = false;
        this.entity = null;
        this.animation = animation;
        this.animationTick = animationTick;
        this.partialTick = partialTick;
        this.transformMap.clear();
        this.prevTransformMap.clear();
    }

    /** Convenience for non-extracted callers; no implicit frame-time accessor. */
    public void update(IAnimatedEntity entity, float partialTick) {
        this.update(entity.getAnimation(), entity.getAnimationTick(), partialTick);
        this.entity = entity;
    }

    /**
     * Rewinds the timeline and selects by token identity. As in upstream, only
     * update clears transform maps; selection itself does not clear them.
     */
    public boolean setAnimation(Animation animation) {
        this.tempTick = this.prevTempTick = 0;
        this.correctAnimation = this.animation == animation;
        return this.correctAnimation;
    }

    public void startKeyframe(int duration) {
        if (!this.correctAnimation) return;
        this.prevTempTick = this.tempTick;
        this.tempTick += duration;
    }

    /** Holds the previous target without consuming or replacing it. */
    public void setStaticKeyframe(int duration) {
        this.startKeyframe(duration);
        this.endKeyframe(true);
    }

    /** Interpolates to an empty target (zero additive transformation). */
    public void resetKeyframe(int duration) {
        this.startKeyframe(duration);
        this.endKeyframe();
    }

    /** Adds to this frame's target rotation, in radians relative to the base pose. */
    public void rotate(AdvancedModelBox box, float x, float y, float z) {
        if (!this.correctAnimation) return;
        this.getTransform(box).addRotation(x, y, z);
    }

    /** Adds to this frame's target pivot translation, in model pixels. */
    public void move(AdvancedModelBox box, float x, float y, float z) {
        if (!this.correctAnimation) return;
        this.getTransform(box).addOffset(x, y, z);
    }

    private Transform getTransform(AdvancedModelBox box) {
        return this.transformMap.computeIfAbsent(box, b -> new Transform());
    }

    public void endKeyframe() {
        this.endKeyframe(false);
    }

    private void endKeyframe(boolean stationary) {
        if (!this.correctAnimation) return;
        // Membership uses integer ticks, not tick + partialTick. Zero-duration
        // frames still establish targets without entering this division.
        if (this.animationTick >= this.prevTempTick && this.animationTick < this.tempTick) {
            if (stationary) {
                this.apply(this.prevTransformMap, 1.0F);
            } else {
                float tick = (this.animationTick - this.prevTempTick + this.partialTick)
                        / (this.tempTick - this.prevTempTick);
                float inc = Mth.sin((float) (tick * Math.PI / 2.0F));
                this.apply(this.prevTransformMap, 1.0F - inc);
                this.apply(this.transformMap, inc);
            }
        }
        if (!stationary) {
            this.prevTransformMap.clear();
            this.prevTransformMap.putAll(this.transformMap);
            this.transformMap.clear();
        }
    }

    private void apply(HashMap<AdvancedModelBox, Transform> transforms, float weight) {
        for (var entry : transforms.entrySet()) {
            AdvancedModelBox box = entry.getKey();
            Transform transform = entry.getValue();
            box.rotateAngleX += weight * transform.getRotationX();
            box.rotateAngleY += weight * transform.getRotationY();
            box.rotateAngleZ += weight * transform.getRotationZ();
            box.rotationPointX += weight * transform.getOffsetX();
            box.rotationPointY += weight * transform.getOffsetY();
            box.rotationPointZ += weight * transform.getOffsetZ();
        }
    }
}
