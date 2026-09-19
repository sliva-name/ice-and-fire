package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;

public class RenderHippocampus extends MobRenderer<EntityHippocampus, HippocampusRenderState, EntityModel<HippocampusRenderState>> {
    private static final RenderPipeline NO_OUTLINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
        .withLocation(Identifier.fromNamespaceAndPath("iceandfire", "pipeline/hippocampus_no_outline"))
        .withShaderDefine("ALPHA_CUTOUT", 0.1F).withShaderDefine("PER_FACE_LIGHTING")
        .withSampler("Sampler1").withCull(false)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
        .build();

    private static RenderType noOutline(Identifier texture) {
        // Legacy entityNoOutline writes color, not depth, and never contributes an outline.
        return RenderType.create("hippocampus_no_outline", RenderSetup.builder(NO_OUTLINE)
            .withTexture("Sampler0", texture).useLightmap().useOverlay().sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE).createRenderSetup());
    }

    private static final Identifier[] TEXTURES = new Identifier[6];
    private static final Identifier[] BLINK_TEXTURES = new Identifier[6];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = texture("hippocampus_" + i);
            BLINK_TEXTURES[i] = texture("hippocampus_" + i + "_blinking");
        }
    }

    public RenderHippocampus(EntityRendererProvider.Context context) {
        super(context, new ModelHippocampus().asEntityModel(), 0.8F);
        addLayer(new LayerHippocampusRainbow(this));
        addLayer(new LayerHippocampusSaddle(this));
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hippocampus/" + name + ".png");
    }

    @Override
    public HippocampusRenderState createRenderState() {
        return new HippocampusRenderState();
    }

    @Override
    public void extractRenderState(EntityHippocampus entity, HippocampusRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.animation = entity.getAnimation() == EntityHippocampus.ANIMATION_SPEAK ? HippocampusRenderState.SPEAK : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.onLandProgress = entity.onLandProgress;
        state.sitProgress = entity.sitProgress;
        state.onGround = entity.onGround();
        state.inWater = entity.isInWater();
        state.tailYaw = entity.tail_buffer == null ? 0 : entity.tail_buffer.sampleYaw(partialTick);
        state.variant = entity.getVariant();
        state.blinking = entity.isBlinking();
        state.saddled = entity.isSaddled();
        state.bridled = state.saddled && entity.getControllingPassenger() != null;
        state.chested = entity.isChested();
        state.armor = entity.getArmor();
        state.rainbow = entity.hasCustomName() && entity.getCustomName() != null
            && entity.getCustomName().getString().toLowerCase().contains("rainbow");
        state.rainbowColor = -1;
        if (state.rainbow) {
            int i = entity.tickCount / 25 + entity.getId();
            int count = DyeColor.values().length;
            DyeColor first = DyeColor.byId(i % count);
            DyeColor second = DyeColor.byId((i + 1) % count);
            float f = ((float) (entity.tickCount % 25) + partialTick) / 25.0F;
            state.rainbowColor = ARGB.colorFromFloat(1,
                sheepChannel(first, 16) * (1 - f) + sheepChannel(second, 16) * f,
                sheepChannel(first, 8) * (1 - f) + sheepChannel(second, 8) * f,
                sheepChannel(first, 0) * (1 - f) + sheepChannel(second, 0) * f);
        }
    }

    // Preserve the old Sheep.getColorArray float math, without the new color lerper's rounding.
    private static float sheepChannel(DyeColor color, int shift) {
        return color == DyeColor.WHITE ? 0.9F : ((color.getTextureDiffuseColor() >> shift) & 255) / 255.0F * 0.75F;
    }

    @Override
    public Identifier getTextureLocation(HippocampusRenderState state) {
        int variant = state.variant >= 0 && state.variant < TEXTURES.length ? state.variant : 0;
        return state.blinking ? BLINK_TEXTURES[variant] : TEXTURES[variant];
    }

    private static class LayerHippocampusSaddle extends RenderLayer<HippocampusRenderState, EntityModel<HippocampusRenderState>> {
        private static final RenderType SADDLE = noOutline(texture("saddle"));
        private static final RenderType BRIDLE = noOutline(texture("bridle"));
        private static final RenderType CHEST = RenderTypes.entityTranslucent(texture("chest"));
        private static final RenderType IRON = RenderTypes.entityCutoutCull(texture("armor_iron"));
        private static final RenderType GOLD = RenderTypes.entityCutoutCull(texture("armor_gold"));
        private static final RenderType DIAMOND = RenderTypes.entityCutoutCull(texture("armor_diamond"));

        LayerHippocampusSaddle(RenderHippocampus renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poses, SubmitNodeCollector collector, int light, HippocampusRenderState state, float yRot, float xRot) {
            if (state.saddled) submitPart(poses, collector, light, state, SADDLE);
            if (state.bridled) submitPart(poses, collector, light, state, BRIDLE);
            if (state.chested) submitPart(poses, collector, light, state, CHEST);
            RenderType armor = switch (state.armor) {
                case 1 -> IRON;
                case 2 -> GOLD;
                case 3 -> DIAMOND;
                default -> null;
            };
            if (armor != null) submitPart(poses, collector, light, state, armor);
        }

        private void submitPart(PoseStack poses, SubmitNodeCollector collector, int light, HippocampusRenderState state, RenderType type) {
            collector.order(2).submitModel(getParentModel(), state, poses, type, light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }

    private static class LayerHippocampusRainbow extends RenderLayer<HippocampusRenderState, EntityModel<HippocampusRenderState>> {
        private static final RenderType TEXTURE = noOutline(texture("rainbow"));
        private static final RenderType BLINK = noOutline(texture("rainbow_blink"));

        LayerHippocampusRainbow(RenderHippocampus renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poses, SubmitNodeCollector collector, int light, HippocampusRenderState state, float yRot, float xRot) {
            if (state.rainbow) {
                collector.order(1).submitModel(getParentModel(), state, poses, state.blinking ? BLINK : TEXTURE,
                    light, LivingEntityRenderer.getOverlayCoords(state, 0), state.rainbowColor, null, state.outlineColor, null);
            }
        }
    }
}
