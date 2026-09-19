/*
 * Citadel animation API, based on 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Authors: Alexthe666; LLibrary: iLexiconn and Gegy1000. See NOTICE.md.
 */
package com.github.alexthe666.citadel.animation;

/** An identity-based animation token. Duration is measured in entity ticks. */
public class Animation {
    private int id;
    private final int duration;

    private Animation(int duration) {
        this.duration = duration;
    }

    /** @deprecated Network synchronization uses the entity's animation array, not IDs. */
    @Deprecated
    public static Animation create(int id, int duration) {
        Animation animation = create(duration);
        animation.id = id;
        return animation;
    }

    public static Animation create(int duration) {
        return new Animation(duration);
    }

    /** @deprecated Network synchronization uses the entity's animation array, not IDs. */
    @Deprecated
    public int getID() {
        return id;
    }

    public int getDuration() {
        return duration;
    }
}
