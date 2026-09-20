package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.client.render.entity.layer.*;
import com.github.alexthe666.iceandfire.client.texture.ArrayLayeredTexture;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemDisplayContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RenderDragonBase extends MobRenderer<EntityDragonBase, DragonRenderState, EntityModel<DragonRenderState>> {
    private final Map<String, Identifier> textures = new HashMap<>();
    private final TabulaModel<DragonRenderState> dragonModel;
    private final LayerDragonRider riders;
    private final int dragonType;

    public RenderDragonBase(EntityRendererProvider.Context context, TabulaModel<DragonRenderState> model, int dragonType) {
        super(context, model.asEntityModel(), 0.15F);
        this.dragonModel = model;
        this.dragonType = dragonType;
        addLayer(new LayerDragonEyes(this, dragonType));
        riders = new LayerDragonRider(this, true);
        addLayer(riders);
        addLayer(new LayerDragonBanner(this));
        addLayer(new LayerDragonArmor(this, dragonType));
        if (dragonType == 1) {
            addLayer(new LayerBlackFrostDecay(this));
        }
    }

    public TabulaModel<DragonRenderState> dragonModel() { return dragonModel; }

    @Override
    protected boolean affectedByCulling(EntityDragonBase entity) {
        return false;
    }

    @Override public DragonRenderState createRenderState() { return new DragonRenderState(); }
    @Override public Identifier getTextureLocation(DragonRenderState state) { return state.texture; }
    @Override protected float getShadowRadius(DragonRenderState state) { return state.dragonScale; }

    @Override
    protected void scale(DragonRenderState state, PoseStack poses) {
        poses.mulPose(Axis.XP.rotationDegrees(state.dragonPitch));
        poses.scale(state.dragonScale, state.dragonScale, state.dragonScale);
    }

    @Override
    public void submit(DragonRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        state.camera = camera;
        super.submit(state, poses, collector, camera);
    }

    @Override
    public void extractRenderState(EntityDragonBase entity, DragonRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // getScale() sizes the collision box; the legacy model uses renderSize / 3 instead.
        state.scale = 1.0F;
        state.dragonType = dragonType;
        state.partialTick = partialTick;
        state.renderSize = entity.getRenderSize();
        state.dragonScale = state.renderSize / 3;
        state.dragonPitch = Mth.lerp(partialTick, entity.prevDragonPitch, entity.getDragonPitch());
        state.animationTick = entity.getAnimationTick();
        var animation = entity.getAnimation();
        state.animation = animation == EntityDragonBase.ANIMATION_FIRECHARGE ? AnimationKind.FIRECHARGE
            : animation == EntityDragonBase.ANIMATION_SPEAK ? AnimationKind.SPEAK
            : animation == EntityDragonBase.ANIMATION_BITE ? AnimationKind.BITE
            : animation == EntityDragonBase.ANIMATION_SHAKEPREY ? AnimationKind.SHAKEPREY
            : animation == EntityDragonBase.ANIMATION_TAILWHACK ? AnimationKind.TAILWHACK
            : animation == EntityDragonBase.ANIMATION_WINGBLAST ? AnimationKind.WINGBLAST
            : animation == EntityDragonBase.ANIMATION_ROAR ? AnimationKind.ROAR
            : animation == EntityDragonBase.ANIMATION_EPIC_ROAR ? AnimationKind.EPIC_ROAR
            : animation == EntityDragonBase.ANIMATION_EAT ? AnimationKind.EAT : AnimationKind.NONE;
        state.walkCycle = entity.walkCycle;
        state.flightCycle = entity.flightCycle;
        state.swimCycle = entity.swimCycle;
        state.hoverProgress = entity.hoverProgress;
        state.flyProgress = entity.flyProgress;
        state.swimProgress = entity.swimProgress;
        state.modelDeadProgress = entity.modelDeadProgress;
        state.prevModelDeadProgress = entity.prevModelDeadProgress;
        state.sleepProgress = entity.sleepProgress;
        state.sitProgress = entity.sitProgress;
        state.ridingProgress = entity.ridingProgress;
        state.tackleProgress = entity.tackleProgress;
        state.diveProgress = entity.diveProgress;
        state.prevDiveProgress = entity.prevDiveProgress;
        state.fireBreathProgress = entity.fireBreathProgress;
        state.prevFireBreathProgress = entity.prevFireBreathProgress;
        System.arraycopy(entity.prevAnimationProgresses, 0, state.prevAnimationProgresses, 0, 10);
        state.hovering = entity.isHovering();
        state.flying = entity.isFlying();
        state.sleeping = entity.isSleeping();
        state.noAi = entity.isNoAi();
        state.actuallyBreathingFire = entity.isActuallyBreathingFire();
        state.modelDead = entity.isModelDead();
        state.vehicle = entity.isVehicle();
        state.passenger = entity.isPassenger();
        state.breathingFire = entity.isBreathingFire();
        state.male = entity.isMale();
        state.customPose = entity.getCustomPose();
        // Animator applies +=; legacy ReversedBuffer applied -=, so negate to preserve visual direction.
        state.turn = entity.turn_buffer != null && !state.vehicle && !state.passenger && state.breathingFire
            ? -entity.turn_buffer.sampleYaw(partialTick) : 0;
        state.tail = entity.tail_buffer != null && !state.passenger ? entity.tail_buffer.sampleYaw(partialTick) : 0;
        boolean airborne = state.flyProgress > 0 || state.hoverProgress > 0;
        boolean buffers = entity.roll_buffer != null && entity.pitch_buffer_body != null && entity.pitch_buffer != null;
        state.roll = airborne && buffers ? sample(entity.roll_buffer, partialTick) : 0;
        state.bodyPitch = airborne && buffers ? sample(entity.pitch_buffer_body, partialTick) : 0;
        state.tailPitch = airborne && buffers ? sample(entity.pitch_buffer, partialTick) : 0;
        var legs = entity.legSolver;
        state.backLeftHeight = legs.backLeft.getHeight(partialTick);
        state.backRightHeight = legs.backRight.getHeight(partialTick);
        state.frontLeftHeight = legs.frontLeft.getHeight(partialTick);
        state.frontRightHeight = legs.frontRight.getHeight(partialTick);
        state.legBodyLength = Math.abs((legs.backLeft.forward + legs.backRight.forward - legs.frontLeft.forward - legs.frontRight.forward) / 2);
        state.blackFrost = entity instanceof EntityIceDragon ice && ice.isBlackFrost();
        state.texture = extractTexture(entity);
        state.eyeTexture = entity.shouldRenderEyes() ? EnumDragonTextures.getEyeTextureFromDragon(entity) : null;
        state.armorTexture = LayerDragonArmor.extractTexture(entity, dragonType);
        state.banner.clear();
        var banner = entity.getItemInHand(InteractionHand.OFF_HAND);
        if (banner.getItem() instanceof BannerItem) {
            Minecraft.getInstance().getItemModelResolver().updateForLiving(state.banner, banner, ItemDisplayContext.NONE, entity);
        }
        riders.extract(entity, state, partialTick);
    }

    private static float sample(IFChainBuffer buffer, float partialTick) {
        return Mth.DEG_TO_RAD * Mth.lerp(partialTick, buffer.getPreviousYawVariation(), buffer.getYawVariation());
    }

    private Identifier extractTexture(EntityDragonBase entity) {
        var layers = new ArrayList<String>();
        layers.add(EnumDragonTextures.getTextureFromDragon(entity).toString());
        if (entity.isMale() && !entity.isSkeletal()) {
            var variant = EnumDragonTextures.getDragonEnum(entity);
            layers.add((dragonType == 0 ? variant.FIRE_MALE_OVERLAY : dragonType == 1 ? variant.ICE_MALE_OVERLAY : variant.LIGHTNING_MALE_OVERLAY).toString());
        }
        String key = String.join("|", layers);
        return textures.computeIfAbsent(key, ignored -> {
            Identifier id = Identifier.fromNamespaceAndPath("iceandfire", "dragon_texture_" + dragonType + "_" + textures.size());
            Minecraft.getInstance().getTextureManager().registerAndLoad(id, new ArrayLayeredTexture(layers));
            return id;
        });
    }
}
