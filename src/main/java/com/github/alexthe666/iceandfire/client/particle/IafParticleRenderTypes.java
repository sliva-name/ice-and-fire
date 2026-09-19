package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.particle.ParticleRenderType;

/**
 * Custom groups registered through {@link net.minecraft.client.particle.ParticleEngine#registerParticleGroup}.
 * Vanilla only ships {@code ELDER_GUARDIANS} for model particles; anything else falls through to
 * {@code NO_RENDER} or a quad group.
 */
public final class IafParticleRenderTypes {
    public static final ParticleRenderType APPEARANCE = new ParticleRenderType("ICEANDFIRE_APPEARANCE");

    private IafParticleRenderTypes() {
    }
}
