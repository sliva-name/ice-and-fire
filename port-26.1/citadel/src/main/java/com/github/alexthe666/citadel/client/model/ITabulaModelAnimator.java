/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * See citadel/NOTICE.md for attribution and distribution restrictions.
 */
package com.github.alexthe666.citadel.client.model;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Animates an already reset Tabula pose from extracted render state only.
 * Callers extract limb motion, look angles, animation token/tick and partial
 * tick into their own state subtype. Keyframe users must explicitly call
 * model.llibAnimator.update(animation, tick, partialTick) each evaluation;
 * neither live entities nor Minecraft's frame-time singleton are consulted.
 * This intentionally replaces the legacy entity-and-floats callback.
 */
@FunctionalInterface
public interface ITabulaModelAnimator<S extends EntityRenderState> {
    void setRotationAngles(TabulaModel<S> model, S state);
}
