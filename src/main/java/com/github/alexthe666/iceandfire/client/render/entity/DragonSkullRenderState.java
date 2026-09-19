package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public class DragonSkullRenderState extends EntityRenderState {
    public int dragonType;
    public float yaw;
    public float size;
    public boolean onWall;
    public Identifier texture;
}
