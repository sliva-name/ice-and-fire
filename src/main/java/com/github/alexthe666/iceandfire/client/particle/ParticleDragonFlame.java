package com.github.alexthe666.iceandfire.client.particle;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ParticleDragonFlame extends SingleQuadParticle {

    private final float dragonSize;
    private final double initialX;
    private final double initialY;
    private final double initialZ;
    private double targetX;
    private double targetY;
    private double targetZ;
    private final int touchedTime = 0;
    private final float speedBonus;
    @Nullable
    private EntityDragonBase dragon;

    public ParticleDragonFlame(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float dragonSize) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, IafParticleSprites.get(IafParticleSprites.DRAGON_FLAME));
        this.initialX = xCoordIn;
        this.initialY = yCoordIn;
        this.initialZ = zCoordIn;
        targetX = xCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * dragonSize);
        targetY = yCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * dragonSize);
        targetZ = zCoordIn + (double) ((this.random.nextFloat() - this.random.nextFloat()) * 1.75F * dragonSize);
        this.setPos(x, y, z);
        this.dragonSize = dragonSize;
        this.speedBonus = random.nextFloat() * 0.015F;
    }

    public ParticleDragonFlame(ClientLevel world, double x, double y, double z, double motX, double motY, double motZ, EntityDragonBase entityDragonBase, int startingAge) {
        this(world, x, y, z, motX, motY, motZ, Mth.clamp(entityDragonBase.getRenderSize() * 0.08F, 0.55F, 3F));
        this.dragon = entityDragonBase;
        this.targetX = dragon.burnParticleX + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
        this.targetY = dragon.burnParticleY + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
        this.targetZ = dragon.burnParticleZ + (double) ((this.random.nextFloat() - this.random.nextFloat())) * 3.5F;
        this.x = x;
        this.y = y;
        this.z = z;
        this.age = startingAge;
    }

    @Override
    public int getLifetime() {
        return dragon == null ? 10 : 30;
    }

    @Override
    protected int getLightCoords(float partialTick) {
        // 1.18 getLightColor: dragon fire is always fullbright.
        return 240;
    }

    @Override
    public void tick() {
        super.tick();
        // 1.18 removed the particle from render() once it outlived getLifetime().
        if (age > this.getLifetime()) {
            this.remove();
            return;
        }

        if (dragon == null) {
            float distX = (float) (this.initialX - x);
            float distZ = (float) (this.initialZ - z);
            this.xd += distX * -0.01F * dragonSize * random.nextFloat();
            this.zd += distZ * -0.01F * dragonSize * random.nextFloat();
            this.yd += 0.015F * random.nextFloat();
        } else {
            double d2 = this.targetX - initialX;
            double d3 = this.targetY - initialY;
            double d4 = this.targetZ - initialZ;
            double dist = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
            float speed = 0.015F + speedBonus;
            this.xd += d2 * speed;
            this.yd += d3 * speed;
            this.zd += d4 * speed;
            if (touchedTime > 3) {
                this.remove();
            }
        }
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
