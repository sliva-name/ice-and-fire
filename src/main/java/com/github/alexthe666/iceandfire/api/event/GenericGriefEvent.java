package com.github.alexthe666.iceandfire.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * Fired immediately before an entity destroys or modifies blocks.
 * Post on {@link #BUS}; a true return value means no destruction or explosion should follow.
 * Cancellable listeners return true to cancel the event.
 * For dragon breath terrain damage, see {@link DragonFireDamageWorldEvent}.
 */
public final class GenericGriefEvent extends MutableEvent implements LivingEvent, Cancellable {
    public static final CancellableEventBus<GenericGriefEvent> BUS = CancellableEventBus.create(GenericGriefEvent.class);

    private final LivingEntity griefer;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public GenericGriefEvent(LivingEntity griefer, double targetX, double targetY, double targetZ) {
        this.griefer = griefer;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    @Override
    public LivingEntity getEntity() {
        return griefer;
    }

    public LivingEntity getEntityLiving() {
        return getEntity();
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
