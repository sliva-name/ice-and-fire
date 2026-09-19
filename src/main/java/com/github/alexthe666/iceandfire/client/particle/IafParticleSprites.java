package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * 26.1 {@link SingleQuadParticle} needs an atlas sprite in the ctor. IAF particles
 * still bind the same standalone 1.18 PNGs via {@link #layer(Identifier)} and
 * full-sprite UVs (0..1), matching the old {@code RenderSystem.setShaderTexture}
 * + 0/1 UV path. AtlasManager keys are {@link AtlasIds} ({@code minecraft:particles}),
 * not {@code TextureAtlas.LOCATION_PARTICLES} ({@code textures/atlas/particles.png}).
 */
public final class IafParticleSprites {
    private IafParticleSprites() {
    }

    public static TextureAtlasSprite missing() {
        return Minecraft.getInstance().getAtlasManager()
            .getAtlasOrThrow(AtlasIds.PARTICLES)
            .missingSprite();
    }

    public static SingleQuadParticle.Layer layer(Identifier texture) {
        return new SingleQuadParticle.Layer(true, texture, SingleQuadParticle.Layer.TRANSLUCENT.pipeline());
    }
}
