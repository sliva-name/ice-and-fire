package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelStymphalianBird;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderStymphalianBird extends MobRenderer<EntityStymphalianBird, StymphalianBirdRenderState, EntityModel<StymphalianBirdRenderState>> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/stymphalianbird/stymphalian_bird.png");

    public RenderStymphalianBird(EntityRendererProvider.Context context) {
        super(context, new ModelStymphalianBird().asEntityModel(), 0.6F);
    }

    @Override
    public StymphalianBirdRenderState createRenderState() {
        return new StymphalianBirdRenderState();
    }

    @Override
    public void extractRenderState(EntityStymphalianBird entity, StymphalianBirdRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var animation = entity.getAnimation();
        state.animation = animation == EntityStymphalianBird.ANIMATION_PECK ? StymphalianBirdRenderState.PECK
            : animation == EntityStymphalianBird.ANIMATION_SPEAK ? StymphalianBirdRenderState.SPEAK
            : animation == EntityStymphalianBird.ANIMATION_SHOOT_ARROWS ? StymphalianBirdRenderState.SHOOT_ARROWS : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.flyProgress = entity.flyProgress;
    }

    @Override
    public void scale(StymphalianBirdRenderState state, PoseStack poses) {
        poses.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    public Identifier getTextureLocation(StymphalianBirdRenderState state) {
        return TEXTURE;
    }
}
