package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.client.model.ArmedCitadelEntityModel;
import com.github.alexthe666.iceandfire.client.model.ModelDreadLich;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDreadItemInHand;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadLich;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderDreadLich extends MobRenderer<EntityDreadLich, DreadHumanoidRenderState, ArmedCitadelEntityModel> {
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_0.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_1.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_2.png");
    public static final Identifier TEXTURE_3 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_3.png");
    public static final Identifier TEXTURE_4 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_4.png");

    public RenderDreadLich(EntityRendererProvider.Context context) {
        super(context, new ModelDreadLich(0.0F).asArmedEntityModel(), 0.6F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
        this.addLayer(new LayerDreadItemInHand<>(this));
    }

    @Override
    public DreadHumanoidRenderState createRenderState() {
        return new DreadHumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadLich entity, DreadHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTick, this.itemModelResolver);
        state.tickCount = entity.tickCount;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDreadLich.ANIMATION_SUMMON ? DreadHumanoidRenderState.SUMMON
            : animation == EntityDreadLich.ANIMATION_SPAWN ? DreadHumanoidRenderState.SPAWN
            : IAnimatedEntity.NO_ANIMATION;
        state.variant = entity.getVariant();
        state.texture = switch (state.variant) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            case 4 -> TEXTURE_4;
            default -> TEXTURE_0;
        };
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
    public Identifier getTextureLocation(DreadHumanoidRenderState state) {
        return state.texture;
    }
}
