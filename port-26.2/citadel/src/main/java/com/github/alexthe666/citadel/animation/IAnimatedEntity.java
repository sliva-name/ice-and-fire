/*
 * Citadel animation API, based on 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Authors: Alexthe666; LLibrary: iLexiconn and Gegy1000. See NOTICE.md.
 */
package com.github.alexthe666.citadel.animation;

public interface IAnimatedEntity {
    Animation NO_ANIMATION = Animation.create(0);

    int getAnimationTick();

    void setAnimationTick(int tick);

    Animation getAnimation();

    void setAnimation(Animation animation);

    /** Entries must have corresponding ordering on the client and server. */
    Animation[] getAnimations();
}
