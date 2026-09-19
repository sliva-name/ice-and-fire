package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Detached animation and texture inputs; enum tokens never initialize the entity class. */
public class CyclopsRenderState extends LivingEntityRenderState {
    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
    public Variant variant = Variant.ZERO;
    public boolean blinking;
    public boolean blinded;

    public Identifier bodyTexture() {
        return blinded ? variant.blindedTexture : blinking ? variant.blinkTexture : variant.texture;
    }

    public enum AnimationKind {
        NONE(0), STOMP(27), KICK(20), EATPLAYER(40), ROAR(30);

        public final Animation token;

        AnimationKind(int duration) {
            token = Animation.create(duration);
        }
    }

    public enum Variant {
        ZERO(0), ONE(1), TWO(2), THREE(3);

        public final Identifier texture;
        public final Identifier blinkTexture;
        public final Identifier blindedTexture;

        Variant(int index) {
            texture = texture(index, "");
            blinkTexture = texture(index, "_blink");
            blindedTexture = texture(index, "_injured");
        }
    }

    private static Identifier texture(int index, String suffix) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cyclops/cyclops_" + index + suffix + ".png");
    }
}
