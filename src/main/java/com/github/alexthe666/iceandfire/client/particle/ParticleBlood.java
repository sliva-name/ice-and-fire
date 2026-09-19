package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import org.jetbrains.annotations.NotNull;

public class ParticleBlood extends SingleQuadParticle {

    public ParticleBlood(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z, 0, Math.random() * (double) 0.2F + 0.1, 0, IafParticleSprites.get(IafParticleSprites.BLOOD));
        this.setPos(x, y, z);
        this.yd += 0.01D;
    }

    @Override
    public void tick() {
        super.tick();
        // 1.18 slowed the particle and shrank it every frame inside render().
        xd *= 0.75D;
        yd *= 0.75D;
        zd *= 0.75D;
        if (age > this.getLifetime()) {
            this.remove();
        }
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return Math.max(0.0F, 0.125F * (this.lifetime - (this.age + partialTicks)) * 0.09F);
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
