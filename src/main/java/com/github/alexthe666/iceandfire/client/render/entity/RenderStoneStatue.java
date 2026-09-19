package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafClientSetup;
import com.github.alexthe666.iceandfire.client.model.ModelAmphithere;
import com.github.alexthe666.iceandfire.client.model.ModelCockatrice;
import com.github.alexthe666.iceandfire.client.model.ModelCyclops;
import com.github.alexthe666.iceandfire.client.model.ModelDeathWorm;
import com.github.alexthe666.iceandfire.client.model.ModelDreadBeast;
import com.github.alexthe666.iceandfire.client.model.ModelDreadGhoul;
import com.github.alexthe666.iceandfire.client.model.ModelDreadKnight;
import com.github.alexthe666.iceandfire.client.model.ModelDreadLich;
import com.github.alexthe666.iceandfire.client.model.ModelDreadScuttler;
import com.github.alexthe666.iceandfire.client.model.ModelDreadThrall;
import com.github.alexthe666.iceandfire.client.model.ModelGhost;
import com.github.alexthe666.iceandfire.client.model.ModelGorgon;
import com.github.alexthe666.iceandfire.client.model.ModelHippocampus;
import com.github.alexthe666.iceandfire.client.model.ModelHippogryph;
import com.github.alexthe666.iceandfire.client.model.ModelHydraBody;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexQueen;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexRoyal;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSentinel;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSoldier;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexWorker;
import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.model.ModelSiren;
import com.github.alexthe666.iceandfire.client.model.ModelStonePlayer;
import com.github.alexthe666.iceandfire.client.model.ModelStymphalianBird;
import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.IafRenderType;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerHydraHead;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class RenderStoneStatue extends EntityRenderer<EntityStoneStatue, StoneStatueRenderState> {
    private final Map<String, Entity> hollowEntityMap = new HashMap<>();
    private final Map<String, AdvancedEntityModel<?>> citadelMap = new HashMap<>();
    private final EntityModel<?> pigModel;
    private final EntityModel<HumanoidRenderState> playerModel;

    public RenderStoneStatue(EntityRendererProvider.Context context) {
        super(context);
        this.pigModel = new PigModel(context.bakeLayer(ModelLayers.PIG));
        this.playerModel = new ModelStonePlayer(context.bakeLayer(ModelLayers.PLAYER));
    }

    @Override
    public StoneStatueRenderState createRenderState() {
        return new StoneStatueRenderState();
    }

    public @NotNull Identifier getTextureLocation(StoneStatueRenderState state) {
        return Identifier.withDefaultNamespace("textures/atlas/blocks.png");
    }

    @Override
    public void extractRenderState(EntityStoneStatue entity, StoneStatueRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.statueScale = StoneStatuePose.scale(entity.getScale());
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.crackAmount = entity.getCrackAmount();
        state.crackTexture = StoneStatuePose.crackTexture(state.crackAmount);
        state.sitting = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
        state.trappedBaby = entity.isBaby();
        state.attackTime = entity.getAttackAnim(partialTick);
        state.trappedType = entity.getTrappedEntityTypeString();
        state.bodyRenderType = IafRenderType.getStoneMobRenderType(200, 200);
        state.hydra = null;
        state.vanillaModel = null;
        state.vanillaState = null;

        Entity fake = resolveFake(entity);
        if (fake instanceof EntityTroll troll) {
            state.kind = StoneStatueRenderState.Kind.TROLL;
            state.trollVariant = switch (troll.getTrollType()) {
                case FOREST -> TrollRenderState.Variant.FOREST;
                case FROST -> TrollRenderState.Variant.FROST;
                case MOUNTAIN -> TrollRenderState.Variant.MOUNTAIN;
            };
            state.bodyRenderType = RenderTypes.entityCutout(state.trollVariant.stoneTexture);
            return;
        }
        if (fake instanceof EntityHydra hydra) {
            state.kind = StoneStatueRenderState.Kind.HYDRA;
            HydraRenderState hydraState = new HydraRenderState();
            RenderHydra.extractHydraState(hydra, hydraState, partialTick);
            hydraState.stone = true;
            hydraState.walkAnimationPos = 0.0F;
            hydraState.walkAnimationSpeed = 0.0F;
            hydraState.ageInTicks = 0.0F;
            hydraState.yRot = 0.0F;
            hydraState.xRot = 0.0F;
            state.hydra = hydraState;
            return;
        }
        if (isCitadelType(state.trappedType)) {
            state.kind = StoneStatueRenderState.Kind.CITADEL;
            return;
        }
        if (entity.getTrappedEntityType() == EntityType.PLAYER) {
            state.kind = StoneStatueRenderState.Kind.PLAYER;
            HumanoidRenderState humanoid = new HumanoidRenderState();
            humanoid.isBaby = state.trappedBaby;
            humanoid.isPassenger = state.sitting;
            humanoid.attackTime = state.attackTime;
            state.vanillaModel = playerModel;
            state.vanillaState = humanoid;
            return;
        }
        state.kind = StoneStatueRenderState.Kind.VANILLA;
        resolveVanilla(fake, state, partialTick);
    }

    @Override
    public void submit(StoneStatueRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.kind == StoneStatueRenderState.Kind.VANILLA || state.kind == StoneStatueRenderState.Kind.PLAYER) {
            if (state.vanillaModel == null || state.vanillaState == null) {
                return;
            }
            submitVanilla(state, poses, collector, state.bodyRenderType);
            if (state.crackTexture != null) {
                submitVanilla(state, poses, collector, IafRenderType.getStoneCrackRenderType(state.crackTexture));
            }
            return;
        }
        AdvancedEntityModel<?> geometry = geometryFor(state);
        if (geometry == null) {
            return;
        }
        submitCitadel(geometry, state, poses, collector, state.bodyRenderType, false);
        if (state.kind == StoneStatueRenderState.Kind.HYDRA && state.hydra != null && geometry instanceof ModelHydraBody body) {
            poses.pushPose();
            StoneStatuePose.applyTransforms(poses, state.statueScale, state.yRot);
            LayerHydraHead.submitStatueHeads(body, state.hydra, poses, collector, state.lightCoords, state.outlineColor);
            poses.popPose();
        }
        if (state.crackTexture != null) {
            submitCitadel(geometry, state, poses, collector,
                IafRenderType.getStoneCrackRenderType(state.crackTexture), true);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void submitVanilla(StoneStatueRenderState state, PoseStack poses, SubmitNodeCollector collector, net.minecraft.client.renderer.rendertype.RenderType type) {
        poses.pushPose();
        StoneStatuePose.applyTransforms(poses, state.statueScale, state.yRot);
        collector.submitModel((EntityModel) state.vanillaModel, state.vanillaState, poses, type,
            state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poses.popPose();
    }

    private void submitCitadel(AdvancedEntityModel<?> geometry, StoneStatueRenderState state, PoseStack poses,
                               SubmitNodeCollector collector, net.minecraft.client.renderer.rendertype.RenderType type,
                               boolean crackPass) {
        StoneStatuePose.poseCitadel(geometry, crackPass, state.trappedBaby);
        poses.pushPose();
        StoneStatuePose.applyTransforms(poses, state.statueScale, state.yRot);
        collector.submitModelPart(geometry.asEntityModel().root(), poses, type, state.lightCoords,
            OverlayTexture.NO_OVERLAY, null, false, false, -1, null, state.outlineColor);
        poses.popPose();
    }

    private static boolean isCitadelType(String trappedType) {
        return switch (StoneStatuePose.entityPath(trappedType)) {
            case "hippogryph", "gorgon", "pixie", "cyclops", "siren", "hippocampus", "deathworm",
                 "cockatrice", "stymphalian_bird", "troll", "myrmex_worker", "myrmex_soldier",
                 "myrmex_sentinel", "myrmex_royal", "myrmex_swarmer", "myrmex_queen", "amphithere",
                 "dread_thrall", "dread_ghoul", "dread_beast", "dread_scuttler", "dread_lich",
                 "dread_knight", "hydra", "ghost", "fire_dragon", "ice_dragon", "lightning_dragon",
                 "sea_serpent" -> true;
            default -> false;
        };
    }

    private AdvancedEntityModel<?> geometryFor(StoneStatueRenderState state) {
        return citadelMap.computeIfAbsent(state.trappedType, this::createGeometry);
    }

    private AdvancedEntityModel<?> createGeometry(String trappedType) {
        return switch (StoneStatuePose.entityPath(trappedType)) {
            case "hippogryph" -> new ModelHippogryph();
            case "gorgon" -> new ModelGorgon();
            case "pixie" -> new ModelPixie();
            case "cyclops" -> new ModelCyclops();
            case "siren" -> new ModelSiren();
            case "hippocampus" -> new ModelHippocampus();
            case "deathworm" -> new ModelDeathWorm();
            case "cockatrice" -> new ModelCockatrice();
            case "stymphalian_bird" -> new ModelStymphalianBird();
            case "troll" -> new ModelTroll();
            case "myrmex_worker" -> new ModelMyrmexWorker();
            case "myrmex_soldier" -> new ModelMyrmexSoldier();
            case "myrmex_sentinel" -> new ModelMyrmexSentinel();
            case "myrmex_royal", "myrmex_swarmer" -> new ModelMyrmexRoyal();
            case "myrmex_queen" -> new ModelMyrmexQueen();
            case "amphithere" -> new ModelAmphithere();
            case "dread_thrall" -> new ModelDreadThrall(0.0F, false);
            case "dread_ghoul" -> new ModelDreadGhoul(0.0F);
            case "dread_beast" -> new ModelDreadBeast();
            case "dread_scuttler" -> new ModelDreadScuttler();
            case "dread_lich" -> new ModelDreadLich(0.0F);
            case "dread_knight" -> new ModelDreadKnight(0.0F);
            case "hydra" -> new ModelHydraBody();
            case "ghost" -> new ModelGhost(0.0F);
            case "fire_dragon" -> IafClientSetup.FIRE_DRAGON_BASE_MODEL;
            case "ice_dragon" -> IafClientSetup.ICE_DRAGON_BASE_MODEL;
            case "lightning_dragon" -> IafClientSetup.LIGHTNING_DRAGON_BASE_MODEL;
            case "sea_serpent" -> IafClientSetup.SEA_SERPENT_BASE_MODEL;
            default -> null;
        };
    }

    private void resolveVanilla(Entity fake, StoneStatueRenderState state, float partialTick) {
        if (fake != null) {
            EntityRenderer<? super Entity, ?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(fake);
            if (renderer instanceof LivingEntityRenderer<?, ?, ?> living) {
                state.vanillaModel = living.getModel();
                EntityRenderState extracted = renderer.createRenderState(fake, partialTick);
                StoneStatuePose.freezeVanilla(extracted);
                if (extracted instanceof LivingEntityRenderState livingState) {
                    livingState.isBaby = state.trappedBaby;
                    if (state.sitting) {
                        livingState.pose = net.minecraft.world.entity.Pose.SITTING;
                    }
                }
                if (extracted instanceof HumanoidRenderState humanoid) {
                    humanoid.isPassenger = state.sitting;
                }
                state.vanillaState = extracted;
                return;
            }
        }
        LivingEntityRenderState pigState = new LivingEntityRenderState();
        pigState.isBaby = state.trappedBaby;
        if (state.sitting) {
            pigState.pose = net.minecraft.world.entity.Pose.SITTING;
        }
        StoneStatuePose.freezeVanilla(pigState);
        state.vanillaModel = pigModel;
        state.vanillaState = pigState;
    }

    private Entity resolveFake(EntityStoneStatue statue) {
        String key = statue.getTrappedEntityTypeString();
        Entity cached = hollowEntityMap.get(key);
        if (cached != null) {
            return cached;
        }
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            return null;
        }
        try {
            var input = net.minecraft.world.level.storage.TagValueInput.create(
                net.minecraft.util.ProblemReporter.DISCARDING, client.level.registryAccess(), statue.getTrappedTag());
            Entity build = statue.getTrappedEntityType().create(input, client.level, net.minecraft.world.entity.EntitySpawnReason.LOAD).orElse(null);
            if (build == null) {
                return null;
            }
            hollowEntityMap.put(key, build);
            return build;
        } catch (Exception e) {
            IceAndFire.LOGGER.warn("Mob {} could not build statue NBT", key);
            return null;
        }
    }
}
