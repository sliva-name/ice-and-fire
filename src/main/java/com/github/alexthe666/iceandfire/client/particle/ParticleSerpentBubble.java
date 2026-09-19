package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import org.jetbrains.annotations.NotNull;

public class ParticleSerpentBubble extends SingleQuadParticle {

    public ParticleSerpentBubble(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, float size) {
        super(world, x, y, z, motX, motY, motZ, IafParticleSprites.get(IafParticleSprites.SEA_SERPENT_BUBBLE));
        this.setPos(x, y, z);
        this.quadSize = 0.3F;
    }

    @Override
    public void tick() {
        super.tick();
        if (age > this.getLifetime()) {
            this.remove();
        }
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return 240;
    }

    public int getFXLayer() {
        return 3;
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return IafParticleSprites.layer(this.sprite);
    }

}
