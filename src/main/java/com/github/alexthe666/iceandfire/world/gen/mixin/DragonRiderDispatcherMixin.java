package com.github.alexthe666.iceandfire.world.gen.mixin;

import com.github.alexthe666.iceandfire.client.render.entity.DragonRiderRenderHooks;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class DragonRiderDispatcherMixin {
    @Inject(
        method = "extractEntity(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
        at = @At("RETURN"), require = 1
    )
    private void iceandfire$extractDragonRider(Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> callback) {
        // Only world/dispatcher snapshots are suppressed. LayerDragonRider extracts a separate state.
        DragonRiderRenderHooks.extractedStandalone(callback.getReturnValue(), entity.getVehicle() instanceof EntityDragonBase);
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
        at = @At("HEAD"), cancellable = true, require = 1
    )
    private void iceandfire$suppressStandaloneDragonRider(EntityRenderState state, CameraRenderState camera,
            double x, double y, double z, PoseStack poses, SubmitNodeCollector collector, CallbackInfo callback) {
        // Cancel before both renderer submission and the dispatcher's separate flame/shadow submissions.
        if (DragonRiderRenderHooks.shouldSuppress(state)) callback.cancel();
    }
}
