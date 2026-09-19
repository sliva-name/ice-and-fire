/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Author: Alexthe666, since 1.0.0; LLibrary: iLexiconn and Gegy1000.
 * See citadel/NOTICE.md for attribution and distribution restrictions.
 */
package com.github.alexthe666.citadel.client.model.container;

/** Mutable additive rotation and pivot translation for a legacy keyframe. */
public class Transform {
    private float rotationX, rotationY, rotationZ;
    private float offsetX, offsetY, offsetZ;

    public float getRotationX() { return this.rotationX; }
    public float getRotationY() { return this.rotationY; }
    public float getRotationZ() { return this.rotationZ; }
    public float getOffsetX() { return this.offsetX; }
    public float getOffsetY() { return this.offsetY; }
    public float getOffsetZ() { return this.offsetZ; }

    public void addRotation(float x, float y, float z) {
        this.rotationX += x;
        this.rotationY += y;
        this.rotationZ += z;
    }

    public void addOffset(float x, float y, float z) {
        this.offsetX += x;
        this.offsetY += y;
        this.offsetZ += z;
    }

    public void resetRotation() {
        this.rotationX = this.rotationY = this.rotationZ = 0.0F;
    }

    public void resetOffset() {
        this.offsetX = this.offsetY = this.offsetZ = 0.0F;
    }

    public void setRotation(float x, float y, float z) {
        this.resetRotation();
        this.addRotation(x, y, z);
    }

    public void setOffset(float x, float y, float z) {
        this.resetOffset();
        this.addOffset(x, y, z);
    }
}
