package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class DreadScuttlerRenderState extends LivingEntityRenderState {
    public enum AnimationKind {
        NONE(IAnimatedEntity.NO_ANIMATION), BITE(Animation.create(15)), SPAWN(Animation.create(40));

        private final Animation token;

        AnimationKind(Animation token) { this.token = token; }
        public Animation token() { return token; }
    }

    public AnimationKind animation = AnimationKind.NONE;
    public int animationTick;
    public float partialTick;
}
