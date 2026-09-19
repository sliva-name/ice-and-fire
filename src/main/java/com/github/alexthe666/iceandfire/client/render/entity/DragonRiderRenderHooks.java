package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/** Per-snapshot routing for dragon passengers; no entity or frame-global rider registry is retained. */
public final class DragonRiderRenderHooks {
    private DragonRiderRenderHooks() {}

    /** Implemented on every native entity state by the client-only EntityRenderState mixin. */
    public interface StateAccess {
        boolean iceandfire$isStandaloneDragonRider();
        void iceandfire$setStandaloneDragonRider(boolean value);
    }

    public static void extractedStandalone(EntityRenderState state, boolean dragonPassenger) {
        access(state).iceandfire$setStandaloneDragonRider(dragonPassenger);
    }

    public static void extractedNested(EntityRenderState state) {
        access(state).iceandfire$setStandaloneDragonRider(false);
    }

    public static boolean shouldSuppress(EntityRenderState state) {
        return access(state).iceandfire$isStandaloneDragonRider();
    }

    private static StateAccess access(EntityRenderState state) {
        // Fail visibly if the required mixin was not applied instead of silently rendering duplicates.
        return (StateAccess) state;
    }
}
