package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class HippocampusRenderState extends LivingEntityRenderState {
    public static final Animation SPEAK = Animation.create(15);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public float onLandProgress;
    public float sitProgress;
    public float tailYaw;
    public boolean onGround;
    public boolean inWater;
    public int variant;
    public boolean blinking;
    public boolean saddled;
    public boolean bridled;
    public boolean chested;
    public int armor;
    public boolean rainbow;
    public int rainbowColor = -1;
}
