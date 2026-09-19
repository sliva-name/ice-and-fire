package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class GhostSwordRenderState extends EntityRenderState {
    public float yRot;
    public float xRot;
    public final ItemStackRenderState item = new ItemStackRenderState();
}
