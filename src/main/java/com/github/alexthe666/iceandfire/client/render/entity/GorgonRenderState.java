package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Gorgon-specific inputs; walking, age and head rotation come from the living state. */
public class GorgonRenderState extends LivingEntityRenderState {
    public Animation animation = IAnimatedEntity.NO_ANIMATION;
    public int animationTick;
    public float partialTick;
    public boolean scaring;
    public boolean hitting;
    // Keep the original integer-tick death pose separate from vanilla's interpolated deathTime.
    public float deathProgress;
}
