package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

/** Extracted appearance shared by living eggs and block-entity egg models. */
public class EggRenderState extends LivingEntityRenderState {
    public Identifier texture = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/firedragon/egg_red.png");
    public boolean inverted;
    public float wobbleAmount;
}
