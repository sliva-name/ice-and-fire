package com.github.alexthe666.iceandfire.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ParticleSirenMusic extends SingleQuadParticle {

    float noteParticleScale;
    float colorScale;

    public ParticleSirenMusic(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, float size) {
        super(world, x, y, z, motX, motY, motZ, IafParticleSprites.get(IafParticleSprites.SIREN_MUSIC));
        this.setPos(x, y, z);
        this.colorScale = (float) 1;
        this.rCol = Math.max(0.0F, Mth.sin((colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin((colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin((colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
    }


    @Override
    public void tick() {
        super.tick();
        if (age > this.getLifetime()) {
            this.remove();
            return;
        }
        colorScale += 0.015;
        if (colorScale > 25) {
            colorScale = 0;
        }
        this.rCol = Math.max(0.0F, Mth.sin((colorScale + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin((colorScale + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin((colorScale + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);

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
