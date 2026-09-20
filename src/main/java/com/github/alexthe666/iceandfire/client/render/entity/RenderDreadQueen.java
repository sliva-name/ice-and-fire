package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.client.model.ArmedCitadelEntityModel;
import com.github.alexthe666.iceandfire.client.model.ModelDreadQueen;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDreadItemInHand;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderDreadQueen extends MobRenderer<EntityDreadQueen, DreadHumanoidRenderState, ArmedCitadelEntityModel> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_queen.png");
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_queen_eyes.png");

    public RenderDreadQueen(EntityRendererProvider.Context context) {
        super(context, new ModelDreadQueen(0.0F).asArmedEntityModel(), 0.6F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
        this.addLayer(new LayerDreadItemInHand<>(this));
    }

    @Override
    public DreadHumanoidRenderState createRenderState() {
        return new DreadHumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadQueen entity, DreadHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTick, this.itemModelResolver);
        state.tickCount = entity.tickCount;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDreadQueen.ANIMATION_SUMMON ? DreadHumanoidRenderState.SUMMON
            : animation == EntityDreadQueen.ANIMATION_SPAWN ? DreadHumanoidRenderState.SPAWN
            : IAnimatedEntity.NO_ANIMATION;
        state.texture = TEXTURE;
        state.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        state.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        state.hideHeldItems = state.animation == DreadHumanoidRenderState.SPAWN
            && state.animationTick <= DreadHumanoidRenderState.SPAWN.getDuration() - 10;
    }

    @Override
    protected void scale(DreadHumanoidRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadHumanoidRenderState state) {
        return state.texture == null ? TEXTURE : state.texture;
    }
}
