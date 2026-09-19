/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5 (LGPL).
 * Citadel by Alexthe666; animation code from LLibrary, credited to iLexiconn
 * and Gegy1000. Modified for the Ice and Fire Forge 26.1 subset.
 */
package com.github.alexthe666.citadel.animation;

import com.github.alexthe666.citadel.network.AnimationNetwork;
import com.github.alexthe666.citadel.network.AnimationMessage;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author iLexiconn
 * @since 1.0.0
 */
public enum AnimationHandler {
    INSTANCE;

    /**
     * Sets the server animation without resetting its tick and notifies tracking
     * clients (including the entity itself when it is a player).
     */
    public <T extends Entity & IAnimatedEntity> void sendAnimationMessage(T entity, Animation animation) {
        if (entity.level().isClientSide()) {
            return;
        }
        entity.setAnimation(animation);
        AnimationNetwork.send(entity, new AnimationMessage(entity.getId(),
                ArrayUtils.indexOf(entity.getAnimations(), animation)));
    }

    public <T extends Entity & IAnimatedEntity> void updateAnimations(T entity) {
        if (entity.getAnimation() == null) {
            entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
        } else if (entity.getAnimation() != IAnimatedEntity.NO_ANIMATION) {
            if (entity.getAnimationTick() == 0) {
                AnimationEvent.Start<T> event = new AnimationEvent.Start<>(entity, entity.getAnimation());
                // Cancellation suppresses synchronization, not local advancement.
                if (!AnimationEvent.Start.BUS.post(event)) {
                    sendAnimationMessage(entity, event.getAnimation());
                }
            }
            if (entity.getAnimationTick() < entity.getAnimation().getDuration()) {
                entity.setAnimationTick(entity.getAnimationTick() + 1);
                AnimationEvent.Tick.BUS.post(new AnimationEvent.Tick<>(entity,
                        entity.getAnimation(), entity.getAnimationTick()));
            }
            if (entity.getAnimationTick() == entity.getAnimation().getDuration()) {
                entity.setAnimationTick(0);
                entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            }
        }
    }
}
