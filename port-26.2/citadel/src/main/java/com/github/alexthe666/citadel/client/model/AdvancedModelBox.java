package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * Enhanced mutable part, adapted from AlexModGuy/Citadel
 * 8018e44d8b569913ca828f31aa6c86163319e7a5 (GNU LGPL).
 * Original author: gegy1000, since 1.0.0.
 *
 * <p>Offsets are additional translations in model pixels, before rotation. Unlike the
 * pinned source's commented-out offsets, these participate in rendering and default poses.
 * Geometry and descendants have independent scale transforms, including zero/negative scales.
 */
public class AdvancedModelBox extends BasicModelPart {
    public float defaultRotationX, defaultRotationY, defaultRotationZ;
    public float defaultPositionX, defaultPositionY, defaultPositionZ;
    public float defaultOffsetX, defaultOffsetY, defaultOffsetZ;
    public float scaleX = 1, scaleY = 1, scaleZ = 1;
    public float offsetX, offsetY, offsetZ;
    public boolean scaleChildren;
    public String boxName;
    private final AdvancedEntityModel<?> model;
    private AdvancedModelBox parent;
    private float defaultScaleX = 1, defaultScaleY = 1, defaultScaleZ = 1;

    public AdvancedModelBox(AdvancedEntityModel<?> model, String name) {
        super(model.texWidth, model.texHeight, 0, 0);
        this.model = model;
        boxName = name;
    }

    public AdvancedModelBox(AdvancedEntityModel<?> model) { this(model, null); }

    public AdvancedModelBox(AdvancedEntityModel<?> model, int u, int v) {
        this(model);
        setTextureOffset(u, v);
    }

    public AdvancedModelBox setTexSize(int width, int height) {
        setTextureSize(width, height);
        return this;
    }

    @Override
    public AdvancedModelBox setTextureOffset(int u, int v) {
        super.setTextureOffset(u, v);
        return this;
    }

    public void setShouldScaleChildren(boolean value) { scaleChildren = value; }
    public void setScale(float x, float y, float z) { scaleX = x; scaleY = y; scaleZ = z; }
    public void setScaleX(float value) { scaleX = value; }
    public void setScaleY(float value) { scaleY = value; }
    public void setScaleZ(float value) { scaleZ = value; }
    @Override protected float renderScaleX() { return scaleX; }
    @Override protected float renderScaleY() { return scaleY; }
    @Override protected float renderScaleZ() { return scaleZ; }
    @Override protected boolean scalesChildren() { return scaleChildren; }

    @Override
    public void translateRotate(PoseStack poses) {
        poses.translate(offsetX / 16, offsetY / 16, offsetZ / 16);
        super.translateRotate(poses);
    }

    @Override
    protected void copyTransformTo(ModelPart part) {
        super.copyTransformTo(part);
        part.x += offsetX;
        part.y += offsetY;
        part.z += offsetZ;
    }

    /** Captures rotation, pivot, offset and scale, but not visibility or hierarchy policy. */
    public void updateDefaultPose() {
        defaultRotationX = rotateAngleX; defaultRotationY = rotateAngleY; defaultRotationZ = rotateAngleZ;
        defaultPositionX = rotationPointX; defaultPositionY = rotationPointY; defaultPositionZ = rotationPointZ;
        defaultOffsetX = offsetX; defaultOffsetY = offsetY; defaultOffsetZ = offsetZ;
        defaultScaleX = scaleX; defaultScaleY = scaleY; defaultScaleZ = scaleZ;
    }

    public void resetToDefaultPose() {
        rotateAngleX = defaultRotationX; rotateAngleY = defaultRotationY; rotateAngleZ = defaultRotationZ;
        setPos(defaultPositionX, defaultPositionY, defaultPositionZ);
        offsetX = defaultOffsetX; offsetY = defaultOffsetY; offsetZ = defaultOffsetZ;
        setScale(defaultScaleX, defaultScaleY, defaultScaleZ);
    }

    @Override
    public void addChild(BasicModelPart child) {
        if (child instanceof AdvancedModelBox advanced && advanced.parent != null && advanced.parent != this) {
            throw new IllegalArgumentException("Detach an advanced part before reparenting it");
        }
        super.addChild(child);
        if (child instanceof AdvancedModelBox advanced) advanced.parent = this;
    }

    public AdvancedModelBox getParent() { return parent; }

    /** Updates both sides of the hierarchy, rather than leaving a stale parent pointer. */
    public void setParent(AdvancedModelBox newParent) {
        if (newParent == parent) return;
        AdvancedModelBox oldParent = parent;
        if (newParent != null) {
            // Validate cycles before detaching from the old parent.
            parent = null;
            try {
                newParent.addChild(this);
            } catch (RuntimeException failure) {
                parent = oldParent;
                throw failure;
            }
        } else {
            parent = null;
        }
        if (oldParent != null) oldParent.childModels.remove(this);
    }

    /** Applies the same ancestor transforms used when rendering this part, for attachments. */
    public void translateAndRotateWithParents(PoseStack poses) {
        if (parent != null) parent.applyAncestorTransform(poses);
        translateAndRotate(poses);
    }

    private void applyAncestorTransform(PoseStack poses) {
        if (parent != null) parent.applyAncestorTransform(poses);
        translateRotate(poses);
        if (scaleChildren) poses.scale(scaleX, scaleY, scaleZ);
    }

    public AdvancedEntityModel<?> getModel() { return model; }

    private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        float movementScale = model.getMovementScale();
        float rotation = Mth.cos(time * speed * movementScale + offset) * degree * movementScale * amount + weight * amount;
        return invert ? -rotation : rotation;
    }

    public void walk(float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        rotateAngleX += calculateRotation(speed, degree, invert, offset, weight, time, amount);
    }

    public void swing(float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        rotateAngleY += calculateRotation(speed, degree, invert, offset, weight, time, amount);
    }

    public void flap(float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        rotateAngleZ += calculateRotation(speed, degree, invert, offset, weight, time, amount);
    }

    public void bob(float speed, float degree, boolean bounce, float time, float amount) {
        float movementScale = model.getMovementScale();
        degree *= movementScale;
        speed *= movementScale;
        float wave = (float) (Math.sin(time * speed) * amount * degree);
        rotationPointY += bounce ? -Math.abs(wave) : wave - amount * degree;
    }

    public void transitionTo(AdvancedModelBox to, float timer, float maxTime) {
        if (maxTime == 0) throw new IllegalArgumentException("Transition duration must be nonzero");
        float weight = timer / maxTime;
        rotateAngleX += (to.rotateAngleX - rotateAngleX) * weight;
        rotateAngleY += (to.rotateAngleY - rotateAngleY) * weight;
        rotateAngleZ += (to.rotateAngleZ - rotateAngleZ) * weight;
        rotationPointX += (to.rotationPointX - rotationPointX) * weight;
        rotationPointY += (to.rotationPointY - rotationPointY) * weight;
        rotationPointZ += (to.rotationPointZ - rotationPointZ) * weight;
        offsetX += (to.offsetX - offsetX) * weight;
        offsetY += (to.offsetY - offsetY) * weight;
        offsetZ += (to.offsetZ - offsetZ) * weight;
    }
}
