package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class ModelMyrmexBase extends AdvancedEntityModel<MyrmexRenderState> implements ICustomStatueModel {
    public void postRenderArm(float scale, PoseStack stackIn) {
        for (BasicModelPart renderer : this.getHeadParts()) {
            renderer.translateRotate(stackIn);
        }
    }

    /** Growth models have no held-item attachment; adult models supply their existing neck/head chain. */
    public BasicModelPart[] getHeadParts() {
        return new BasicModelPart[0];
    }

    // Preserve ModelDragonBase's degree and default-pose semantics without its live-entity bound.
    public void rotate(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
        animator.rotate(model, (float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
    }

    public void rotateMinus(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
        animator.rotate(model, (float) Math.toRadians(x) - model.defaultRotationX, (float) Math.toRadians(y) - model.defaultRotationY, (float) Math.toRadians(z) - model.defaultRotationZ);
    }

    public void progressRotationInterp(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ, float max) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / max;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / max;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / max;
    }

    public void progressPositionInterp(AdvancedModelBox model, float progress, float x, float y, float z, float max) {
        model.rotationPointX += progress * x / max;
        model.rotationPointY += progress * y / max;
        model.rotationPointZ += progress * z / max;
    }

    public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    public void progressRotationPrev(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * rotX / 20.0F;
        model.rotateAngleY += progress * rotY / 20.0F;
        model.rotateAngleZ += progress * rotZ / 20.0F;
    }

    public void progressPosition(AdvancedModelBox model, float progress, float x, float y, float z) {
        model.rotationPointX += progress * (x - model.defaultPositionX) / 20.0F;
        model.rotationPointY += progress * (y - model.defaultPositionY) / 20.0F;
        model.rotationPointZ += progress * (z - model.defaultPositionZ) / 20.0F;
    }

    public void progressPositionPrev(AdvancedModelBox model, float progress, float x, float y, float z) {
        model.rotationPointX += progress * x / 20.0F;
        model.rotationPointY += progress * y / 20.0F;
        model.rotationPointZ += progress * z / 20.0F;
    }

    @Override
    public void faceTarget(float yaw, float pitch, float rotationDivisor, AdvancedModelBox... boxes) {
        float actualRotationDivisor = rotationDivisor * (float) boxes.length;
        float yawAmount = yaw * (float) Math.PI / 180F / actualRotationDivisor;
        float pitchAmount = pitch * (float) Math.PI / 180F / actualRotationDivisor;
        for (AdvancedModelBox box : boxes) {
            box.rotateAngleY += yawAmount;
            box.rotateAngleX += pitchAmount;
        }
    }
}
