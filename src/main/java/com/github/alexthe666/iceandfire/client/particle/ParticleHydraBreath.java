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

public class ParticleHydraBreath extends SingleQuadParticle {
    private static final Identifier HYDRA_POISON = Identifier.parse("iceandfire:textures/particles/hydra_poison.png");
    float reddustParticleScale;

    public ParticleHydraBreath(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, float p_i46349_8_, float p_i46349_9_, float p_i46349_10_) {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, 1F, p_i46349_8_, p_i46349_9_, p_i46349_10_);
    }

    protected ParticleHydraBreath(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, float scale, float red, float green, float blue) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D, IafParticleSprites.missing());
        this.xd *= 0.10000000149011612D;
        this.yd *= 0.10000000149011612D;
        this.zd *= 0.10000000149011612D;
        float f = (float) Math.random() * 0.4F + 0.6F;
        this.rCol = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * red * f;
        this.gCol = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * green * f;
        this.bCol = ((float) (Math.random() * 0.20000000298023224D) + 0.8F) * blue * f;
        this.quadSize *= scale;
        this.reddustParticleScale = this.quadSize;
        this.lifetime = (int) (4.0D / (Math.random() * 0.8D + 0.2D));
        this.lifetime = (int) ((float) this.lifetime * scale);
    }




    public void onUpdate() {
        this.xo = x;
        this.yo = y;
        this.zo = z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);

        if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
        }

        this.xd *= 0.9599999785423279D;
        this.yd *= 0.9599999785423279D;
        this.zd *= 0.9599999785423279D;

        if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return ParticleRenderType.SINGLE_QUADS;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return IafParticleSprites.layer(HYDRA_POISON);
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
