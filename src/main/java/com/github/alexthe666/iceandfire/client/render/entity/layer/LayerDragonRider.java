package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.DragonRiderRenderHooks;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;

public class LayerDragonRider extends RenderLayer<DragonRenderState, EntityModel<DragonRenderState>> {
    private final RenderDragonBase renderer;
    private final boolean excludeDreadQueenMob;

    public LayerDragonRider(RenderDragonBase renderer, boolean excludeDreadQueenMob) {
        super(renderer);
        this.renderer = renderer;
        this.excludeDreadQueenMob = excludeDreadQueenMob;
    }

    public void extract(EntityDragonBase dragon, DragonRenderState state, float partialTick) {
        state.riders.clear();
        for (Entity passenger : dragon.getPassengers()) {
            // The camera player must not be drawn by the nested path in first person either.
            if (passenger == Minecraft.getInstance().player && Minecraft.getInstance().options.getCameraType().isFirstPerson()) continue;
            extractPassenger(dragon, passenger, state, partialTick);
        }
    }

    private <E extends Entity> void extractPassenger(EntityDragonBase dragon, E passenger, DragonRenderState state, float partialTick) {
        var riderRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(passenger);
        var nested = riderRenderer.createRenderState(passenger, partialTick);
        DragonRiderRenderHooks.extractedNested(nested);
        nested.lightCoords = state.lightCoords;
        var model = riderRenderer instanceof LivingEntityRenderer<?, ?, ?> living ? living.getModel() : null;
        boolean horse = model instanceof HorseModel;
        boolean upright = (passenger.getBbHeight() > passenger.getBbWidth() || model instanceof HumanoidModel)
            && !(model instanceof QuadrupedModel) && !horse;
        boolean prey = dragon.getControllingPassenger() != passenger;
        float yaw = passenger.yRotO + (passenger.getYRot() - passenger.yRotO) * partialTick;
        if (passenger instanceof EntityDreadQueen) {
            prey = false;
            yaw = 0.0F;
            if (nested instanceof LivingEntityRenderState living) {
                living.bodyRot = 0.0F;
                living.yRot = 0.0F;
                living.xRot = 0.0F;
                living.walkAnimationSpeed = 0.0F;
            }
            if (nested instanceof HumanoidRenderState humanoid) {
                humanoid.isPassenger = true;
            }
        }
        state.riders.add(new DragonRenderState.Rider(nested, riderRenderer, yaw, prey, upright, horse));
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, DragonRenderState state, float yaw, float pitch) {
        renderer.dragonModel().setupAnim(state);
        float scale = state.dragonScale;
        int ticks = state.animation == DragonRenderState.AnimationKind.SHAKEPREY ? state.animationTick : 0;
        for (var rider : state.riders) {
            poses.pushPose();
            try {
                if (ticks == 0 || ticks >= 15) attach(poses, "BodyUpper", "Neck1");
                if (rider.prey()) {
                    if (ticks == 0 || ticks >= 15 || state.flying) {
                        attach(poses, "Neck2", "Neck3", "Head");
                        if (state.dragonType == 2) poses.translate(0.1F, -0.2F, -0.1F);
                        float height = rider.state().boundingBoxHeight, width = rider.state().boundingBoxWidth;
                        if (rider.upright()) {
                            poses.translate(-0.15F * height, 0.1F * scale - 0.1F * height, -0.1F * scale - 0.1F * width);
                            poses.mulPose(Axis.ZP.rotationDegrees(90));
                            poses.mulPose(Axis.YP.rotationDegrees(45));
                        } else {
                            poses.translate((rider.horse() ? -0.08F : -0.15F) * width, 0.1F * scale - 0.15F * width, -0.1F * scale - 0.1F * width);
                            poses.mulPose(Axis.XN.rotationDegrees(90));
                        }
                    } else poses.translate(0, 0.555F * scale, -0.5F * scale);
                } else poses.translate(0, -0.01F * scale, -0.035F * scale);
                poses.mulPose(Axis.ZP.rotationDegrees(180));
                poses.mulPose(Axis.YP.rotationDegrees(rider.yaw() + 180));
                poses.scale(1 / scale, 1 / scale, 1 / scale);
                poses.translate(0, -0.25F, 0);
                submitRider(rider, poses, collector, state);
            } finally {
                poses.popPose();
            }
        }
    }

    private void attach(PoseStack poses, String... names) {
        for (String name : names) renderer.dragonModel().getCube(name).translateAndRotate(poses);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void submitRider(DragonRenderState.Rider rider, PoseStack poses, SubmitNodeCollector collector, DragonRenderState dragon) {
        // The renderer/state pair is created together by extractPassenger; capture is erased only here.
        ((EntityRenderer) rider.renderer()).submit(rider.state(), poses, collector, dragon.camera);
    }
}
