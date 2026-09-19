package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class StymphalianBirdRenderState extends LivingEntityRenderState {
    public static final Animation PECK = Animation.create(20);
    public static final Animation SPEAK = Animation.create(10);
    public static final Animation SHOOT_ARROWS = Animation.create(30);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public float flyProgress;
}
