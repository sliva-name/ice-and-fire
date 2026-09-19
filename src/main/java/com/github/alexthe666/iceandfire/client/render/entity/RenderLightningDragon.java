package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderLightningDragon extends RenderDragonBase {
    private final LightningRender lightningRender = new LightningRender();

    public RenderLightningDragon(EntityRendererProvider.Context context, TabulaModel<DragonRenderState> model, int dragonType) {
        super(context, model, dragonType);
    }

    @Override
    public LightningDragonRenderState createRenderState() {
        return new LightningDragonRenderState();
    }

    @Override
    public boolean shouldRender(EntityDragonBase entity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, camera, camX, camY, camZ)) return true;
        if (entity instanceof EntityLightningDragon dragon && dragon.hasLightningTarget()) {
            return camera.isVisible(new AABB(dragon.getHeadPosition(), target(dragon)));
        }
        return false;
    }

    @Override
    public void extractRenderState(EntityDragonBase entity, DragonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var lightningState = (LightningDragonRenderState) state;
        lightningState.lightning = LightningRender.Snapshot.EMPTY;
        var minecraft = Minecraft.getInstance();
        if (!(entity instanceof EntityLightningDragon dragon) || !dragon.hasLightningTarget()
            || minecraft.player == null
            || !LightningRender.withinRange(minecraft.player.distanceToSqr(entity), minecraft.options.renderDistance().get())) {
            lightningRender.remove(entity);
            return;
        }
        // The dispatcher supplies the interpolated entity pose. Subtract the current position,
        // as the legacy renderer did, to keep the head/target and lingering bolts in that frame.
        lightningState.lightning = lightningRender.extract(entity, entity.level().getGameTime() + (double) partialTick,
            dragon.getHeadPosition(), target(dragon), dragon.getScale(), entity.position());
    }

    @Override
    public void submit(DragonRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poses, collector, camera);
        LightningRender.submit(((LightningDragonRenderState) state).lightning, poses, collector);
    }

    private static Vec3 target(EntityLightningDragon dragon) {
        return new Vec3(dragon.getLightningTargetX(), dragon.getLightningTargetY(), dragon.getLightningTargetZ());
    }
}
