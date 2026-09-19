package com.github.alexthe666.iceandfire.client.render.entity;


import com.github.alexthe666.iceandfire.client.model.ModelAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderAmphithere extends MobRenderer<EntityAmphithere, AmphithereRenderState, EntityModel<AmphithereRenderState>> {
    public static final Identifier TEXTURE_BLUE = AmphithereRenderState.Variant.BLUE.texture;
    public static final Identifier TEXTURE_BLUE_BLINK = AmphithereRenderState.Variant.BLUE.blinkTexture;
    public static final Identifier TEXTURE_GREEN = AmphithereRenderState.Variant.GREEN.texture;
    public static final Identifier TEXTURE_GREEN_BLINK = AmphithereRenderState.Variant.GREEN.blinkTexture;
    public static final Identifier TEXTURE_OLIVE = AmphithereRenderState.Variant.OLIVE.texture;
    public static final Identifier TEXTURE_OLIVE_BLINK = AmphithereRenderState.Variant.OLIVE.blinkTexture;
    public static final Identifier TEXTURE_RED = AmphithereRenderState.Variant.RED.texture;
    public static final Identifier TEXTURE_RED_BLINK = AmphithereRenderState.Variant.RED.blinkTexture;
    public static final Identifier TEXTURE_YELLOW = AmphithereRenderState.Variant.YELLOW.texture;
    public static final Identifier TEXTURE_YELLOW_BLINK = AmphithereRenderState.Variant.YELLOW.blinkTexture;

    public RenderAmphithere(EntityRendererProvider.Context context) {
        super(context, new ModelAmphithere().asEntityModel(), 1.6F);
    }

    @Override
    public AmphithereRenderState createRenderState() {
        return new AmphithereRenderState();
    }

    @Override
    public void extractRenderState(EntityAmphithere entity, AmphithereRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityAmphithere.ANIMATION_BITE ? AmphithereRenderState.AnimationKind.BITE
            : animation == EntityAmphithere.ANIMATION_BITE_RIDER ? AmphithereRenderState.AnimationKind.BITE_RIDER
            : animation == EntityAmphithere.ANIMATION_WING_BLAST ? AmphithereRenderState.AnimationKind.WING_BLAST
            : animation == EntityAmphithere.ANIMATION_TAIL_WHIP ? AmphithereRenderState.AnimationKind.TAIL_WHIP
            : animation == EntityAmphithere.ANIMATION_SPEAK ? AmphithereRenderState.AnimationKind.SPEAK
            : AmphithereRenderState.AnimationKind.NONE;
        state.flapProgress = entity.flapProgress;
        state.groundProgress = entity.groundProgress;
        state.diveProgress = entity.diveProgress;
        state.sitProgress = entity.sitProgress;
        state.onGround = entity.onGround();
        state.captureBufferRotations(0, 0, 0, 0, 0, 0, partialTick);
        if (state.groundProgress <= 0 && state.animation != AmphithereRenderState.AnimationKind.WING_BLAST && !state.onGround) {
            // Legacy applyChainWaveBuffer reads yaw history even for pitch_buffer.
            state.captureBufferRotations(
                entity.roll_buffer.getPreviousYawVariation(), entity.roll_buffer.getYawVariation(),
                entity.pitch_buffer.getPreviousYawVariation(), entity.pitch_buffer.getYawVariation(),
                entity.tail_buffer.getPreviousYawVariation(), entity.tail_buffer.getYawVariation(), partialTick);
        }
        // These enums map integer save-data IDs, not ordinal() or a registry enum.
        // Unknown IDs historically use GREEN without the blink suffix.
        int variant = entity.getVariant();
        state.variant = switch (variant) {
            case 0 -> AmphithereRenderState.Variant.BLUE;
            case 2 -> AmphithereRenderState.Variant.OLIVE;
            case 3 -> AmphithereRenderState.Variant.RED;
            case 4 -> AmphithereRenderState.Variant.YELLOW;
            default -> AmphithereRenderState.Variant.GREEN;
        };
        state.blinking = variant >= 0 && variant <= 4 && entity.isBlinking();
    }

    @Override
    protected void scale(AmphithereRenderState state, PoseStack poses) {
        poses.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public Identifier getTextureLocation(AmphithereRenderState state) {
        return state.bodyTexture();
    }


}
