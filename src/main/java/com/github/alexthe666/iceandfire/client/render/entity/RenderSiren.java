package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelSiren;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderSiren extends MobRenderer<EntitySiren, SirenRenderState, EntityModel<SirenRenderState>> {
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_0.png");
    public static final Identifier TEXTURE_0_AGGRESSIVE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_0_aggressive.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_1.png");
    public static final Identifier TEXTURE_1_AGGRESSIVE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_1_aggressive.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_2.png");
    public static final Identifier TEXTURE_2_AGGRESSIVE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/siren/siren_2_aggressive.png");

    public RenderSiren(EntityRendererProvider.Context context) {
        super(context, new ModelSiren().asEntityModel(), 0.8F);
    }

    @Override
    public SirenRenderState createRenderState() {
        return new SirenRenderState();
    }

    @Override
    public void extractRenderState(EntitySiren entity, SirenRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var animation = entity.getAnimation();
        state.animation = animation == EntitySiren.ANIMATION_BITE ? SirenRenderState.BITE
            : animation == EntitySiren.ANIMATION_PULL ? SirenRenderState.PULL : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.swimProgress = entity.swimProgress;
        state.singProgress = entity.singProgress;
        state.swimming = entity.isSwimming();
        state.singing = entity.isSinging();
        state.singingPose = entity.getSingingPose();
        state.onGround = entity.onGround();
        state.tailYaw = entity.tail_buffer == null ? 0 : entity.tail_buffer.sampleYaw(partialTick);
        state.hairColor = entity.getHairColor();
        state.aggressive = entity.isAgressive();
    }

    @Override
    public void scale(SirenRenderState state, PoseStack poses) {
        poses.translate(0, 0, -0.5F);
    }

    @Override
    public Identifier getTextureLocation(SirenRenderState state) {
        return switch (state.hairColor) {
            case 1 -> state.aggressive ? TEXTURE_1_AGGRESSIVE : TEXTURE_1;
            case 2 -> state.aggressive ? TEXTURE_2_AGGRESSIVE : TEXTURE_2;
            default -> state.aggressive ? TEXTURE_0_AGGRESSIVE : TEXTURE_0;
        };
    }

    public static Identifier getSirenOverlayTexture(int siren) {
        return switch (siren) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }
}
