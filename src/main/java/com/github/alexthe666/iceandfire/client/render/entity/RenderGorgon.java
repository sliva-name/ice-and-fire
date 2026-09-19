package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelGorgon;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGorgonEyes;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderGorgon extends MobRenderer<EntityGorgon, GorgonRenderState, EntityModel<GorgonRenderState>> {

    public static final Identifier PASSIVE_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/gorgon_passive.png");
    public static final Identifier AGRESSIVE_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/gorgon_active.png");
    public static final Identifier DEAD_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/gorgon_decapitated.png");

    public RenderGorgon(EntityRendererProvider.Context context) {
        super(context, new ModelGorgon().asEntityModel(), 0.4F);
        this.addLayer(new LayerGorgonEyes(this));
    }

    @Override
    public GorgonRenderState createRenderState() {
        return new GorgonRenderState();
    }

    @Override
    public void extractRenderState(EntityGorgon entity, GorgonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.animation = entity.getAnimation();
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.scaring = state.animation == EntityGorgon.ANIMATION_SCARE;
        state.hitting = state.animation == EntityGorgon.ANIMATION_HIT;
        state.deathProgress = Math.min(40, (float) entity.deathTime) / 2;
    }

    @Override
    public void scale(GorgonRenderState state, PoseStack stack) {
        stack.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public Identifier getTextureLocation(GorgonRenderState state) {
        if (state.scaring) {
            return AGRESSIVE_TEXTURE;
        } else if (state.deathProgress > 0) {
            return DEAD_TEXTURE;
        } else {
            return PASSIVE_TEXTURE;
        }
    }
}
