package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.HideableModelRenderer;
import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import net.minecraft.client.model.HumanoidModel;

public class ModelDreadKnight extends ModelDreadBase {
    public HideableModelRenderer chestplate;
    public HideableModelRenderer cloak;
    public HideableModelRenderer crown;
    public HideableModelRenderer sleeveRight;
    public HideableModelRenderer robeLowerRight;
    public HideableModelRenderer sleeveLeft;
    public HideableModelRenderer robeLowerLeft;

    public ModelDreadKnight(float modelScale) {
        this.texWidth = 128;
        this.texHeight = 64;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.sleeveRight = new HideableModelRenderer(this, 35, 33);
        this.sleeveRight.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveRight.addBox(-4.0F, -2.1F, -2.5F, 5, 6, 5, modelScale);
        this.chestplate = new HideableModelRenderer(this, 1, 32);
        this.chestplate.setPos(0.0F, 0.1F, 0.0F);
        this.chestplate.addBox(-4.5F, 0.0F, -2.5F, 9, 11, 5, modelScale);
        this.crown = new HideableModelRenderer(this, 58, -1);
        this.crown.setPos(0.0F, 0.0F, 0.0F);
        this.crown.addBox(-4.5F, -10.2F, -4.5F, 9, 11, 9, modelScale);
        this.body = new HideableModelRenderer(this, 16, 16);
        this.body.setPos(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelScale);
        this.legRight = new HideableModelRenderer(this, 0, 16);
        this.legRight.setPos(-1.9F, 12.0F, 0.1F);
        this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.armLeft = new HideableModelRenderer(this, 40, 16);
        this.armLeft.mirror = true;
        this.armLeft.setPos(5.0F, 2.0F, -0.0F);
        this.armLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelScale);
        this.setRotateAngle(armLeft, -0.0F, 0.10000736613927509F, -0.10000736613927509F);
        this.legLeft = new HideableModelRenderer(this, 0, 16);
        this.legLeft.mirror = true;
        this.legLeft.setPos(1.9F, 12.0F, 0.1F);
        this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelScale);
        this.head = new HideableModelRenderer(this, 0, 0);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelScale);
        this.sleeveLeft = new HideableModelRenderer(this, 35, 33);
        this.sleeveLeft.mirror = true;
        this.sleeveLeft.setPos(0.0F, -0.1F, 0.0F);
        this.sleeveLeft.addBox(-1.0F, -2.1F, -2.5F, 5, 6, 5, modelScale);
        this.robeLowerRight = new HideableModelRenderer(this, 58, 33);
        this.robeLowerRight.mirror = true;
        this.robeLowerRight.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerRight.addBox(-2.1F, 0.0F, -2.5F, 4, 7, 5, modelScale);
        this.cloak = new HideableModelRenderer(this, 81, 37);
        this.cloak.setPos(0.0F, 0.1F, 0.0F);
        this.cloak.addBox(-4.5F, 0.0F, 2.3F, 9, 21, 1, modelScale);
        this.setRotateAngle(cloak, 0.045553093477052F, 0.0F, 0.0F);
        this.armRight = new HideableModelRenderer(this, 40, 16);
        this.armRight.setPos(-5.0F, 2.0F, 0.0F);
        this.armRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelScale);
        this.setRotateAngle(armRight, -0.0F, -0.10000736613927509F, 0.10000736613927509F);
        this.robeLowerLeft = new HideableModelRenderer(this, 58, 33);
        this.robeLowerLeft.setPos(0.0F, -0.2F, 0.0F);
        this.robeLowerLeft.addBox(-1.9F, 0.0F, -2.5F, 4, 7, 5, modelScale);
        this.armRight.addChild(this.sleeveRight);
        this.body.addChild(this.chestplate);
        this.head.addChild(this.crown);
        this.body.addChild(this.legRight);
        this.body.addChild(this.armLeft);
        this.body.addChild(this.legLeft);
        this.body.addChild(this.head);
        this.armLeft.addChild(this.sleeveLeft);
        this.legRight.addChild(this.robeLowerRight);
        this.body.addChild(this.cloak);
        this.body.addChild(this.armRight);
        this.legLeft.addChild(this.robeLowerLeft);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    @Override
    public void setRotationAnglesSpawn(DreadHumanoidRenderState state) {
        return;
    }

    @Override
    public void animate(DreadHumanoidRenderState state) {
        return;
    }

    @Override
    public Animation getSpawnAnimation() {
        return DreadHumanoidRenderState.SPAWN;
    }

}