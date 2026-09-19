package com.github.alexthe666.iceandfire.entity.util;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;

/**
 * 1.18 {@code getOwnerUUID}/{@code setOwnerUUID} on {@link TamableAnimal}.
 * 26.1 stores the same UUID on {@link EntityReference}.
 */
public final class IafOwners {
    private IafOwners() {
    }

    @Nullable
    public static UUID getUUID(OwnableEntity entity) {
        EntityReference<LivingEntity> ref = entity.getOwnerReference();
        return ref == null ? null : ref.getUUID();
    }

    public static void setUUID(TamableAnimal entity, @Nullable UUID owner) {
        entity.setOwnerReference(owner == null ? null : EntityReference.of(owner));
    }
}
