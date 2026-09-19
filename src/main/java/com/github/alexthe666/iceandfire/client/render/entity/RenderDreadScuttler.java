package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDreadScuttler;
import com.github.alexthe666.iceandfire.client.render.entity.DreadScuttlerRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadScuttler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderDreadScuttler extends MobRenderer<EntityDreadScuttler, DreadScuttlerRenderState, EntityModel<DreadScuttlerRenderState>> {

    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_scuttler_eyes.png");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_scuttler.png");

    public RenderDreadScuttler(EntityRendererProvider.Context context) {
        super(context, new ModelDreadScuttler().asEntityModel(), 0.75F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public DreadScuttlerRenderState createRenderState() {
        return new DreadScuttlerRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadScuttler entity, DreadScuttlerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        float size = entity.getSize();
        state.scale = size < 0.01F ? 1F : size;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDreadScuttler.ANIMATION_BITE ? AnimationKind.BITE
            : animation == EntityDreadScuttler.ANIMATION_SPAWN ? AnimationKind.SPAWN : AnimationKind.NONE;
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadScuttlerRenderState state) {
        return TEXTURE;
    }
}
