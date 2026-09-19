package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Native adapter that forwards held-item attachment to the Citadel arm, matching the
 * original {@code translateToHand} that only transformed the arm itself.
 */
public final class ArmedCitadelEntityModel extends EntityModel<DreadHumanoidRenderState>
    implements ArmedModel<DreadHumanoidRenderState> {
    private final EntityModel<DreadHumanoidRenderState> nativeModel;
    private final ModelBipedBase citadel;

    public ArmedCitadelEntityModel(ModelBipedBase citadel) {
        this(citadel, citadel.asEntityModel());
    }

    private ArmedCitadelEntityModel(ModelBipedBase citadel, EntityModel<DreadHumanoidRenderState> nativeModel) {
        super(nativeModel.root());
        this.citadel = citadel;
        this.nativeModel = nativeModel;
    }

    @Override
    public void setupAnim(DreadHumanoidRenderState state) {
        nativeModel.setupAnim(state);
    }

    @Override
    public void translateToHand(DreadHumanoidRenderState state, HumanoidArm arm, PoseStack poses) {
        citadel.translateToHand(state, arm, poses);
    }
}
