package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.iceandfire.client.render.tile.RenderDreadPortal;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public final class IafRenderType {
    private static final Identifier STONE_TEXTURE = Identifier.withDefaultNamespace("textures/block/stone.png");
    private static final RenderPipeline GHOST = entityPipeline("ghost", BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA, false, CompareOp.LESS_THAN_OR_EQUAL);
    private static final RenderPipeline GHOST_DAY = entityPipeline("ghost_day", BlendFunction.TRANSLUCENT, false, CompareOp.LESS_THAN_OR_EQUAL);
    private static final RenderPipeline STONE_CRACK = entityPipeline("stone_crack", BlendFunction.TRANSLUCENT, false, CompareOp.EQUAL);
    private static final RenderPipeline ICE = RenderPipeline.builder(RenderPipelines.BEACON_BEAM_SNIPPET)
        .withLocation(id("pipeline/ice"))
        .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withCull(true)
        .build();

    // The custom shader assets still need the 26.1 uniform-block/PORTAL_LAYERS migration.
    // Keep the custom shader rather than substitute vanilla's differently colored portal.
    private static final RenderPipeline DREAD_PORTAL = RenderPipeline.builder(RenderPipelines.END_PORTAL_SNIPPET)
        .withLocation(id("pipeline/dread_portal"))
        .withVertexShader(id("core/rendertype_dread_portal"))
        .withFragmentShader(id("core/rendertype_dread_portal"))
        .withShaderDefine("PORTAL_LAYERS", 5)
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
        .build();
    private static final RenderType DREADLANDS_PORTAL = RenderType.create("dreadlands_portal", RenderSetup.builder(DREAD_PORTAL)
        .withTexture("Sampler0", RenderDreadPortal.DREAD_PORTAL_BACKGROUND)
        .withTexture("Sampler1", RenderDreadPortal.DREAD_PORTAL)
        .bufferSize(256)
        .createRenderSetup());
    private static final RenderType STONE = textured("stone_entity_type", RenderPipelines.ENTITY_CUTOUT_CULL, STONE_TEXTURE, true);

    private IafRenderType() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath("iceandfire", path);
    }

    private static RenderPipeline entityPipeline(String name, BlendFunction blend, boolean cull, CompareOp depthTest) {
        return RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(id("pipeline/" + name))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("PER_FACE_LIGHTING")
            .withSampler("Sampler1")
            .withColorTargetState(new ColorTargetState(blend))
            .withCull(cull)
            .withDepthStencilState(new DepthStencilState(depthTest, true))
            .build();
    }

    private static RenderType textured(String name, RenderPipeline pipeline, Identifier texture, boolean outline) {
        return RenderType.create(name, RenderSetup.builder(pipeline)
            .withTexture("Sampler0", texture)
            .useLightmap()
            .useOverlay()
            .sortOnUpload()
            .bufferSize(256)
            .setOutline(outline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
            .createRenderSetup());
    }

    public static RenderType getGhost(Identifier locationIn) {
        return textured("ghost_iaf", GHOST, locationIn, true);
    }

    public static RenderType getGhostDaytime(Identifier locationIn) {
        return textured("ghost_iaf_day", GHOST_DAY, locationIn, true);
    }

    public static RenderType getDreadlandsPortal() {
        return DREADLANDS_PORTAL;
    }

    public static RenderType getStoneMobRenderType(float x, float y) {
        return STONE;
    }

    public static RenderType getIce(Identifier locationIn) {
        return textured("ice_texture", ICE, locationIn, true);
    }

    public static RenderType getStoneCrackRenderType(Identifier crackTex) {
        return textured("stone_entity_type_crack", STONE_CRACK, crackTex, false);
    }
}
