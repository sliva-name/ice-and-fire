package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.entity.EntityDragonArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public class RenderDragonArrow extends ArrowRenderer<EntityDragonArrow, ArrowRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/dragonbone_arrow.png");

    public RenderDragonArrow(EntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    public @NotNull ArrowRenderState createRenderState() {
            return new ArrowRenderState();
        }

        @Override
        public @NotNull Identifier getTextureLocation(@NotNull ArrowRenderState state) {
        return TEXTURE;
    }
}