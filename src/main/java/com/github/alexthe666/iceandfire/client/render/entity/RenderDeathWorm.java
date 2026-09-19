package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

public class RenderDeathWorm extends MobRenderer<EntityDeathWorm, DeathWormRenderState, EntityModel<DeathWormRenderState>> {
    public static final Identifier TEXTURE_RED = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/deathworm/deathworm_red.png");
    public static final Identifier TEXTURE_WHITE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/deathworm/deathworm_white.png");
    public static final Identifier TEXTURE_YELLOW = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/deathworm/deathworm_yellow.png");

    public RenderDeathWorm(EntityRendererProvider.Context context) {
        super(context, new ModelDeathWorm().asEntityModel(), 0);
    }

    @Override
    protected boolean affectedByCulling(EntityDeathWorm entity) {
        return false;
    }

    @Override
    public DeathWormRenderState createRenderState() {
        return new DeathWormRenderState();
    }

    @Override
    public void extractRenderState(EntityDeathWorm entity, DeathWormRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // LivingEntity.getScale() is final in 26.1 (Attributes.SCALE). Worm size is getAgeScale().
        state.scale = entity.getAgeScale();
        state.animation = entity.getAnimation() == EntityDeathWorm.ANIMATION_BITE ? DeathWormRenderState.BITE : null;
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.variant = entity.getVariant();
        state.jumpProgress = entity.prevJumpProgress + (entity.jumpProgress - entity.prevJumpProgress) * partialTick;
        state.jumping = entity.getWormJumping() > 0;
        state.tailYaw = entity.tail_buffer == null ? 0 : entity.tail_buffer.sampleYaw(partialTick);
    }

    @Override
    public boolean shouldRender(EntityDeathWorm entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) {
            return true;
        }
        if (!entity.shouldRender(x, y, z)) {
            return false;
        }
        Entity[] parts = entity.getWormParts();
        if (parts == null) {
            return false;
        }
        for (Entity part : parts) {
            if (part != null && frustum.isVisible(part.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected float getShadowRadius(DeathWormRenderState state) {
        return state.scale / 3;
    }

    @Override
    protected int getBlockLightLevel(EntityDeathWorm entity, BlockPos pos) {
        return entity.isOnFire() ? 15 : entity.getWormBrightness(false);
    }

    @Override
    protected int getSkyLightLevel(EntityDeathWorm entity, BlockPos pos) {
        return entity.getWormBrightness(true);
    }

    @Override
    public Identifier getTextureLocation(DeathWormRenderState state) {
        return state.variant == 2 ? TEXTURE_WHITE : state.variant == 1 ? TEXTURE_RED : TEXTURE_YELLOW;
    }
}
