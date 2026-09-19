package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelPixie extends AdvancedEntityModel<PixieRenderState> {
    public AdvancedModelBox Body;
    public AdvancedModelBox Left_Arm;
    public AdvancedModelBox Head;
    public AdvancedModelBox Right_Arm;
    public AdvancedModelBox Neck;
    public AdvancedModelBox Left_Leg;
    public AdvancedModelBox Right_Leg;
    public AdvancedModelBox Left_Wing;
    public AdvancedModelBox Left_Wing2;
    public AdvancedModelBox Right_Wing;
    public AdvancedModelBox Right_Wing2;
    public AdvancedModelBox Dress;

    public ModelPixie() {
        this.texWidth = 32;
        this.texHeight = 32;
        this.Neck = new AdvancedModelBox(this, 40, 25);
        this.Neck.setPos(0.0F, -8.2F, 0.0F);
        this.Neck.addBox(-1.5F, -1.1F, -1.0F, 3, 1, 1, 0.0F);
        this.Right_Arm = new AdvancedModelBox(this, 0, 17);
        this.Right_Arm.setPos(-1.8F, -7.0F, 0.0F);
        this.Right_Arm.addBox(-0.6F, -0.5F, -1.0F, 1, 6, 1, 0.0F);
        this.setRotateAngle(Right_Arm, 0.0F, 0.0F, 0.17453292519943295F);
        this.Right_Wing2 = new AdvancedModelBox(this, 24, 10);
        this.Right_Wing2.setPos(-1.4F, -5.0F, -0.1F);
        this.Right_Wing2.addBox(-1.2F, -0.5F, 0.5F, 3, 10, 1, 0.0F);
        this.setRotateAngle(Right_Wing2, 0.5235987755982988F, -0.01832595714594046F, 1.0471975511965976F);
        this.Right_Wing = new AdvancedModelBox(this, 14, 10);
        this.Right_Wing.setPos(-1.2F, -6.3F, 0.4F);
        this.Right_Wing.addBox(-1.2F, -0.5F, 0.5F, 3, 12, 1, 0.0F);
        this.setRotateAngle(Right_Wing, 0.5235987755982988F, -0.2617993877991494F, 1.7453292519943295F);
        this.Body = new AdvancedModelBox(this, 0, 8);
        this.Body.setPos(0.0F, 16.9F, 0.5F);
        this.Body.addBox(-1.5F, -7.9F, -1.4F, 3, 5, 2, 0.0F);
        this.Right_Leg = new AdvancedModelBox(this, 5, 17);
        this.Right_Leg.mirror = true;
        this.Right_Leg.setPos(-0.8F, -1.5F, 0.0F);
        this.Right_Leg.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.Dress = new AdvancedModelBox(this, 0, 24);
        this.Dress.setPos(0.0F, -2.5F, 0.1F);
        this.Dress.addBox(-2.0F, -0.4F, -1.5F, 4, 3, 2, 0.0F);
        this.Head = new AdvancedModelBox(this, 0, 0);
        this.Head.setPos(0.0F, -8.0F, -0.8F);
        this.Head.addBox(-2.0F, -3.8F, -1.6F, 4, 4, 4, 0.0F);
        this.Left_Wing = new AdvancedModelBox(this, 14, 10);
        this.Left_Wing.mirror = true;
        this.Left_Wing.setPos(1.2F, -6.3F, 0.4F);
        this.Left_Wing.addBox(-1.8F, -0.5F, 0.5F, 3, 12, 1, 0.0F);
        this.setRotateAngle(Left_Wing, 0.5235987755982988F, 0.2617993877991494F, -1.7453292519943295F);
        this.Left_Leg = new AdvancedModelBox(this, 5, 17);
        this.Left_Leg.setPos(0.8F, -1.5F, 0.0F);
        this.Left_Leg.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.Left_Wing2 = new AdvancedModelBox(this, 24, 10);
        this.Left_Wing2.mirror = true;
        this.Left_Wing2.setPos(1.4F, -5.0F, -0.1F);
        this.Left_Wing2.addBox(-1.8F, -0.5F, 0.5F, 3, 10, 1, 0.0F);
        this.setRotateAngle(Left_Wing2, 0.5235987755982988F, 0.01832595714594046F, -1.0471975511965976F);
        this.Left_Arm = new AdvancedModelBox(this, 0, 17);
        this.Left_Arm.setPos(1.8F, -7.0F, 0.0F);
        this.Left_Arm.addBox(-0.6F, -0.5F, -0.9F, 1, 6, 1, 0.0F);
        this.setRotateAngle(Left_Arm, 0.0F, 0.0F, -0.17453292519943295F);
        this.Body.addChild(this.Neck);
        this.Body.addChild(this.Right_Arm);
        this.Body.addChild(this.Right_Wing2);
        this.Body.addChild(this.Right_Wing);
        this.Body.addChild(this.Right_Leg);
        this.Body.addChild(this.Dress);
        this.Body.addChild(this.Head);
        this.Body.addChild(this.Left_Wing);
        this.Body.addChild(this.Left_Leg);
        this.Body.addChild(this.Left_Wing2);
        this.Body.addChild(this.Left_Arm);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(Body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(Body, Left_Arm, Head, Right_Arm, Neck, Left_Leg, Right_Leg, Left_Wing,
            Left_Wing2, Right_Wing, Right_Wing2, Dress);
    }

    @Override
    public void setupAnim(PixieRenderState state) {
        super.setupAnim(state);
        if (state.mode == PixieRenderState.Mode.ENTITY) {
            float swing = state.walkAnimationPos;
            float amount = state.walkAnimationSpeed;
            this.Left_Leg.rotateAngleX = Mth.cos(swing * 0.6662F + (float) Math.PI) * amount * 0.5F;
            this.Right_Leg.rotateAngleX = Mth.cos(swing * 0.6662F) * amount * 0.5F;
            float lean = Mth.clamp(amount, 0.0F, (float) Math.toRadians(20));
            this.Body.rotateAngleX = lean;
            this.Head.rotateAngleX -= lean;
            if (state.holdingItem) {
                this.faceTarget(state.yRot, state.xRot, 1, this.Head);
                this.Left_Arm.rotateAngleX += (float) Math.toRadians(-35);
                this.Right_Arm.rotateAngleX += (float) Math.toRadians(-35);
                this.Body.rotateAngleX += (float) Math.toRadians(10);
                this.Left_Leg.rotateAngleX += (float) Math.toRadians(-10);
                this.Right_Leg.rotateAngleX += (float) Math.toRadians(-10);
                this.Head.rotateAngleX += (float) Math.toRadians(-10);
            } else {
                this.Right_Arm.rotateAngleX = Mth.cos(swing * 0.6662F + (float) Math.PI) * amount * 0.5F;
                this.Left_Arm.rotateAngleX = Mth.cos(swing * 0.6662F) * amount * 0.5F;
            }
        }
        if (state.sitting || state.mode == PixieRenderState.Mode.HOUSE) {
            applySittingPose();
        } else {
            this.chainWave(new AdvancedModelBox[]{Left_Wing, Left_Wing2}, 1.1F, 0.75F, 1, state.ageInTicks, 1);
            this.chainWave(new AdvancedModelBox[]{Right_Wing, Right_Wing2}, 1.1F, 0.75F, 1, state.ageInTicks, 1);
        }
    }

    private void applySittingPose() {
        this.Right_Arm.rotateAngleX -= (float) Math.PI / 5F;
        this.Left_Arm.rotateAngleX -= (float) Math.PI / 5F;
        this.Right_Leg.rotateAngleX = -1.4137167F;
        this.Right_Leg.rotateAngleY = (float) Math.PI / 10F;
        this.Right_Leg.rotateAngleZ = 0.07853982F;
        this.Left_Leg.rotateAngleX = -1.4137167F;
        this.Left_Leg.rotateAngleY = -(float) Math.PI / 10F;
        this.Left_Leg.rotateAngleZ = -0.07853982F;
        this.Dress.rotateAngleX += (float) Math.toRadians(-50);
        this.Dress.rotationPointZ += 0.25F;
        this.Dress.rotationPointY += 0.35F;
        this.Left_Wing.rotateAngleZ = (float) Math.toRadians(-28);
        this.Right_Wing.rotateAngleZ = (float) Math.toRadians(28);
        this.Left_Wing2.rotateAngleZ = (float) Math.toRadians(-8);
        this.Right_Wing2.rotateAngleZ = (float) Math.toRadians(8);
    }

    /** Direct statue entry point retained; the shared statue renderer still needs an adapter bridge. */
    public void renderStatue(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, Entity living) {
        this.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
    }
}
