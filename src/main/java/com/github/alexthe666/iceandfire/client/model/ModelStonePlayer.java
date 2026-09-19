package com.github.alexthe666.iceandfire.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class ModelStonePlayer extends HumanoidModel<HumanoidRenderState> {

    public ModelStonePlayer(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(HumanoidRenderState state) {
        // The 1.18 override discarded living pose so player statues stay at the baked rest.
    }
}
