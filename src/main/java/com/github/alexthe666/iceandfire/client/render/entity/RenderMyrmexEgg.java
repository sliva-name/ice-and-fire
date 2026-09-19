package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;

public class RenderMyrmexEgg extends LivingEntityRenderer<EntityMyrmexEgg, EggRenderState, EntityModel<EggRenderState>> {

    public static final Identifier EGG_JUNGLE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/myrmex/myrmex_jungle_egg.png");
    public static final Identifier EGG_DESERT = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/myrmex/myrmex_desert_egg.png");

    public RenderMyrmexEgg(EntityRendererProvider.Context context) {
        super(context, new ModelDragonEgg().asEntityModel(), 0.3F);
    }

    @Override
    public EggRenderState createRenderState() {
        return new EggRenderState();
    }

    @Override
    public void extractRenderState(EntityMyrmexEgg entity, EggRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.texture = entity.isJungle() ? EGG_JUNGLE : EGG_DESERT;
        state.inverted = false;
        state.wobbleAmount = 0;
    }

    @Override
    protected boolean shouldShowName(EntityMyrmexEgg entity, double distanceToCameraSq) {
        return entity.shouldShowName() && entity.hasCustomName();
    }

    @Override
    public Identifier getTextureLocation(EggRenderState state) {
        return state.texture;
    }
}
