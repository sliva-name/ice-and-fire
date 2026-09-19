package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * Fired immediately before a dragon damages terrain with fire, ice or lightning.
 * Post on {@link #BUS}; a true return value means the damage should be skipped.
 * Cancellable listeners return true to cancel the event.
 * To prevent the breath entirely, see {@link DragonFireEvent}.
 */
public final class DragonFireDamageWorldEvent extends MutableEvent implements LivingEvent, Cancellable {
    public static final CancellableEventBus<DragonFireDamageWorldEvent> BUS = CancellableEventBus.create(DragonFireDamageWorldEvent.class);

    private final EntityDragonBase dragonBase;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public DragonFireDamageWorldEvent(EntityDragonBase dragonBase, double targetX, double targetY, double targetZ) {
        this.dragonBase = dragonBase;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    @Override
    public LivingEntity getEntity() {
        return dragonBase;
    }

    public LivingEntity getEntityLiving() {
        return getEntity();
    }

    public EntityDragonBase getDragon() {
        return dragonBase;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getTargetZ() {
        return targetZ;
    }
}
