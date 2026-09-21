package com.github.alexthe666.iceandfire.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.packets.SpawnEntity;

public class EntityDragonPart extends EntityMutlipartPart {
    private static final float COVER = 1.2F;
    private EntityDragonBase dragon;
    private float tailLag;

    public EntityDragonPart(EntityType<?> t, Level world) {
        super(t, world);
    }

    public EntityDragonPart(SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.DRAGON_MULTIPART.get(), worldIn);
    }

    public EntityDragonPart(EntityType<?> type, EntityDragonBase dragon, float radius, float angleYaw, float offsetY,
        float sizeX, float sizeY, float damageMultiplier) {
        super(type, dragon, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
        this.dragon = dragon;
        enlarge();
    }

    public EntityDragonPart(EntityDragonBase parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.DRAGON_MULTIPART.get(), parent, radius, angleYaw, offsetY, sizeX, sizeY,
            damageMultiplier);
        this.dragon = parent;
        enlarge();
    }

    private void enlarge() {
        this.setScaleX(this.getScaleX() * COVER);
        this.setScaleY(this.getScaleY() * COVER);
    }

    @Override
    protected void placePart(Entity parent, float bodyYaw) {
        if (!(parent instanceof EntityDragonBase dragon)) {
            super.placePart(parent, bodyYaw);
            return;
        }
        float radius = this.radius;
        float angle = this.angleYaw;
        float modelScale = Math.max(dragon.getRenderSize() / 3F, 0.05F);
        boolean forward = radius > 0 && Mth.sin(angle) > 0.75F && Math.abs(Mth.cos(angle)) < 0.45F;
        boolean tail = radius < 0;
        boolean head = forward && offsetY / radius < 0.6F;
        if (head || tail) {
            radius *= 1.1F;
        }
        if (!forward && !tail) {
            float spread = Mth.clamp((dragon.flyProgress + dragon.hoverProgress) / 20F, 0F, 1F);
            float fold = (1F - spread) * 0.7F;
            angle += Mth.cos(angle) < 0 ? fold : -fold;
        }

        float lx = radius * Mth.cos(angle);
        float ly = this.offsetY;
        float lz = radius * Mth.sin(angle);
        if (forward) {
            float influence = head ? 0.5F : 0.25F;
            float lookYaw = Mth.wrapDegrees(dragon.yHeadRot - bodyYaw) * influence * ((float) Math.PI / 180F);
            float lookPitch = dragon.getXRot() * influence * ((float) Math.PI / 180F);
            float pivot = head ? 0.55F : 0.75F;
            float ox = lx * (1F - pivot);
            float oy = ly * (1F - pivot);
            float oz = lz * (1F - pivot);
            float cosY = Mth.cos(lookYaw);
            float sinY = Mth.sin(lookYaw);
            float rx = ox * cosY - oz * sinY;
            float rz = ox * sinY + oz * cosY;
            float cosX = Mth.cos(lookPitch);
            float sinX = Mth.sin(lookPitch);
            lx = lx * pivot + rx;
            ly = ly * pivot + oy * cosX - rz * sinX;
            lz = lz * pivot + oy * sinX + rz * cosX;
        } else if (tail) {
            float delta = Mth.wrapDegrees(dragon.yBodyRot - dragon.yBodyRotO);
            this.tailLag = Mth.clamp(this.tailLag * 0.85F + delta, -35F, 35F);
            float lever = Mth.clamp(Math.abs(radius) / (modelScale * 2.2F), 0.35F, 1.4F);
            float turn = -this.tailLag * lever * ((float) Math.PI / 180F);
            float cosY = Mth.cos(turn);
            float sinY = Mth.sin(turn);
            float rx = lx * cosY - lz * sinY;
            lz = lx * sinY + lz * cosY;
            lx = rx;
        }
        ly -= (dragon.sitProgress * 0.015F + dragon.sleepProgress * 0.025F) * modelScale;

        float pitch = dragon.getDragonPitch() * ((float) Math.PI / 180F);
        float cosP = Mth.cos(pitch);
        float sinP = Mth.sin(pitch);
        float pitchedY = ly * cosP - lz * sinP;
        float pitchedZ = ly * sinP + lz * cosP;
        float yaw = bodyYaw * ((float) Math.PI / 180F);
        float cosYaw = Mth.cos(yaw);
        float sinYaw = Mth.sin(yaw);
        this.setPos(parent.getX() + lx * cosYaw - pitchedZ * sinYaw, parent.getY() + pitchedY, parent.getZ() + lx * sinYaw + pitchedZ * cosYaw);
    }

    @Override
    public void collideWithNearbyEntities() {
    }

    @Override
    public boolean shouldNotExist() {
        return this.dragon != null && !this.dragon.isAlive() && !this.dragon.isModelDead();
    }
}
