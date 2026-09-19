package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.enums.EnumSkullType;

/** Uses the sea serpent's native state type even when rendering only its head. */
public class MobSkullRenderState extends SeaSerpentRenderState {
    public EnumSkullType skullType = EnumSkullType.HIPPOGRYPH;
    public float yaw;
    public boolean onWall;
}
