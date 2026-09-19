package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.client.model.ModelDreadGhoul;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadGhoul;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderDreadGhoul extends MobRenderer<EntityDreadGhoul, DreadHumanoidRenderState, EntityModel<DreadHumanoidRenderState>> {

    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_closed_1.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_closed_2.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_closed_3.png");
    public static final Identifier TEXTURE_0_MID = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_mid_1.png");
    public static final Identifier TEXTURE_1_MID = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_mid_2.png");
    public static final Identifier TEXTURE_2_MID = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_mid_3.png");
    public static final Identifier TEXTURE_0_OPEN = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_open_1.png");
    public static final Identifier TEXTURE_1_OPEN = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_open_2.png");
    public static final Identifier TEXTURE_2_OPEN = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_ghoul_open_3.png");

    public RenderDreadGhoul(EntityRendererProvider.Context context) {
        super(context, new ModelDreadGhoul(0.0F).asEntityModel(), 0.5F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public DreadHumanoidRenderState createRenderState() {
        return new DreadHumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadGhoul entity, DreadHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTick, this.itemModelResolver);
        float size = entity.getSize();
        state.scale = size < 0.01F ? 1F : size;
        state.tickCount = entity.tickCount;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDreadGhoul.ANIMATION_SLASH ? DreadHumanoidRenderState.SLASH
            : animation == EntityDreadGhoul.ANIMATION_SPAWN ? DreadHumanoidRenderState.SPAWN
            : IAnimatedEntity.NO_ANIMATION;
        state.variant = entity.getVariant();
        state.screamStage = entity.getScreamStage();
        state.texture = texture(state.screamStage, state.variant);
    }

    private static Identifier texture(int screamStage, int variant) {
        if (screamStage == 2) {
            return switch (variant) {
                case 1 -> TEXTURE_1_OPEN;
                case 2 -> TEXTURE_2_OPEN;
                default -> TEXTURE_0_OPEN;
            };
        }
        if (screamStage == 1) {
            return switch (variant) {
                case 1 -> TEXTURE_1_MID;
                case 2 -> TEXTURE_2_MID;
                default -> TEXTURE_0_MID;
            };
        }
        return switch (variant) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadHumanoidRenderState state) {
        return state.texture;
    }
}
