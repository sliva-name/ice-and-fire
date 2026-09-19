package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelCockatrice;
import com.github.alexthe666.iceandfire.client.model.ModelCockatriceChick;
import com.github.alexthe666.iceandfire.client.particle.CockatriceBeamRender;
import com.github.alexthe666.iceandfire.client.render.entity.CockatriceRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderCockatrice extends MobRenderer<EntityCockatrice, CockatriceRenderState, EntityModel<CockatriceRenderState>> {

    public static final Identifier TEXTURE_ROOSTER = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cockatrice/cockatrice_0.png");
    public static final Identifier TEXTURE_HEN = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cockatrice/cockatrice_1.png");
    public static final Identifier TEXTURE_ROOSTER_CHICK = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cockatrice/cockatrice_0_chick.png");
    public static final Identifier TEXTURE_HEN_CHICK = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/cockatrice/cockatrice_1_chick.png");
    private final EntityModel<CockatriceRenderState> adultModel;
    private final EntityModel<CockatriceRenderState> babyModel = new ModelCockatriceChick().asEntityModel();

    public RenderCockatrice(EntityRendererProvider.Context context) {
        super(context, new ModelCockatrice().asEntityModel(), 0.6F);
        adultModel = model;
    }

    @Override
    public CockatriceRenderState createRenderState() {
        return new CockatriceRenderState();
    }

    @Override
    public void extractRenderState(EntityCockatrice entity, CockatriceRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityCockatrice.ANIMATION_JUMPAT ? AnimationKind.JUMPAT
            : animation == EntityCockatrice.ANIMATION_WATTLESHAKE ? AnimationKind.WATTLESHAKE
            : animation == EntityCockatrice.ANIMATION_BITE ? AnimationKind.BITE
            : animation == EntityCockatrice.ANIMATION_SPEAK ? AnimationKind.SPEAK
            : animation == EntityCockatrice.ANIMATION_EAT ? AnimationKind.EAT : AnimationKind.NONE;
        state.sitProgress = entity.sitProgress;
        state.stareProgress = entity.stareProgress;
        state.isBaby = entity.isBaby();
        state.hen = entity.isHen();
        state.hasTargetedEntity = entity.hasTargetedEntity();
        state.attackAnimationScale = entity.getAttackAnimationScale(partialTick);
        state.beamTime = (float) entity.level().getGameTime() + partialTick;
        state.tickCount = entity.tickCount;
        // Use the current eye height, as the original beam did, even in a sleeping pose.
        state.eyeHeight = entity.getEyeHeight();

        LivingEntity target = entity.getTargetedEntity();
        state.blinded = entity.hasEffect(MobEffects.BLINDNESS)
            || target != null && target.hasEffect(MobEffects.BLINDNESS);
        state.mutuallyLooking = !state.blinded && target != null
            && EntityGorgon.isEntityLookingAt(entity, target, EntityCockatrice.VIEW_RADIUS)
            && EntityGorgon.isEntityLookingAt(target, entity, EntityCockatrice.VIEW_RADIUS);
        state.beamTarget = null;
        if (target != null) {
            // Snapshot only beam inputs, avoiding recursive extraction when two mobs target each other.
            EntityRenderState targetState = new EntityRenderState();
            Vec3 position = getPosition(target, 0.0D, partialTick);
            targetState.x = position.x;
            targetState.y = position.y;
            targetState.z = position.z;
            targetState.boundingBoxHeight = target.getBbHeight();
            state.beamTarget = targetState;
        }
    }

    private static Vec3 getPosition(LivingEntity entity, double yOffset, float partialTick) {
        double x = entity.xOld + (entity.getX() - entity.xOld) * (double) partialTick;
        double y = yOffset + entity.yOld + (entity.getY() - entity.yOld) * (double) partialTick;
        double z = entity.zOld + (entity.getZ() - entity.zOld) * (double) partialTick;
        return new Vec3(x, y, z);
    }

    @Override
    public boolean shouldRender(EntityCockatrice entity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, camera, camX, camY, camZ)) {
            return true;
        }
        if (entity.hasTargetedEntity()) {
            LivingEntity target = entity.getTargetedEntity();
            if (target != null) {
                Vec3 end = getPosition(target, (double) target.getBbHeight() * 0.5D, 1.0F);
                Vec3 start = getPosition(entity, entity.getEyeHeight(), 1.0F);
                return camera.isVisible(new AABB(start.x, start.y, start.z, end.x, end.y, end.z));
            }
        }
        return false;
    }

    @Override
    public void submit(CockatriceRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        model = state.isBaby ? babyModel : adultModel;
        super.submit(state, poseStack, collector, camera);
        if (!state.blinded && state.mutuallyLooking && state.beamTarget != null) {
            CockatriceBeamRender.render(state, state.beamTarget, poseStack, collector);
        }
    }

    @Override
    protected void scale(CockatriceRenderState state, PoseStack poseStack) {
        if (state.isBaby) {
            poseStack.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public Identifier getTextureLocation(CockatriceRenderState state) {
        if (state.isBaby) {
            return state.hen ? TEXTURE_HEN_CHICK : TEXTURE_ROOSTER_CHICK;
        }
        return state.hen ? TEXTURE_HEN : TEXTURE_ROOSTER;
    }
}
