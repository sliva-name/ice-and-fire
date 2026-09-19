package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class DreadHumanoidRenderState extends HumanoidRenderState {
    public static final Animation SPAWN = Animation.create(40);
    public static final Animation SLASH = Animation.create(25);
    public static final Animation SUMMON = Animation.create(15);

    public Animation animation = IAnimatedEntity.NO_ANIMATION;
    public int animationTick;
    public float partialTick;
    public int tickCount;
    public int variant;
    public int screamStage;
    public int armorVariant;
    public boolean hideHeldItems;
    public Identifier texture;
}
