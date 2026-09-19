package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Locale;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RenderHippogryph extends MobRenderer<EntityHippogryph, HippogryphRenderState, EntityModel<HippogryphRenderState>> {
    private static final RenderPipeline NO_OUTLINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
        .withLocation(Identifier.fromNamespaceAndPath("iceandfire", "pipeline/hippogryph_no_outline"))
        .withShaderDefine("ALPHA_CUTOUT", 0.1F).withShaderDefine("PER_FACE_LIGHTING").withCull(false)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
        .build();

    private static RenderType noOutline(Identifier texture) {
        // Legacy entityNoOutline writes color, not depth, and never contributes an outline.
        return RenderType.create("hippogryph_no_outline", RenderSetup.builder(NO_OUTLINE)
            .withTexture("Sampler0", texture).useLightmap().useOverlay().sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE).createRenderSetup());
    }

    public RenderHippogryph(EntityRendererProvider.Context context) {
        super(context, new ModelHippogryph().asEntityModel(), 0.8F);
        addLayer(new LayerHippogriffSaddle(this));
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hippogryph/" + name + ".png");
    }

    @Override
    public HippogryphRenderState createRenderState() {
        return new HippogryphRenderState();
    }

    @Override
    public void extractRenderState(EntityHippogryph entity, HippogryphRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var animation = entity.getAnimation();
        state.animation = animation == EntityHippogryph.ANIMATION_SPEAK ? HippogryphRenderState.SPEAK
            : animation == EntityHippogryph.ANIMATION_EAT ? HippogryphRenderState.EAT
            : animation == EntityHippogryph.ANIMATION_BITE ? HippogryphRenderState.BITE
            : animation == EntityHippogryph.ANIMATION_SCRATCH ? HippogryphRenderState.SCRATCH : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.sitProgress = entity.sitProgress;
        state.hoverProgress = entity.hoverProgress;
        state.flyProgress = entity.flyProgress;
        state.flying = entity.isFlying();
        state.hovering = entity.isHovering();
        state.airBorneCounter = entity.airBorneCounter;
        var variant = entity.getEnumVariant();
        state.dodo = variant == EnumHippogryphTypes.DODO;
        state.texture = texture(variant.name().toLowerCase(Locale.ROOT) + (entity.isBlinking() ? "_blink" : ""));
        state.saddled = entity.isSaddled();
        state.bridled = state.saddled && entity.getControllingPassenger() != null;
        state.chested = entity.isChested();
        state.armor = entity.getArmor();
    }

    @Override
    protected void scale(HippogryphRenderState state, PoseStack poses) {
        poses.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTextureLocation(HippogryphRenderState state) {
        return state.texture;
    }

    private static class LayerHippogriffSaddle extends RenderLayer<HippogryphRenderState, EntityModel<HippogryphRenderState>> {
        private static final RenderType SADDLE = noOutline(texture("saddle"));
        private static final RenderType BRIDLE = noOutline(texture("bridle"));
        private static final RenderType CHEST = RenderTypes.entityTranslucent(texture("chest"));
        private static final RenderType IRON = noOutline(texture("armor_iron"));
        private static final RenderType GOLD = noOutline(texture("armor_gold"));
        private static final RenderType DIAMOND = noOutline(texture("armor_diamond"));

        LayerHippogriffSaddle(RenderHippogryph renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poses, SubmitNodeCollector collector, int light, HippogryphRenderState state, float yRot, float xRot) {
            RenderType armor = switch (state.armor) {
                case 1 -> IRON;
                case 2 -> GOLD;
                case 3 -> DIAMOND;
                default -> null;
            };
            if (armor != null) submitPart(poses, collector, light, state, armor);
            if (state.saddled) submitPart(poses, collector, light, state, SADDLE);
            if (state.bridled) submitPart(poses, collector, light, state, BRIDLE);
            if (state.chested) submitPart(poses, collector, light, state, CHEST);
        }

        private void submitPart(PoseStack poses, SubmitNodeCollector collector, int light, HippogryphRenderState state, RenderType type) {
            collector.order(1).submitModel(getParentModel(), state, poses, type, light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
