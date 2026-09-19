package com.github.alexthe666.iceandfire.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Quaternionf;
import com.mojang.math.Axis;
import org.joml.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ParticleBlood extends SingleQuadParticle {
    private static final Identifier BLOOD = Identifier.parse("iceandfire:textures/particles/blood.png");

    public ParticleBlood(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z, 0, Math.random() * (double) 0.2F + 0.1, 0, IafParticleSprites.missing());
        this.setPos(x, y, z);
        this.yd += 0.01D;
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
        return IafParticleSprites.layer(BLOOD);
    }

    @Override
    protected float getU0() {
        return 0.0F;
    }

    @Override
    protected float getU1() {
        return 1.0F;
    }

    @Override
    protected float getV0() {
        return 0.0F;
    }

    @Override
    protected float getV1() {
        return 1.0F;
    }

}
