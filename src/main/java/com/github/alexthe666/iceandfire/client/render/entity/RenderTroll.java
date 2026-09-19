package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerTrollEyes;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerTrollWeapon;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class RenderTroll extends MobRenderer<EntityTroll, TrollRenderState, EntityModel<TrollRenderState>> {
    private final ModelTroll trollModel;

    public RenderTroll(EntityRendererProvider.Context context) {
        this(context, new ModelTroll());
    }

    private RenderTroll(EntityRendererProvider.Context context, ModelTroll model) {
        super(context, model.asEntityModel(), 0.9F);
        trollModel = model;
        addLayer(new LayerTrollWeapon(this));
        addLayer(new LayerTrollEyes(this));
    }

    public ModelTroll getTrollModel() {
        return trollModel;
    }

    @Override
    public TrollRenderState createRenderState() {
        return new TrollRenderState();
    }

    @Override
    public void extractRenderState(EntityTroll entity, TrollRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        extractTrollState(entity, state, partialTick);
    }

    public static void extractTrollState(EntityTroll entity, TrollRenderState state, float partialTick) {
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityTroll.ANIMATION_SPEAK ? TrollRenderState.AnimationKind.SPEAK
            : animation == EntityTroll.ANIMATION_ROAR ? TrollRenderState.AnimationKind.ROAR
            : animation == EntityTroll.ANIMATION_STRIKE_HORIZONTAL ? TrollRenderState.AnimationKind.STRIKE_HORIZONTAL
            : animation == EntityTroll.ANIMATION_STRIKE_VERTICAL ? TrollRenderState.AnimationKind.STRIKE_VERTICAL
            : TrollRenderState.AnimationKind.NONE;
        state.stone = EntityGorgon.isStoneMob(entity);
        state.stoneProgress = entity.stoneProgress;
        state.statue = false;
        // EnumTroll's Identifier fields are still legacy APIs. Map the actual
        // enum values to target-native assets, not casts or parsed legacy strings.
        state.variant = switch (entity.getTrollType()) {
            case FOREST -> TrollRenderState.Variant.FOREST;
            case FROST -> TrollRenderState.Variant.FROST;
            case MOUNTAIN -> TrollRenderState.Variant.MOUNTAIN;
        };
        var weapon = entity.getWeaponType();
        state.weapon = weapon == null ? null : switch (weapon) {
            case AXE -> TrollRenderState.Weapon.AXE;
            case COLUMN -> TrollRenderState.Weapon.COLUMN;
            case COLUMN_FOREST -> TrollRenderState.Weapon.COLUMN_FOREST;
            case COLUMN_FROST -> TrollRenderState.Weapon.COLUMN_FROST;
            case HAMMER -> TrollRenderState.Weapon.HAMMER;
            case TRUNK -> TrollRenderState.Weapon.TRUNK;
            case TRUNK_FROST -> TrollRenderState.Weapon.TRUNK_FROST;
        };
    }

    @Override
    public Identifier getTextureLocation(TrollRenderState state) {
        return state.bodyTexture();
    }
}
