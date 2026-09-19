package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import org.jetbrains.annotations.NotNull;

public class ParticleDreadPortal extends SingleQuadParticle {

    private final boolean big;

    public ParticleDreadPortal(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, float size) {
        super(world, x, y, z, motX, motY, motZ, IafParticleSprites.get(IafParticleSprites.SNOWFLAKE));
        this.setPos(x, y, z);
        big = random.nextBoolean();
        if (big) {
            this.setSprite(IafParticleSprites.get(IafParticleSprites.SNOWFLAKE_BIG));
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 1.18 shrank the particle every frame inside render().
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

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return IafParticleSprites.layer(this.sprite);
    }

    public int getFXLayer() {
        return 3;
    }

}
