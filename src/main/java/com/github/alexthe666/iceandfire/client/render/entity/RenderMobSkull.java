package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.basic.BasicEntityModel;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.iceandfire.client.model.*;
import com.github.alexthe666.iceandfire.entity.EntityMobSkull;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RenderMobSkull extends EntityRenderer<EntityMobSkull, MobSkullRenderState> {
    private static final Map<EnumSkullType, Identifier> SKULL_TEXTURE_CACHE = new EnumMap<>(EnumSkullType.class);
    private final Map<EnumSkullType, EntityModel<MobSkullRenderState>> models = new EnumMap<>(EnumSkullType.class);

    public RenderMobSkull(EntityRendererProvider.Context context, TabulaModel<SeaSerpentRenderState> seaSerpentModel) {
        super(context);
        var hippogryph = new ModelHippogryph();
        var cyclops = new ModelCyclops();
        var cockatrice = new ModelCockatrice();
        var stymphalian = new ModelStymphalianBird();
        var troll = new ModelTroll();
        var amphithere = new ModelAmphithere();
        var hydra = new ModelHydraHead(0);
        models.put(EnumSkullType.HIPPOGRYPH, headModel(hippogryph.Head, hippogryph::resetToDefaultPose, -5));
        models.put(EnumSkullType.CYCLOPS, headModel(cyclops.Head, cyclops::resetToDefaultPose, 0));
        models.put(EnumSkullType.COCKATRICE, headModel(cockatrice.head, cockatrice::resetToDefaultPose, 0));
        models.put(EnumSkullType.STYMPHALIAN, headModel(stymphalian.HeadBase, stymphalian::resetToDefaultPose, 0));
        models.put(EnumSkullType.TROLL, headModel(troll.head, troll::resetToDefaultPose, -20));
        models.put(EnumSkullType.AMPHITHERE, headModel(amphithere.Head, amphithere::resetToDefaultPose, 0));
        models.put(EnumSkullType.SEASERPENT, headModel(seaSerpentModel.getCube("Head"), seaSerpentModel::resetToDefaultPose, 0));
        models.put(EnumSkullType.HYDRA, headModel(hydra.Head1, hydra::resetToDefaultPose, 0));
    }

    @Override
    protected boolean affectedByCulling(EntityMobSkull entity) {
        return false;
    }

    private static EntityModel<MobSkullRenderState> headModel(BasicModelPart head, Runnable resetPose, float floorPitch) {
        // Bake only the head subtree. The adapter synchronizes it after posing at deferred draw time,
        // so rendering a live sea serpent cannot overwrite a skull's queued pose (or vice versa).
        return new BasicEntityModel<MobSkullRenderState>() {
            @Override
            public Iterable<BasicModelPart> parts() {
                return List.of(head);
            }

            @Override
            public void setupAnim(MobSkullRenderState state) {
                resetPose.run();
                head.rotateAngleX = (float) Math.toRadians(state.onWall ? 50 : floorPitch);
                head.rotateAngleY = 0;
                head.rotateAngleZ = 0;
            }
        }.asEntityModel();
    }

    @Override
    public MobSkullRenderState createRenderState() {
        return new MobSkullRenderState();
    }

    @Override
    public void extractRenderState(EntityMobSkull entity, MobSkullRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.skullType = entity.getSkullType();
        state.yaw = entity.getYaw();
        state.onWall = entity.isOnWall();
    }

    @Override
    public void submit(MobSkullRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poses, collector, camera);
        poses.pushPose();
        poses.mulPose(Axis.XP.rotationDegrees(-180));
        poses.mulPose(Axis.YN.rotationDegrees(180 - state.yaw));
        poses.translate(0, state.onWall ? -0.24F : -0.12F, 0.5F);
        switch (state.skullType) {
            case HIPPOGRYPH -> {
                poses.translate(0, 0, -0.2F);
                poses.scale(1.2F, 1.2F, 1.2F);
            }
            case CYCLOPS -> {
                poses.translate(0, 1.8F, -0.5F);
                poses.scale(2.25F, 2.25F, 2.25F);
            }
            case COCKATRICE -> {
                if (state.onWall) poses.translate(0, 0, 0.35F);
            }
            case STYMPHALIAN -> {
                if (!state.onWall) poses.translate(0, 0, -0.35F);
            }
            case TROLL -> {
                poses.translate(0, 1F, -0.35F);
                if (state.onWall) poses.translate(0, 0, 0.35F);
            }
            case AMPHITHERE -> {
                poses.translate(0, -0.2F, 0.7F);
                poses.scale(2F, 2F, 2F);
            }
            case SEASERPENT -> {
                poses.translate(0, -0.35F, 0.8F);
                poses.scale(2.5F, 2.5F, 2.5F);
            }
            case HYDRA -> {
                poses.translate(0, -0.2F, -0.1F);
                poses.scale(2F, 2F, 2F);
            }
        }
        collector.submitModel(models.get(state.skullType), state, poses,
            RenderTypes.entityTranslucent(getSkullTexture(state.skullType)), state.lightCoords,
            OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poses.popPose();
    }

    public Identifier getSkullTexture(EnumSkullType skull) {
        return SKULL_TEXTURE_CACHE.computeIfAbsent(skull, type -> Identifier.fromNamespaceAndPath(
            "iceandfire", "textures/models/skulls/skull_" + type.name().toLowerCase(Locale.ROOT) + ".png"));
    }
}
