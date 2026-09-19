package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Guardian statue pose inputs. The original camera/target lookup was unused after assignment. */
public class GuardianStatueRenderState extends EntityRenderState {
    public float poseAge;
    public float yRot;
    public float xRot;
    public float tailAnimation;
}
