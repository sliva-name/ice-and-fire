package com.github.alexthe666.iceandfire.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

public class RenderNothing<T extends Entity, S extends EntityRenderState> extends EntityRenderer<T, S> {
    public RenderNothing(EntityRendererProvider.Context context) {
        super(context);
    }

    /** Subclasses using a specialized state must override this factory. */
    @SuppressWarnings("unchecked")
    @Override
    public S createRenderState() {
        return (S) new EntityRenderState();
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
    }

    @Override
    public void submit(S state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
    }
}
