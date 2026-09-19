package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelCyclops;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderCyclops extends MobRenderer<EntityCyclops, CyclopsRenderState, EntityModel<CyclopsRenderState>> {
    public static final Identifier TEXTURE_0 = CyclopsRenderState.Variant.ZERO.texture;
    public static final Identifier BLINK_0_TEXTURE = CyclopsRenderState.Variant.ZERO.blinkTexture;
    public static final Identifier BLINDED_0_TEXTURE = CyclopsRenderState.Variant.ZERO.blindedTexture;
    public static final Identifier TEXTURE_1 = CyclopsRenderState.Variant.ONE.texture;
    public static final Identifier BLINK_1_TEXTURE = CyclopsRenderState.Variant.ONE.blinkTexture;
    public static final Identifier BLINDED_1_TEXTURE = CyclopsRenderState.Variant.ONE.blindedTexture;
    public static final Identifier TEXTURE_2 = CyclopsRenderState.Variant.TWO.texture;
    public static final Identifier BLINK_2_TEXTURE = CyclopsRenderState.Variant.TWO.blinkTexture;
    public static final Identifier BLINDED_2_TEXTURE = CyclopsRenderState.Variant.TWO.blindedTexture;
    public static final Identifier TEXTURE_3 = CyclopsRenderState.Variant.THREE.texture;
    public static final Identifier BLINK_3_TEXTURE = CyclopsRenderState.Variant.THREE.blinkTexture;
    public static final Identifier BLINDED_3_TEXTURE = CyclopsRenderState.Variant.THREE.blindedTexture;

    public RenderCyclops(EntityRendererProvider.Context context) {
        super(context, new ModelCyclops().asEntityModel(), 1.6F);
    }

    @Override
    public CyclopsRenderState createRenderState() {
        return new CyclopsRenderState();
    }

    @Override
    public void extractRenderState(EntityCyclops entity, CyclopsRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        // The legacy entity initializes these static tokens in its constructor.
        // Only extraction compares them; the model uses independent enum tokens.
        state.animation = animation == EntityCyclops.ANIMATION_STOMP ? CyclopsRenderState.AnimationKind.STOMP
            : animation == EntityCyclops.ANIMATION_KICK ? CyclopsRenderState.AnimationKind.KICK
            : animation == EntityCyclops.ANIMATION_EATPLAYER ? CyclopsRenderState.AnimationKind.EATPLAYER
            : animation == EntityCyclops.ANIMATION_ROAR ? CyclopsRenderState.AnimationKind.ROAR
            : CyclopsRenderState.AnimationKind.NONE;
        // Explicit integer save-data mapping; unknown IDs use the normal texture 0,
        // even when blinded/blinking. Blinded takes precedence for known variants.
        int variant = entity.getVariant();
        state.variant = switch (variant) {
            case 1 -> CyclopsRenderState.Variant.ONE;
            case 2 -> CyclopsRenderState.Variant.TWO;
            case 3 -> CyclopsRenderState.Variant.THREE;
            default -> CyclopsRenderState.Variant.ZERO;
        };
        state.blinded = variant >= 0 && variant <= 3 && entity.isBlinded();
        state.blinking = variant >= 0 && variant <= 3 && entity.isBlinking();
    }

    @Override
    protected void scale(CyclopsRenderState state, PoseStack poses) {
        poses.scale(2.25F, 2.25F, 2.25F);
    }

    @Override
    public Identifier getTextureLocation(CyclopsRenderState state) {
        return state.bodyTexture();
    }
}
