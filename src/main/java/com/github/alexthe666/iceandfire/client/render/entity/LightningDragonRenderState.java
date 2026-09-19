package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.particle.LightningRender;

/** Lightning-only snapshot; the shared dragon model still consumes DragonRenderState. */
public class LightningDragonRenderState extends DragonRenderState {
    public LightningRender.Snapshot lightning = LightningRender.Snapshot.EMPTY;
}
