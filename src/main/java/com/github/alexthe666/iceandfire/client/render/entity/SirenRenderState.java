package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class SirenRenderState extends LivingEntityRenderState {
    public static final Animation BITE = Animation.create(20);
    public static final Animation PULL = Animation.create(20);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public float swimProgress;
    public float singProgress;
    public float tailYaw;
    public boolean swimming;
    public boolean singing;
    public boolean onGround;
    public int singingPose;
    public int hairColor;
    public boolean aggressive;
}
