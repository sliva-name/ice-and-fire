package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.Nullable;

/** Detached model and beam inputs; no live entities are retained. */
public class CockatriceRenderState extends LivingEntityRenderState {
    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    public float sitProgress;
    public float stareProgress;
    public boolean hen;
    // EntityCockatrice exposes no blink state; its four existing textures do not blink.
    public boolean hasTargetedEntity;
    public boolean blinded;
    public boolean mutuallyLooking;
    public float attackAnimationScale;
    public float beamTime;
    public int tickCount;
    public @Nullable EntityRenderState beamTarget;

    public enum AnimationKind {
        NONE(0), JUMPAT(30), WATTLESHAKE(20), BITE(15), SPEAK(10), EAT(20);

        public final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }
    }
}
