package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.ModelDreadLichSkull;
import com.github.alexthe666.iceandfire.entity.EntityDragonLightningCharge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderDragonLightningCharge extends EntityRenderer<EntityDragonLightningCharge, DragonLightningChargeRenderState> {
    public static final Identifier TEXTURE = Identifier.parse("iceandfire:textures/models/lightningdragon/charge.png");
    public static final Identifier TEXTURE_CORE = Identifier.parse("iceandfire:textures/models/lightningdragon/charge_core.png");
    private final EntityModel<DragonLightningChargeRenderState> model;

    public RenderDragonLightningCharge(EntityRendererProvider.Context context) {
        super(context);
        ModelDreadLichSkull source = new ModelDreadLichSkull();
        model = new BasicEntityModel<DragonLightningChargeRenderState>() {
            @Override
            public Iterable<BasicModelPart> parts() {
                return source.parts();
            }

            @Override
            public void setupAnim(DragonLightningChargeRenderState state) {
                source.resetToDefaultPose();
            }
        }.asEntityModel();
    }

    @Override
    public DragonLightningChargeRenderState createRenderState() {
        return new DragonLightningChargeRenderState();
    }

    @Override
    public void extractRenderState(EntityDragonLightningCharge entity, DragonLightningChargeRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
    }

    @Override
    public void submit(DragonLightningChargeRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        float age = state.ageInTicks;
        RenderType swirl = RenderTypes.energySwirl(TEXTURE, age * 0.01F, age * 0.01F);
        submitPass(state, poses, collector, RenderTypes.eyes(TEXTURE_CORE), 0.5F, 0.25F, 20, 1);
        submitPass(state, poses, collector, swirl, 0.5F, 0.25F, 15, 1.5F);
        submitPass(state, poses, collector, swirl, 0.75F, 0.75F, 10, 2.5F);
        super.submit(state, poses, collector, camera);
    }

    private void submitPass(DragonLightningChargeRenderState state, PoseStack poses, SubmitNodeCollector collector,
                            RenderType type, float height, float offset, float speed, float scale) {
        poses.pushPose();
        poses.translate(0, height, 0);
        poses.translate(0, -0.25F, 0);
        poses.mulPose(Axis.YP.rotationDegrees(state.yaw - 180));
        poses.mulPose(Axis.XP.rotationDegrees(state.ageInTicks * speed));
        poses.translate(0, offset, 0);
        poses.scale(scale, scale, scale);
        collector.submitModel(model, state, poses, type, state.lightCoords,
            OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poses.popPose();
    }
}
