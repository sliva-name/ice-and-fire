package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ITabulaModelAnimator;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.util.EnumSeaSerpentAnimations;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState.AnimationKind;

public class SeaSerpentTabulaModelAnimator extends IceAndFireTabulaModelAnimator implements ITabulaModelAnimator<SeaSerpentRenderState> {

    public final TabulaModel<?>[] swimPose = {EnumSeaSerpentAnimations.SWIM1.seaserpent_model, EnumSeaSerpentAnimations.SWIM3.seaserpent_model, EnumSeaSerpentAnimations.SWIM4.seaserpent_model, EnumSeaSerpentAnimations.SWIM6.seaserpent_model};

    public SeaSerpentTabulaModelAnimator() {
        super(EnumSeaSerpentAnimations.T_POSE.seaserpent_model);
    }

    @Override
    public void setRotationAngles(TabulaModel<SeaSerpentRenderState> model, SeaSerpentRenderState state) {
        model.resetToDefaultPose();
        model.getCube("BodyUpper").rotationPointY += 9;//model was made too high
        model.llibAnimator.update(state.animation.token(), state.animationTick, state.partialTick);
        animate(model);
        int currentIndex = state.swimCycle / 10;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = 3;
        }
        TabulaModel<?> prevPosition = swimPose[prevIndex];
        TabulaModel<?> currentPosition = swimPose[currentIndex];
        float delta = (state.swimCycle / 10.0F) % 1.0F + (state.partialTick / 10.0F);
        // AdvancedModelBox[] tailParts = {model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4"), model.getCube("Tail5"), model.getCube("Tail6")};
        // AdvancedModelBox[] neckParts = {model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Head")};

        for (AdvancedModelBox cube : model.getCubes().values()) {
            if (state.jumpProgress > 0.0F) {
                if (!isRotationEqual(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName), state.jumpProgress, 5, false);
                }
            }
            if (state.wantJumpProgress > 0.0F) {
                if (!isRotationEqual(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName), state.wantJumpProgress, 10, false);
                }
            }
            AdvancedModelBox prevPositionCube = prevPosition.getCube(cube.boxName);
            AdvancedModelBox currPositionCube = currentPosition.getCube(cube.boxName);
            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            this.addToRotateAngle(cube, state.walkAnimationSpeed, prevX + delta * distance(prevX, x), prevY + delta * distance(prevY, y), prevZ + delta * distance(prevZ, z));

        }
        if (state.breathProgress > 0.0F) {
            progressRotation(model.getCube("Head"), state.breathProgress, (float) Math.toRadians(-15F), 0, 0);
            progressRotation(model.getCube("HeadFront"), state.breathProgress, (float) Math.toRadians(-20F), 0, 0);
            progressRotation(model.getCube("Jaw"), state.breathProgress, (float) Math.toRadians(60F), 0, 0);
        }
        if (state.hasJumpRotation) {
            float jumpRot = state.jumpRotation;
            float turn = state.verticalVelocity * -4F;
            model.getCube("BodyUpper").rotateAngleX += (float) Math.toRadians(22.5F * turn) * jumpRot;
            model.getCube("Tail1").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail2").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail3").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail4").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
        }
        float prevRenderOffset = state.tailBodyYaw;

        model.getCube("Tail1").rotateAngleY += (state.pieceYaw[0] - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail2").rotateAngleY += (state.pieceYaw[1] - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail3").rotateAngleY += (state.pieceYaw[2] - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("Tail4").rotateAngleY += (state.pieceYaw[3] - prevRenderOffset) * ((float) Math.PI / 180F);
        model.getCube("BodyUpper").rotateAngleX -= state.xRot * ((float) Math.PI / 180F);
        if (!state.jumpingOutOfWater || state.inWater) {
            model.getCube("Tail1").rotateAngleX -= (state.piecePitch[0] - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail2").rotateAngleX -= (state.piecePitch[1] - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail3").rotateAngleX -= (state.piecePitch[2] - 0) * ((float) Math.PI / 180F);
            model.getCube("Tail4").rotateAngleX -= (state.piecePitch[3] - 0) * ((float) Math.PI / 180F);
        }
    }

    public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    private void animate(TabulaModel<SeaSerpentRenderState> model) {
        if (model.llibAnimator.setAnimation(AnimationKind.SPEAK.token())) {
            model.llibAnimator.startKeyframe(5);
            this.rotate(model.llibAnimator, model.getCube("Jaw"), 25, 0, 0);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.setStaticKeyframe(5);
            model.llibAnimator.resetKeyframe(5);
        }
        if (model.llibAnimator.setAnimation(AnimationKind.BITE.token())) {
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, EnumSeaSerpentAnimations.BITE1.seaserpent_model);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(5);
            moveToPose(model, EnumSeaSerpentAnimations.BITE2.seaserpent_model);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.setStaticKeyframe(2);
            model.llibAnimator.resetKeyframe(3);
        }
        if (model.llibAnimator.setAnimation(AnimationKind.ROAR.token())) {
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, EnumSeaSerpentAnimations.ROAR1.seaserpent_model);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, EnumSeaSerpentAnimations.ROAR2.seaserpent_model);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.startKeyframe(10);
            moveToPose(model, EnumSeaSerpentAnimations.ROAR3.seaserpent_model);
            model.llibAnimator.endKeyframe();
            model.llibAnimator.resetKeyframe(10);
        }

    }
}
