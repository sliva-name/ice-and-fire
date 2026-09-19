package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.render.entity.SeaSerpentRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerSeaSerpentAncient;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderSeaSerpent extends MobRenderer<EntitySeaSerpent, SeaSerpentRenderState, EntityModel<SeaSerpentRenderState>> {

    public static final Identifier TEXTURE_BLUE = texture("blue");
    public static final Identifier TEXTURE_BLUE_BLINK = texture("blue_blink");
    public static final Identifier TEXTURE_BRONZE = texture("bronze");
    public static final Identifier TEXTURE_BRONZE_BLINK = texture("bronze_blink");
    public static final Identifier TEXTURE_DARKBLUE = texture("darkblue");
    public static final Identifier TEXTURE_DARKBLUE_BLINK = texture("darkblue_blink");
    public static final Identifier TEXTURE_GREEN = texture("green");
    public static final Identifier TEXTURE_GREEN_BLINK = texture("green_blink");
    public static final Identifier TEXTURE_PURPLE = texture("purple");
    public static final Identifier TEXTURE_PURPLE_BLINK = texture("purple_blink");
    public static final Identifier TEXTURE_RED = texture("red");
    public static final Identifier TEXTURE_RED_BLINK = texture("red_blink");
    public static final Identifier TEXTURE_TEAL = texture("teal");
    public static final Identifier TEXTURE_TEAL_BLINK = texture("teal_blink");

    public RenderSeaSerpent(EntityRendererProvider.Context context, TabulaModel<SeaSerpentRenderState> model) {
        super(context, model.asEntityModel(), 1.6F);
        this.addLayer(new LayerSeaSerpentAncient(this));
    }

    private static Identifier texture(String variant) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/seaserpent/seaserpent_" + variant + ".png");
    }

    @Override
    protected boolean affectedByCulling(EntitySeaSerpent entity) {
        return false;
    }

    @Override
    public SeaSerpentRenderState createRenderState() {
        return new SeaSerpentRenderState();
    }

    @Override
    public void extractRenderState(EntitySeaSerpent entity, SeaSerpentRenderState state, float partialTick) {
        // EntityRenderer extracts shadow geometry inside super, before living-state extraction.
        state.serpentScale = entity.getSeaSerpentScale();
        super.extractRenderState(entity, state, partialTick);
        // LivingEntityRenderer applies this once; the old scale hook would apply it twice.
        state.scale = state.serpentScale;
        var animation = entity.getAnimation();
        state.animation = animation == EntitySeaSerpent.ANIMATION_SPEAK ? AnimationKind.SPEAK
            : animation == EntitySeaSerpent.ANIMATION_BITE ? AnimationKind.BITE
            : animation == EntitySeaSerpent.ANIMATION_ROAR ? AnimationKind.ROAR : AnimationKind.NONE;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.swimCycle = entity.swimCycle;
        state.jumpProgress = entity.jumpProgress;
        state.wantJumpProgress = entity.wantJumpProgress;
        state.breathProgress = entity.breathProgress;
        state.hasJumpRotation = entity.jumpRot > 0.0F;
        state.jumpRotation = entity.prevJumpRot + (entity.jumpRot - entity.prevJumpRot) * partialTick;
        state.verticalVelocity = (float) entity.getDeltaMovement().y;
        state.tailBodyYaw = entity.yBodyRotO + (entity.yBodyRot - entity.yBodyRotO) * partialTick;
        for (int i = 0; i < state.pieceYaw.length; i++) {
            state.pieceYaw[i] = entity.getPieceYaw(i + 1, partialTick);
            state.piecePitch[i] = entity.getPiecePitch(i + 1, partialTick);
        }
        state.jumpingOutOfWater = entity.isJumpingOutOfWater();
        state.inWater = entity.isInWater();
        state.variant = entity.getVariant();
        state.blinking = entity.isBlinking();
        state.ancient = entity.isAncient();
    }

    @Override
    protected float getShadowRadius(SeaSerpentRenderState state) {
        return state.serpentScale;
    }

    @Override
    public Identifier getTextureLocation(SeaSerpentRenderState state) {
        return switch (state.variant) {
            case 0 -> state.blinking ? TEXTURE_BLUE_BLINK : TEXTURE_BLUE;
            case 1 -> state.blinking ? TEXTURE_BRONZE_BLINK : TEXTURE_BRONZE;
            case 2 -> state.blinking ? TEXTURE_DARKBLUE_BLINK : TEXTURE_DARKBLUE;
            case 3 -> state.blinking ? TEXTURE_GREEN_BLINK : TEXTURE_GREEN;
            case 4 -> state.blinking ? TEXTURE_PURPLE_BLINK : TEXTURE_PURPLE;
            case 5 -> state.blinking ? TEXTURE_RED_BLINK : TEXTURE_RED;
            case 6 -> state.blinking ? TEXTURE_TEAL_BLINK : TEXTURE_TEAL;
            default -> TEXTURE_BLUE;
        };
    }
}
