package com.github.alexthe666.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.packets.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class EntityCyclopsEye extends EntityMutlipartPart {

    public EntityCyclopsEye(EntityType<?> t, Level world) {
        super(t, world);
    }

    public EntityCyclopsEye(SpawnEntity spawnEntity, Level worldIn) {
        this(IafEntityRegistry.CYCLOPS_MULTIPART.get(), worldIn);
    }

    public EntityCyclopsEye(LivingEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.CYCLOPS_MULTIPART.get(), parent, radius, angleYaw, offsetY, sizeX, sizeY,
            damageMultiplier);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float damage) {
        Entity parent = this.getParent();
        if (parent instanceof EntityCyclops && source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            ((EntityCyclops) parent).onHitEye(source, damage);
            return true;
        } else {
            return parent != null && parent.hurtOrSimulate(source, damage);
        }
    }
}
