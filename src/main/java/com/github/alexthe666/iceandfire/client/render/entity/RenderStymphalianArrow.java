package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.entity.EntityStymphalianArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class RenderStymphalianArrow extends ArrowRenderer<EntityStymphalianArrow, ArrowRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/stymphalian_arrow.png");

    public RenderStymphalianArrow(EntityRendererProvider.Context context) {
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