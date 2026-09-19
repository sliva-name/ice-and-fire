package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** All SeaSerpent pose inputs are copied during extraction; no live entity is retained. */
public class SeaSerpentRenderState extends LivingEntityRenderState {
    public enum AnimationKind {
        NONE(0), SPEAK(15), BITE(15), ROAR(40);

        private final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }

        public Animation token() {
            return token;
        }
    }

    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    public int swimCycle;
    // Legacy progress inputs used current values, not interpolated values.
    public float jumpProgress;
    public float wantJumpProgress;
    public float breathProgress;
    public boolean hasJumpRotation;
    public float jumpRotation;
    public float verticalVelocity;
    public boolean jumpingOutOfWater;
    public boolean inWater;
    /** Linear, unwrapped body yaw: deliberately different from LivingEntityRenderState.bodyRot. */
    public float tailBodyYaw;
    public final float[] pieceYaw = new float[4];
    public final float[] piecePitch = new float[4];
    public float serpentScale = 1.0F;
    public int variant;
    public boolean blinking;
    public boolean ancient;
}
