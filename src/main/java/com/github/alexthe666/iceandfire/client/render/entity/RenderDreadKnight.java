package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.client.model.ArmedCitadelEntityModel;
import com.github.alexthe666.iceandfire.client.model.ModelDreadKnight;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDreadItemInHand;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadKnight;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class RenderDreadKnight extends MobRenderer<EntityDreadKnight, DreadHumanoidRenderState, ArmedCitadelEntityModel> {
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_knight_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_knight_1.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_knight_2.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_knight_3.png");

    public RenderDreadKnight(EntityRendererProvider.Context context) {
        super(context, new ModelDreadKnight(0.0F).asArmedEntityModel(), 0.6F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
        this.addLayer(new LayerDreadItemInHand<>(this));
    }

    @Override
    public DreadHumanoidRenderState createRenderState() {
        return new DreadHumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadKnight entity, DreadHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTick, this.itemModelResolver);
        state.tickCount = entity.tickCount;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        state.animation = entity.getAnimation() == EntityDreadKnight.ANIMATION_SPAWN
            ? DreadHumanoidRenderState.SPAWN : IAnimatedEntity.NO_ANIMATION;
        state.armorVariant = entity.getArmorVariant();
        state.texture = switch (state.armorVariant) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
        state.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        state.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        if (entity.getMainHandItem().is(Items.BOW) && entity.swinging) {
            if (entity.getMainArm() == HumanoidArm.RIGHT) {
                state.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else {
                state.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        }
    }

    @Override
    protected void scale(DreadHumanoidRenderState state, PoseStack matrixStackIn) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadHumanoidRenderState state) {
        return state.texture;
    }
}
