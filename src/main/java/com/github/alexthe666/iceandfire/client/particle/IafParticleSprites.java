package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

/**
 * 26.1 {@link SingleQuadParticle} draws from a pre-stitched atlas. Vanilla
 * {@code assets/minecraft/atlases/particles.json} lists every
 * {@code assets/<ns>/textures/particle/*.png} as sprite {@code <ns>:<name>}.
 * <p>
 * {@link SingleQuadParticle.Layer#textureAtlasLocation()} is bound during an
 * open render pass. A standalone PNG
 * ({@code iceandfire:textures/particles/pixie_dust.png}) is not preloaded, so
 * {@code TextureManager.getTexture} uploads mid-pass and crashes with
 * {@code Close the existing render pass before performing additional commands}.
 * Use {@link SingleQuadParticle.Layer#bySprite(TextureAtlasSprite)} so the
 * layer always points at the already-uploaded particle atlas.
 *
 * @see <a href="https://docs.minecraftforge.net/en/latest/gameeffects/particles/">Forge Particles</a>
 */
public final class IafParticleSprites {
    public static final Identifier BLOOD = sprite("blood");
    public static final Identifier DRAGON_FLAME = sprite("dragon_flame");
    public static final Identifier HYDRA_POISON = sprite("hydra_poison");
    public static final Identifier PIXIE_DUST = sprite("pixie_dust");
    public static final Identifier SEA_SERPENT_BUBBLE = sprite("sea_serpent_bubble");
    public static final Identifier SIREN_MUSIC = sprite("siren_music");
    public static final Identifier SNOWFLAKE = sprite("snowflake_0");
    public static final Identifier SNOWFLAKE_BIG = sprite("snowflake_1");

    private IafParticleSprites() {
    }

    private static Identifier sprite(String name) {
        return Identifier.fromNamespaceAndPath("iceandfire", name);
    }

    /** Sprite from the particle atlas ({@link AtlasIds#PARTICLES}). */
    public static TextureAtlasSprite get(Identifier spriteId) {
        return Minecraft.getInstance().getAtlasManager()
            .getAtlasOrThrow(AtlasIds.PARTICLES)
            .getSprite(spriteId);
    }

    /**
     * Layer whose atlas location is already uploaded. Never pass a raw PNG
     * Identifier here — that is what crashed the 26.1 client.
     */
    public static SingleQuadParticle.Layer layer(TextureAtlasSprite sprite) {
        return SingleQuadParticle.Layer.bySprite(sprite);
    }

    /** Particle atlas translucent layer. Safe if a stale class still calls {@code layer()}. */
    public static SingleQuadParticle.Layer layer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }
}
