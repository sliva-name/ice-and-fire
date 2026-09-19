package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class GhostRenderState extends LivingEntityRenderState {
    public static final Animation SCARE = Animation.create(30);
    public static final Animation HIT = Animation.create(10);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public int color;
    public boolean daytime;
    public boolean shoppingList;
    public boolean spectator;
    public float alpha;
}
