package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.client.model.ArmedCitadelEntityModel;
import com.github.alexthe666.iceandfire.client.model.ModelDreadThrall;
import com.github.alexthe666.iceandfire.client.render.entity.layer.IHasArmorVariantResource;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerBipedArmorMultiple;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDreadItemInHand;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadThrall;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class RenderDreadThrall extends MobRenderer<EntityDreadThrall, DreadHumanoidRenderState, ArmedCitadelEntityModel>
    implements IHasArmorVariantResource {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_thrall.png");
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_thrall_eyes.png");
    public static final Identifier TEXTURE_LEG_ARMOR = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_legs.png");
    public static final Identifier TEXTURE_ARMOR_0 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_1.png");
    public static final Identifier TEXTURE_ARMOR_1 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_2.png");
    public static final Identifier TEXTURE_ARMOR_2 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_3.png");
    public static final Identifier TEXTURE_ARMOR_3 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_4.png");
    public static final Identifier TEXTURE_ARMOR_4 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_5.png");
    public static final Identifier TEXTURE_ARMOR_5 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_6.png");
    public static final Identifier TEXTURE_ARMOR_6 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_7.png");
    public static final Identifier TEXTURE_ARMOR_7 = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/thrall_chest_8.png");

    private final ModelDreadThrall body;

    public RenderDreadThrall(EntityRendererProvider.Context context) {
        this(context, new ModelDreadThrall(0.0F, false));
    }

    private RenderDreadThrall(EntityRendererProvider.Context context, ModelDreadThrall body) {
        super(context, body.asArmedEntityModel(), 0.6F);
        this.body = body;
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
        this.addLayer(new LayerDreadItemInHand<>(this));
        this.addLayer(new LayerBipedArmorMultiple(this, this, body,
            new ModelDreadThrall(0.5F, true),
            new ModelDreadThrall(1.0F, true),
            new ModelDreadThrall(1.0F, true),
            new ModelDreadThrall(1.0F, true),
            TEXTURE_ARMOR_0, TEXTURE_LEG_ARMOR));
    }

    @Override
    public Identifier getArmorResource(int variant, EquipmentSlot equipmentSlotType) {
        if (equipmentSlotType == EquipmentSlot.LEGS) {
            return TEXTURE_LEG_ARMOR;
        }
        return switch (variant) {
            case 1 -> TEXTURE_ARMOR_1;
            case 2 -> TEXTURE_ARMOR_2;
            case 3 -> TEXTURE_ARMOR_3;
            case 4 -> TEXTURE_ARMOR_4;
            case 5 -> TEXTURE_ARMOR_5;
            case 6 -> TEXTURE_ARMOR_6;
            case 7 -> TEXTURE_ARMOR_7;
            default -> TEXTURE_ARMOR_0;
        };
    }

    @Override
    public DreadHumanoidRenderState createRenderState() {
        return new DreadHumanoidRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadThrall entity, DreadHumanoidRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        HumanoidMobRenderer.extractHumanoidRenderState(entity, state, partialTick, this.itemModelResolver);
        state.tickCount = entity.tickCount;
        state.partialTick = partialTick;
        state.animationTick = entity.getAnimationTick();
        state.animation = entity.getAnimation() == EntityDreadThrall.ANIMATION_SPAWN
            ? DreadHumanoidRenderState.SPAWN : IAnimatedEntity.NO_ANIMATION;
        state.armorVariant = entity.getBodyArmorVariant();
        state.texture = TEXTURE;
        state.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        state.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        state.hideHeldItems = state.animation == DreadHumanoidRenderState.SPAWN
            && state.animationTick <= DreadHumanoidRenderState.SPAWN.getDuration() - 10;
    }

    @Override
    public void scale(DreadHumanoidRenderState state, PoseStack stack) {
        stack.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadHumanoidRenderState state) {
        return TEXTURE;
    }
}
