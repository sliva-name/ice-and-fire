package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class HippogryphRenderState extends LivingEntityRenderState {
    public static final Animation SPEAK = Animation.create(15);
    public static final Animation EAT = Animation.create(25);
    public static final Animation BITE = Animation.create(20);
    public static final Animation SCRATCH = Animation.create(25);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public float sitProgress;
    public float hoverProgress;
    public float flyProgress;
    public boolean flying;
    public boolean hovering;
    public int airBorneCounter;
    public boolean dodo;
    public Identifier texture;
    public boolean saddled;
    public boolean bridled;
    public boolean chested;
    public int armor;
}
