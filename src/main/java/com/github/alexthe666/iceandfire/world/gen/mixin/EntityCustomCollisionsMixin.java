package com.github.alexthe666.iceandfire.world.gen.mixin;

import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.18 Citadel rewrote {@code Entity.collide} for {@link ICustomCollisions}.
 * 26.1 {@code collide} is private, so death-worm sand pass-through never ran.
 */
@Mixin(Entity.class)
public abstract class EntityCustomCollisionsMixin {
    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void iceandfire$customCollisions(Vec3 movement, CallbackInfoReturnable<Vec3> callback) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ICustomCollisions) {
            callback.setReturnValue(ICustomCollisions.getAllowedMovementForEntity(self, movement));
        }
    }
}
