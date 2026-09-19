package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.container.TextureOffset;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;

/**
 * Animation helpers adapted from AlexModGuy/Citadel
 * 8018e44d8b569913ca828f31aa6c86163319e7a5 (GNU LGPL).
 * Original author: gegy1000, since 1.0.0.
 * ModelAnimator-dependent rotate/rotateMinus are intentionally deferred until that port exists.
 */
public abstract class AdvancedEntityModel<S extends EntityRenderState> extends BasicEntityModel<S> {
    public int texWidth = 32, texHeight = 32;
    private float movementScale = 1;
    private final Map<String, TextureOffset> modelTextureMap = new HashMap<>();

    public abstract Iterable<AdvancedModelBox> getAllParts();

    public void updateDefaultPose() { getAllParts().forEach(AdvancedModelBox::updateDefaultPose); }
    public void resetToDefaultPose() { getAllParts().forEach(AdvancedModelBox::resetToDefaultPose); }

    /** Subclasses should call super before applying the current state's animation. */
    @Override
    public void setupAnim(S state) { resetToDefaultPose(); }

    protected void setTextureOffset(String name, int u, int v) { modelTextureMap.put(name, new TextureOffset(u, v)); }
    public TextureOffset getTextureOffset(String name) { return modelTextureMap.get(name); }
    public float getMovementScale() { return movementScale; }
    public void setMovementScale(float value) { movementScale = value; }

    public void faceTarget(float yaw, float pitch, float divisor, AdvancedModelBox... boxes) {
        if (boxes.length == 0) return;
        requireDivisor(divisor);
        float factor = (float) Math.PI / 180 / (divisor * boxes.length);
        for (AdvancedModelBox box : boxes) {
            box.rotateAngleY += yaw * factor;
            box.rotateAngleX += pitch * factor;
        }
    }

    public void chainSwing(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float time, float amount) {
        for (int i = 0; i < boxes.length; i++) boxes[i].rotateAngleY += chainRotation(boxes.length, i, speed, degree, rootOffset, time, amount);
    }

    public void chainWave(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float time, float amount) {
        for (int i = 0; i < boxes.length; i++) boxes[i].rotateAngleX += chainRotation(boxes.length, i, speed, degree, rootOffset, time, amount);
    }

    public void chainFlap(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float time, float amount) {
        for (int i = 0; i < boxes.length; i++) boxes[i].rotateAngleZ += chainRotation(boxes.length, i, speed, degree, rootOffset, time, amount);
    }

    private float chainRotation(int count, int index, float speed, float degree, double rootOffset, float time, float amount) {
        float offset = (float) (rootOffset * Math.PI / (2 * count));
        return Mth.cos(time * speed * movementScale + offset * index) * amount * degree * movementScale;
    }

    public void walk(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        box.walk(speed, degree, invert, offset, weight, time, amount);
    }

    public void swing(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        box.swing(speed, degree, invert, offset, weight, time, amount);
    }

    public void flap(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float time, float amount) {
        box.flap(speed, degree, invert, offset, weight, time, amount);
    }

    public void bob(AdvancedModelBox box, float speed, float degree, boolean bounce, float time, float amount) {
        box.bob(speed, degree, bounce, time, amount);
    }

    public float moveBox(float speed, float degree, boolean bounce, float time, float amount) {
        float wave = Mth.sin(time * speed) * amount * degree;
        return bounce ? -Math.abs(wave) : wave - amount * degree;
    }

    public void setRotateAngle(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x; box.rotateAngleY = y; box.rotateAngleZ = z;
    }

    public void progressRotation(AdvancedModelBox box, float progress, float x, float y, float z) {
        progressRotation(box, progress, x, y, z, 20.0F);
    }

    public void progressRotation(AdvancedModelBox box, float progress, float x, float y, float z, float divisor) {
        progressRotationPrev(box, progress, x - box.defaultRotationX, y - box.defaultRotationY, z - box.defaultRotationZ, divisor);
    }

    public void progressRotationPrev(AdvancedModelBox box, float progress, float x, float y, float z) {
        progressRotationPrev(box, progress, x, y, z, 20.0F);
    }

    public void progressRotationPrev(AdvancedModelBox box, float progress, float x, float y, float z, float divisor) {
        requireDivisor(divisor);
        box.rotateAngleX += progress * x / divisor;
        box.rotateAngleY += progress * y / divisor;
        box.rotateAngleZ += progress * z / divisor;
    }

    public void progressPosition(AdvancedModelBox box, float progress, float x, float y, float z) {
        progressPosition(box, progress, x, y, z, 20.0F);
    }

    public void progressPosition(AdvancedModelBox box, float progress, float x, float y, float z, float divisor) {
        progressPositionPrev(box, progress, x - box.defaultPositionX, y - box.defaultPositionY, z - box.defaultPositionZ, divisor);
    }

    public void progressPositionPrev(AdvancedModelBox box, float progress, float x, float y, float z) {
        progressPositionPrev(box, progress, x, y, z, 20.0F);
    }

    public void progressPositionPrev(AdvancedModelBox box, float progress, float x, float y, float z, float divisor) {
        requireDivisor(divisor);
        box.rotationPointX += progress * x / divisor;
        box.rotationPointY += progress * y / divisor;
        box.rotationPointZ += progress * z / divisor;
    }

    private static void requireDivisor(float divisor) {
        if (divisor == 0) throw new IllegalArgumentException("Animation divisor must be nonzero");
    }
}
