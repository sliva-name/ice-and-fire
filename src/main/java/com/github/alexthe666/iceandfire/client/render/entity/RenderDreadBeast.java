package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDreadBeast;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadBeast;
import com.github.alexthe666.iceandfire.client.render.entity.DreadBeastRenderState.AnimationKind;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderDreadBeast extends MobRenderer<EntityDreadBeast, DreadBeastRenderState, EntityModel<DreadBeastRenderState>> {

    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_beast_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_beast_1.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_beast_2.png");

    public RenderDreadBeast(EntityRendererProvider.Context context) {
        super(context, new ModelDreadBeast().asEntityModel(), 0.5F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public DreadBeastRenderState createRenderState() {
        return new DreadBeastRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadBeast entity, DreadBeastRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        float size = entity.getSize();
        state.scale = size < 0.01F ? 1F : size;
        state.variant = entity.getVariant();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDreadBeast.ANIMATION_BITE ? AnimationKind.BITE
            : animation == EntityDreadBeast.ANIMATION_SPAWN ? AnimationKind.SPAWN : AnimationKind.NONE;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadBeastRenderState state) {
        return state.variant == 1 ? TEXTURE_1 : TEXTURE_0;

    }

}
