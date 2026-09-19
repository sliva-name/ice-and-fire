package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.render.entity.ChainTieRenderState;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

public class ModelChainTie extends AdvancedEntityModel<ChainTieRenderState> {
    public AdvancedModelBox knotRenderer;

    public ModelChainTie() {
        this(0, 0, 32, 32);
    }

    public ModelChainTie(int width, int height, int texWidth, int texHeight) {
        this.texWidth = this.textureWidth = texWidth;
        this.texHeight = this.textureHeight = texHeight;
        this.knotRenderer = new AdvancedModelBox(this, width, height);
        this.knotRenderer.addBox(-4.0F, 2.0F, -4.0F, 8, 12, 8, 1.0F);
        this.knotRenderer.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(@NotNull ChainTieRenderState state) {
        super.setupAnim(state);
        this.knotRenderer.rotateAngleY = state.yRot * 0.017453292F;
        this.knotRenderer.rotateAngleX = state.xRot * 0.017453292F;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(knotRenderer);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(knotRenderer);
    }
}
