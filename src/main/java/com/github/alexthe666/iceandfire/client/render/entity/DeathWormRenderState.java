package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class DeathWormRenderState extends LivingEntityRenderState {
    public static final Animation BITE = Animation.create(10);
    public Animation animation;
    public int animationTick;
    public float partialTick;
    public float jumpProgress;
    public float tailYaw;
    public boolean jumping;
    public int variant;
}
