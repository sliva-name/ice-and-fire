package com.github.alexthe666.iceandfire.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.packets.SpawnEntity;

public class EntityDragonPart extends EntityMutlipartPart {
    public enum Role { HEAD, NECK, WING, TAIL, BODY }

    private static final float COVER = 1.2F;
    private static final int NECK_COUNT = 4;
    private EntityDragonBase dragon;
    private float tailLag;
    private Role role;

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

    public void setRole(Role role) {
        this.role = role;
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
        Role role = this.role != null ? this.role : inferRole(radius, angle);
        boolean head = role == Role.HEAD;
        boolean neck = role == Role.NECK;
        boolean tail = role == Role.TAIL;
        boolean wing = role == Role.WING;
        if (head || tail) {
            radius *= 1.1F;
        }
        if (wing) {
            float spread = Mth.clamp((dragon.flyProgress + dragon.hoverProgress) / 20F, 0F, 1F);
            float fold = (1F - spread) * 0.7F;
            angle += Mth.cos(angle) < 0 ? fold : -fold;
        }

        float lx = radius * Mth.cos(angle);
        float ly = this.offsetY;
        float lz = radius * Mth.sin(angle);
        if (head || neck) {
            float influence = head ? 0.5F : 0.25F;
            int lastIndex = head ? 3 : 1;
            float lookYaw = Mth.wrapDegrees(dragon.yHeadRot - bodyYaw) * influence * ((float) Math.PI / 180F);
            float lookPitch = dragon.getXRot() * influence * ((float) Math.PI / 180F);
            if (!dragon.isModelDead()) {
                lookYaw += neckSwing(dragon, lastIndex);
                lookPitch += neckWave(dragon, lastIndex);
            }
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
        } else if (wing && !grounded(dragon)) {
            float spread = Mth.clamp((dragon.flyProgress + dragon.hoverProgress) / 20F, 0F, 1F);
            float sign = Math.signum(lx == 0F ? 1F : lx);
            float shoulder = sign * 0.23F * modelScale;
            float ox = lx - shoulder;
            float flap = -sign * (wingFlapDegrees(dragon.flightCycle) + 120F) * ((float) Math.PI / 180F) * spread;
            float cosF = Mth.cos(flap);
            float sinF = Mth.sin(flap);
            float nx = ox * cosF - ly * sinF;
            ly = ox * sinF + ly * cosF;
            lx = nx + shoulder;
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
        if (role == Role.BODY) {
            ly += bodyBob(dragon) / 16F * modelScale * (radius < 0 ? 0.65F : 1F);
        }

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

    private Role inferRole(float radius, float angle) {
        boolean forward = radius > 0 && Mth.sin(angle) > 0.75F && Math.abs(Mth.cos(angle)) < 0.45F;
        if (radius < 0) {
            return Role.TAIL;
        }
        if (!forward) {
            return Role.WING;
        }
        return this.offsetY / radius < 0.6F ? Role.HEAD : Role.NECK;
    }

    /** Left arm Z of Flight1–6, degrees. The flying pose sits at -120. */
    private static final float[] LEFT_WING_FLAP = {-139F, -93F, -35F, -36F, -75F, -120F};

    private static boolean grounded(EntityDragonBase dragon) {
        return !dragon.isFlying() && !dragon.isHovering() && dragon.flyProgress <= 0.0F && dragon.hoverProgress <= 0.0F;
    }

    private static float chainSum(int lastIndex, float speed, float degree, double rootOffset, float time, float amount) {
        float sum = 0;
        for (int i = 0; i <= lastIndex; i++) {
            float offset = (float) (rootOffset * Math.PI / (2 * NECK_COUNT));
            sum += Mth.cos(time * speed + offset * i) * amount * degree;
        }
        return sum;
    }

    private static float neckSwing(EntityDragonBase dragon, int lastIndex) {
        if (!grounded(dragon)) {
            return 0;
        }
        return chainSum(lastIndex, 0.2F, 0.075F, 2, dragon.walkAnimation.position(), dragon.walkAnimation.speed());
    }

    private static float neckWave(EntityDragonBase dragon, int lastIndex) {
        if (!grounded(dragon)) {
            return chainSum(lastIndex, 0.2F, 0.1F, -4, dragon.tickCount, 1);
        }
        float idleDegree = dragon.isSleeping() ? 0.25F : 0.5F;
        float idleSpeed = dragon.isSleeping() ? 0.025F : 0.05F;
        float idleWave = chainSum(lastIndex, idleSpeed, idleDegree * -0.15F, -3, dragon.tickCount, 1);
        float walkWave = chainSum(lastIndex, 0.2F, 0.025F, -2, dragon.walkAnimation.position(), dragon.walkAnimation.speed());
        return walkWave + idleWave;
    }

    /**
     * Same blend as the flight-pose cycle: at a multiple of 10 the previous pose is shown whole.
     */
    private static float wingFlapDegrees(int flightCycle) {
        int index = Math.floorMod(flightCycle / 10, LEFT_WING_FLAP.length);
        int prev = Math.floorMod(index - 1, LEFT_WING_FLAP.length);
        float delta = (flightCycle / 10.0F) % 1.0F;
        if (delta == 0.0F) {
            return LEFT_WING_FLAP[prev];
        }
        return LEFT_WING_FLAP[prev] + delta * Mth.wrapDegrees(LEFT_WING_FLAP[index] - LEFT_WING_FLAP[prev]);
    }

    /** BodyUpper bob from the animator, in model pixels. Positive is down. */
    private static float bodyBob(EntityDragonBase dragon) {
        float idleDegree = dragon.isSleeping() ? 0.25F : 0.5F;
        float idleSpeed = dragon.isSleeping() ? 0.025F : 0.05F;
        float idle = Mth.sin(dragon.tickCount * idleSpeed) * idleDegree * 1.3F - idleDegree * 1.3F;
        if (!grounded(dragon)) {
            float fly = Mth.sin(dragon.tickCount * -0.2F) * 2.5F - 2.5F;
            return idle + fly;
        }
        float amount = dragon.walkAnimation.speed();
        float walk = Mth.sin(dragon.walkAnimation.position() * 0.4F) * amount * 0.85F - amount * 0.85F;
        return idle + walk;
    }

    @Override
    public void collideWithNearbyEntities() {
    }

    @Override
    public boolean shouldNotExist() {
        return this.dragon != null && !this.dragon.isAlive() && !this.dragon.isModelDead();
    }
}
