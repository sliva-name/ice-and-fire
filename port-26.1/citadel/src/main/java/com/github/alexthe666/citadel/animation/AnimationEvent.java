/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5 (LGPL).
 * Citadel by Alexthe666; code used from LLibrary with permission.
 * Credits: iLexiconn and Gegy1000. Modified for Forge EventBus 7.
 */
package com.github.alexthe666.citadel.animation;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.InheritableEvent;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/** Subscribe to {@link Start#BUS} or {@link Tick#BUS} on Forge 26.1. */
public abstract class AnimationEvent<T extends Entity & IAnimatedEntity> extends MutableEvent implements InheritableEvent {
    protected Animation animation;
    private final T entity;

    AnimationEvent(T entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public T getEntity() {
        return entity;
    }

    public Animation getAnimation() {
        return animation;
    }

    /** A cancelling predicate listener returns true; animation ticking continues. */
    public static final class Start<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> implements Cancellable {
        // A single bus serves every entity type; generic class literals are erased.
        @SuppressWarnings("rawtypes")
        public static final CancellableEventBus<Start> BUS = CancellableEventBus.create(Start.class);

        public Start(T entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }
    }

    public static final class Tick<T extends Entity & IAnimatedEntity> extends AnimationEvent<T> {
        @SuppressWarnings("rawtypes")
        public static final EventBus<Tick> BUS = EventBus.create(Tick.class);
        protected final int tick;

        public Tick(T entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return tick;
        }
    }
}
