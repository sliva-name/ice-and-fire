package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerHydraEyes;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerHydraHead;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderHydra extends MobRenderer<EntityHydra, HydraRenderState, EntityModel<HydraRenderState>> {

    public static final Identifier TEXUTURE_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hydra/hydra_0.png");
    public static final Identifier TEXUTURE_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hydra/hydra_1.png");
    public static final Identifier TEXUTURE_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hydra/hydra_2.png");
    public static final Identifier TEXUTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/hydra/hydra_eyes.png");
    private final ModelHydraBody hydraModel;

    public RenderHydra(EntityRendererProvider.Context context) {
        this(context, new ModelHydraBody());
    }

    private RenderHydra(EntityRendererProvider.Context context, ModelHydraBody model) {
        super(context, model.asEntityModel(), 1.2F);
        this.hydraModel = model;
        this.addLayer(new LayerHydraHead(this));
        this.addLayer(new LayerHydraEyes(this));
    }

    /** The Citadel body behind the native adapter, used for head attachments and statue rendering. */
    public ModelHydraBody getHydraModel() {
        return hydraModel;
    }

    @Override
    public HydraRenderState createRenderState() {
        return new HydraRenderState();
    }

    @Override
    public void extractRenderState(EntityHydra entity, HydraRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        extractHydraState(entity, state, partialTick);
    }

    /** Also used by the immediate-mode statue bridge, which supplies its own living pose inputs. */
    public static void extractHydraState(EntityHydra entity, HydraRenderState state, float partialTick) {
        state.animation = entity.getAnimation();
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.variant = entity.getVariant();
        state.headCount = Mth.clamp(entity.getHeadCount(), 1, HydraRenderState.MAX_HEADS);
        state.severedHead = entity.getSeveredHead();
        state.alive = entity.isAlive();
        state.stone = EntityGorgon.isStoneMob(entity);
        for (int i = 0; i < HydraRenderState.MAX_HEADS; i++) {
            state.strikingProgress[i] = Mth.lerp(partialTick, entity.prevStrikeProgress[i], entity.strikingProgress[i]);
            state.breathProgress[i] = Mth.lerp(partialTick, entity.prevBreathProgress[i], entity.breathProgress[i]);
            state.speakingProgress[i] = Mth.lerp(partialTick, entity.prevSpeakingProgress[i], entity.speakingProgress[i]);
        }
    }

    @Override
    public void scale(HydraRenderState state, PoseStack stack) {
        stack.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public Identifier getTextureLocation(HydraRenderState state) {
        return LayerHydraHead.getHeadTexture(state);
    }
}
