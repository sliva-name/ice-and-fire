package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Extracted Hydra inputs; no entity or entity-owned progress arrays are retained. */
public class HydraRenderState extends LivingEntityRenderState {
    public static final int MAX_HEADS = 9;

    public Animation animation = IAnimatedEntity.NO_ANIMATION;
    public int animationTick;
    public float partialTick;
    public int variant;
    public int headCount = 1;
    /** Zero-based severed head index, or -1 for none. Neck1 remains as the stump. */
    public int severedHead = -1;
    public boolean alive = true;
    public boolean stone;

    // Interpolated once at extraction, for all nine slots (including currently unused heads).
    public final float[] strikingProgress = new float[MAX_HEADS];
    public final float[] breathProgress = new float[MAX_HEADS];
    public final float[] speakingProgress = new float[MAX_HEADS];
}
