package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;

/** Snapshot shared by the native dragon model and its deferred layers. */
public class DragonRenderState extends LivingEntityRenderState {
    public enum AnimationKind {
        NONE(0), FIRECHARGE(30), SPEAK(20), BITE(35), SHAKEPREY(65),
        TAILWHACK(40), WINGBLAST(50), ROAR(40), EPIC_ROAR(60), EAT(20);
        private final Animation token;
        AnimationKind(int duration) { token = Animation.create(duration); }
        public Animation token() { return token; }
    }

    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick, walkCycle, flightCycle, swimCycle, dragonType;
    public float partialTick, dragonPitch, dragonScale = 1, renderSize = 3;
    public float hoverProgress, flyProgress, swimProgress, modelDeadProgress, prevModelDeadProgress;
    public float sleepProgress, sitProgress, ridingProgress, tackleProgress, diveProgress, prevDiveProgress;
    public float fireBreathProgress, prevFireBreathProgress;
    public final float[] prevAnimationProgresses = new float[10];
    public boolean hovering, flying, sleeping, noAi, actuallyBreathingFire, modelDead;
    public boolean vehicle, passenger, breathingFire, male;
    public String customPose = "";
    // Buffer contributions are sampled during extraction, in radians, not retained as live buffers.
    public float turn, tail, roll, bodyPitch, tailPitch;
    public float backLeftHeight, backRightHeight, frontLeftHeight, frontRightHeight, legBodyLength;
    public net.minecraft.client.renderer.state.level.CameraRenderState camera;
    public Identifier texture, armorTexture, eyeTexture;
    public final ItemStackRenderState banner = new ItemStackRenderState();
    public final List<Rider> riders = new ArrayList<>();

    public record Rider(net.minecraft.client.renderer.entity.state.EntityRenderState state,
                        net.minecraft.client.renderer.entity.EntityRenderer<?, ?> renderer,
                        float yaw, boolean prey, boolean upright, boolean horse) {}
}
