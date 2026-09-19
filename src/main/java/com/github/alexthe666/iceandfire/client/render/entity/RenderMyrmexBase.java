package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexBase;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexPupa;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState.AnimationKind;
import com.github.alexthe666.iceandfire.client.render.entity.MyrmexRenderState.Caste;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerMyrmexItem;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderMyrmexBase extends MobRenderer<EntityMyrmexBase, MyrmexRenderState, EntityModel<MyrmexRenderState>> {

    private static final Identifier[] LARVA_TEXTURES = texturePair("larva");
    private static final Identifier[] PUPA_TEXTURES = texturePair("pupa");
    private static final Identifier[] WORKER_TEXTURES = texturePair("worker");
    private static final Identifier[] SOLDIER_TEXTURES = texturePair("soldier");
    private static final Identifier[] SENTINEL_TEXTURES = texturePair("sentinel");
    private static final Identifier[] HIDDEN_SENTINEL_TEXTURES = texturePair("sentinel_hidden");
    private static final Identifier[] ROYAL_TEXTURES = texturePair("royal");
    private static final Identifier[] QUEEN_TEXTURES = texturePair("queen");

    private final ModelMyrmexBase adultModel;
    private final EntityModel<MyrmexRenderState> adultAdapter;
    private final EntityModel<MyrmexRenderState> pupaAdapter;

    public RenderMyrmexBase(EntityRendererProvider.Context context, ModelMyrmexBase model, float shadowSize) {
        super(context, model.asEntityModel(), shadowSize);
        this.adultModel = model;
        this.adultAdapter = this.model;
        this.pupaAdapter = new ModelMyrmexPupa().asEntityModel();
        this.addLayer(new LayerMyrmexItem(this));
    }

    public ModelMyrmexBase getAdultModel() {
        return adultModel;
    }

    @Override
    public MyrmexRenderState createRenderState() {
        return new MyrmexRenderState();
    }

    @Override
    public void extractRenderState(EntityMyrmexBase entity, MyrmexRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.caste = getCaste(entity);
        state.growthStage = entity.getGrowthStage();
        state.animation = getAnimationKind(entity.getAnimation());
        state.animationTick = entity.getAnimationTick();
        state.partialTick = partialTick;
        state.tickCount = entity.tickCount;
        state.hasPassengers = !entity.getPassengers().isEmpty();
        state.holding = false;
        state.hiding = false;
        state.holdingProgress = 0.0F;
        state.hidingProgress = 0.0F;
        state.flying = false;
        state.onGround = false;
        state.flyProgress = 0.0F;
        if (entity instanceof EntityMyrmexSentinel sentinel) {
            state.holding = sentinel.getHeldEntity() != null;
            state.hiding = sentinel.isHiding();
            state.holdingProgress = sentinel.holdingProgress;
            state.hidingProgress = sentinel.hidingProgress;
        }
        if (entity instanceof EntityMyrmexRoyal royal) {
            state.flying = royal.isFlying();
            state.onGround = royal.onGround();
            state.flyProgress = royal.flyProgress;
        }
        state.modelScale = entity.getModelScale();
        state.passenger = entity.isPassenger();
        state.texture = selectTexture(state, entity.isJungle());
        state.mouthItem.clear();
        state.mouthItemIsBlock = false;
        state.shiftKeyDown = false;
        if (entity instanceof EntityMyrmexWorker && state.growthStage >= 2) {
            ItemStack item = entity.getItemInHand(InteractionHand.MAIN_HAND);
            // The old FIXED render used no owner and seed zero; retain those item-model inputs.
            this.itemModelResolver.updateForTopItem(state.mouthItem, item, ItemDisplayContext.FIXED, entity.level(), null, 0);
            state.mouthItemIsBlock = item.getItem() instanceof BlockItem;
            state.shiftKeyDown = entity.isShiftKeyDown();
        }
    }

    @Override
    public void submit(MyrmexRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
        // Submit the fixed adapter itself, not a deferred callback that reads this.model later.
        this.model = state.usesPupaModel() ? pupaAdapter : adultAdapter;
        super.submit(state, stack, collector, camera);
    }

    @Override
    protected void scale(MyrmexRenderState state, PoseStack stack) {
        float scale = state.modelScale;
        if (state.growthStage == 0) {
            scale /= 2.0F;
        } else if (state.growthStage == 1) {
            scale /= 1.5F;
        }
        stack.scale(scale, scale, scale);
        if (state.passenger && state.growthStage < 2) {
            stack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }
    }

    @Override
    public Identifier getTextureLocation(MyrmexRenderState state) {
        return state.texture;
    }

    private static Caste getCaste(EntityMyrmexBase entity) {
        if (entity instanceof EntityMyrmexQueen) {
            return Caste.QUEEN;
        } else if (entity instanceof EntityMyrmexRoyal) {
            // Swarmers inherit the royal model, animation tokens and textures.
            return Caste.ROYAL;
        } else if (entity instanceof EntityMyrmexSentinel) {
            return Caste.SENTINEL;
        } else if (entity instanceof EntityMyrmexSoldier) {
            return Caste.SOLDIER;
        }
        return Caste.WORKER;
    }

    private static AnimationKind getAnimationKind(Animation animation) {
        if (animation == EntityMyrmexBase.ANIMATION_PUPA_WIGGLE) {
            return AnimationKind.PUPA_WIGGLE;
        } else if (animation == EntityMyrmexWorker.ANIMATION_BITE
            || animation == EntityMyrmexSoldier.ANIMATION_BITE
            || animation == EntityMyrmexRoyal.ANIMATION_BITE
            || animation == EntityMyrmexQueen.ANIMATION_BITE) {
            return AnimationKind.BITE;
        } else if (animation == EntityMyrmexWorker.ANIMATION_STING
            || animation == EntityMyrmexSoldier.ANIMATION_STING
            || animation == EntityMyrmexRoyal.ANIMATION_STING
            || animation == EntityMyrmexQueen.ANIMATION_STING) {
            return AnimationKind.STING;
        } else if (animation == EntityMyrmexSentinel.ANIMATION_STING) {
            return AnimationKind.SENTINEL_STING;
        } else if (animation == EntityMyrmexSentinel.ANIMATION_GRAB) {
            return AnimationKind.GRAB;
        } else if (animation == EntityMyrmexSentinel.ANIMATION_NIBBLE) {
            return AnimationKind.NIBBLE;
        } else if (animation == EntityMyrmexSentinel.ANIMATION_SLASH) {
            return AnimationKind.SLASH;
        } else if (animation == EntityMyrmexQueen.ANIMATION_EGG) {
            return AnimationKind.EGG;
        } else if (animation == EntityMyrmexQueen.ANIMATION_DIGNEST) {
            return AnimationKind.DIG_NEST;
        }
        return AnimationKind.NONE;
    }

    private static Identifier[] texturePair(String kind) {
        return new Identifier[]{
            Identifier.fromNamespaceAndPath("iceandfire", "textures/models/myrmex/myrmex_desert_" + kind + ".png"),
            Identifier.fromNamespaceAndPath("iceandfire", "textures/models/myrmex/myrmex_jungle_" + kind + ".png")
        };
    }

    private static Identifier selectTexture(MyrmexRenderState state, boolean jungle) {
        // Mirror getTexture/getAdultTexture without depending on the entities' legacy Identifier API.
        Identifier[] textures;
        if (state.growthStage == 0) {
            textures = LARVA_TEXTURES;
        } else if (state.growthStage == 1) {
            textures = PUPA_TEXTURES;
        } else {
            textures = switch (state.caste) {
                case WORKER -> WORKER_TEXTURES;
                case SOLDIER -> SOLDIER_TEXTURES;
                case SENTINEL -> state.hiding ? HIDDEN_SENTINEL_TEXTURES : SENTINEL_TEXTURES;
                case ROYAL -> ROYAL_TEXTURES;
                case QUEEN -> QUEEN_TEXTURES;
            };
        }
        return textures[jungle ? 1 : 0];
    }
}
