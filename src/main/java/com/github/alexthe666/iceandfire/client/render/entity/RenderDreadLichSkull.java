package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDreadLichSkull;
import com.github.alexthe666.iceandfire.entity.EntityDreadLichSkull;
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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RenderDreadLichSkull extends EntityRenderer<EntityDreadLichSkull, DreadLichSkullRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/dread/dread_lich_skull.png");
    private final EntityModel<DreadLichSkullRenderState> model = new ModelDreadLichSkull().asEntityModel();

    public RenderDreadLichSkull(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public DreadLichSkullRenderState createRenderState() {
        return new DreadLichSkullRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadLichSkull entity, DreadLichSkullRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.visible = entity.tickCount > 3;
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
    }

    @Override
    public void submit(DreadLichSkullRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.visible) {
            poses.pushPose();
            poses.scale(1.5F, -1.5F, 1.5F);
            poses.mulPose(Axis.YP.rotationDegrees(state.yRot - 180));
            collector.submitModel(model, state, poses, RenderTypes.eyes(TEXTURE),
                240, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
            poses.popPose();
        }
        super.submit(state, poses, collector, camera);
    }

    public @NotNull Identifier getTextureLocation(DreadLichSkullRenderState state) {
        return TEXTURE;
    }
}
