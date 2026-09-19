package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import java.util.Arrays;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Detached animation inputs. No live entity, chain buffer or frame clock is retained. */
public class AmphithereRenderState extends LivingEntityRenderState {
    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    // The entity has no previous progress fields; preserve its tick-based progress.
    public float flapProgress;
    public float groundProgress;
    public float diveProgress;
    public float sitProgress;
    public boolean onGround;
    public float bodyRoll;
    public float bodyPitch;
    public final float[] tailYaw = new float[4];
    public Variant variant = Variant.GREEN;
    public boolean blinking;

    /** Inputs are the actual previous/current yaw histories used by IFChainBuffer.apply*. */
    public void captureBufferRotations(float previousRoll, float roll, float previousPitch, float pitch,
                                       float previousTail, float tail, float partialTick) {
        bodyRoll = bufferRadians(previousRoll, roll, partialTick);
        bodyPitch = bufferRadians(previousPitch, pitch, partialTick);
        Arrays.fill(tailYaw, bufferRadians(previousTail, tail, partialTick) / tailYaw.length);
    }

    private static float bufferRadians(float previous, float current, float partialTick) {
        return 0.01745329251F * (previous + partialTick * (current - previous));
    }

    public Identifier bodyTexture() {
        return blinking ? variant.blinkTexture : variant.texture;
    }

    public enum AnimationKind {
        NONE(0), BITE(15), BITE_RIDER(15), WING_BLAST(30), TAIL_WHIP(30), SPEAK(10);

        public final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }
    }

    public enum Variant {
        BLUE("blue"), GREEN("green"), OLIVE("olive"), RED("red"), YELLOW("yellow");

        public final Identifier texture;
        public final Identifier blinkTexture;

        Variant(String name) {
            texture = texture(name);
            blinkTexture = texture(name + "_blink");
        }
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/amphithere/amphithere_" + name + ".png");
    }
}
