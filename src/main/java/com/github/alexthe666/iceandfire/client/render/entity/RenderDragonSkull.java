package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RenderDragonSkull extends EntityRenderer<EntityDragonSkull, DragonSkullRenderState> {
    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    private final EntityModel<DragonSkullRenderState> fireDragonModel;
    private final EntityModel<DragonSkullRenderState> lightningDragonModel;
    private final EntityModel<DragonSkullRenderState> iceDragonModel;
    public float[][] growth_stages;

    public RenderDragonSkull(EntityRendererProvider.Context context, TabulaModel<DragonRenderState> fireDragonModel,
                             TabulaModel<DragonRenderState> iceDragonModel, TabulaModel<DragonRenderState> lightningDragonModel) {
        super(context);
        growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
        this.fireDragonModel = headModel(fireDragonModel);
        this.iceDragonModel = headModel(iceDragonModel);
        this.lightningDragonModel = headModel(lightningDragonModel);
    }

    @Override
    protected boolean affectedByCulling(EntityDragonSkull entity) {
        return false;
    }

    private static EntityModel<DragonSkullRenderState> headModel(TabulaModel<DragonRenderState> source) {
        BasicModelPart head = source.getCube("Head");
        return new BasicEntityModel<DragonSkullRenderState>() {
            @Override
            public Iterable<BasicModelPart> parts() {
                return List.of(head);
            }

            @Override
            public void setupAnim(DragonSkullRenderState state) {
                // Reapply the skull pose when the queued model is drawn, not during submission.
                source.resetToDefaultPose();
                head.rotateAngleX = state.onWall ? (float) Math.toRadians(50) : 0;
                head.rotateAngleY = 0;
                head.rotateAngleZ = 0;
            }
        }.asEntityModel();
    }

    @Override
    public DragonSkullRenderState createRenderState() {
        return new DragonSkullRenderState();
    }

    @Override
    public void extractRenderState(EntityDragonSkull entity, DragonSkullRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.dragonType = entity.getDragonType();
        state.yaw = entity.getYaw();
        state.size = getRenderSize(entity) / 3;
        state.onWall = entity.isOnWall();
        state.texture = getTextureLocation(entity);
    }

    @Override
    public void submit(DragonSkullRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        EntityModel<DragonSkullRenderState> model = state.dragonType == 2 ? lightningDragonModel
            : state.dragonType == 1 ? iceDragonModel : fireDragonModel;
        poses.pushPose();
        poses.mulPose(Axis.XP.rotationDegrees(-180));
        poses.mulPose(Axis.YN.rotationDegrees(180 - state.yaw));
        poses.scale(state.size, state.size, state.size);
        poses.translate(0, state.onWall ? -0.24F : -0.12F, state.onWall ? 0.4F : 0.5F);
        collector.submitModel(model, state, poses, RenderTypes.entityTranslucent(state.texture),
            state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poses.popPose();
    }

    public Identifier getTextureLocation(EntityDragonSkull entity) {
        if (entity.getDragonType() == 2) return EnumDragonTextures.getLightningDragonSkullTextures(entity);
        if (entity.getDragonType() == 1) return EnumDragonTextures.getIceDragonSkullTextures(entity);
        return EnumDragonTextures.getFireDragonSkullTextures(entity);
    }

    public float getRenderSize(EntityDragonSkull skull) {
        float step = (growth_stages[skull.getDragonStage() - 1][1] - growth_stages[skull.getDragonStage() - 1][0]) / 25;
        if (skull.getDragonAge() > 125) {
            return growth_stages[skull.getDragonStage() - 1][0] + step * 25;
        }
        return growth_stages[skull.getDragonStage() - 1][0] + step * getAgeFactor(skull);
    }

    private int getAgeFactor(EntityDragonSkull skull) {
        return skull.getDragonStage() > 1 ? skull.getDragonAge() - 25 * (skull.getDragonStage() - 1) : skull.getDragonAge();
    }
}
