package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;

abstract class ModelDreadBase extends ModelBipedBase {

    ModelDreadBase() {
        super();
    }

    public abstract Animation getSpawnAnimation();

    @Override
    public void setupAnim(DreadHumanoidRenderState state) {
        super.setupAnim(state);
        setRotationAnglesSpawn(state);
    }

    public void setRotationAnglesSpawn(DreadHumanoidRenderState state) {
        if (state.animation == getSpawnAnimation()) {
            if (state.animationTick < 30) {
                this.flap(armRight, 0.5F, 0.5F, false, 2, -0.7F, state.tickCount, 1);
                this.flap(armLeft, 0.5F, 0.5F, true, 2, -0.7F, state.tickCount, 1);
                this.walk(armRight, 0.5F, 0.5F, true, 1, 0, state.tickCount, 1);
                this.walk(armLeft, 0.5F, 0.5F, true, 1, 0, state.tickCount, 1);
            }
        }
    }

    @Override
    public void animate(DreadHumanoidRenderState state) {
        animator.update(state.animation, state.animationTick, state.partialTick);
        if (animator.setAnimation(getSpawnAnimation())) {
            animator.startKeyframe(0);
            animator.move(this.body, 0, 35, 0);
            rotate(animator, this.armLeft, -180, 0, 0);
            rotate(animator, this.armRight, -180, 0, 0);
            animator.endKeyframe();
            animator.startKeyframe(30);
            animator.move(this.body, 0, 0, 0);
            rotate(animator, this.armLeft, -180, 0, 0);
            rotate(animator, this.armRight, -180, 0, 0);
            animator.endKeyframe();
            animator.resetKeyframe(5);
        }
    }

}
