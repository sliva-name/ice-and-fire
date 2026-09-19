/*
 * Adapted from Citadel ClientProxy at
 * 8018e44d8b569913ca828f31aa6c86163319e7a5 (LGPL), by Alexthe666.
 * LLibrary credits: iLexiconn and Gegy1000. Modified for Forge 26.1.
 */
package com.github.alexthe666.citadel.client.animation;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.network.AnimationMessage;
import net.minecraft.client.Minecraft;

/** Loaded only on the physical client; invoked by the main-thread packet consumer. */
public final class ClientAnimationHandler {
    private ClientAnimationHandler() {
    }

    public static void handle(AnimationMessage message) {
        var level = Minecraft.getInstance().level;
        if (level == null || !(level.getEntity(message.entityId()) instanceof IAnimatedEntity entity)) {
            return;
        }
        Animation animation;
        if (message.index() == -1) {
            animation = IAnimatedEntity.NO_ANIMATION;
        } else {
            Animation[] animations = entity.getAnimations();
            if (animations == null || message.index() < 0 || message.index() >= animations.length) {
                return;
            }
            animation = animations[message.index()];
            if (animation == null) {
                return;
            }
        }
        entity.setAnimation(animation);
        entity.setAnimationTick(0);
    }
}
