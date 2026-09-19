package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class LayerDragonBanner extends RenderLayer<DragonRenderState, EntityModel<DragonRenderState>> {
    private final RenderDragonBase renderer;
    public LayerDragonBanner(RenderDragonBase renderer) {
        super(renderer);
        this.renderer = renderer;
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, DragonRenderState state, float yaw, float pitch) {
        if (state.banner.isEmpty()) return;
        // Native model setup is deferred; explicitly evaluate the attachment's pose now.
        renderer.dragonModel().setupAnim(state);
        poses.pushPose();
        try {
            renderer.dragonModel().getCube("BodyUpper").translateAndRotate(poses);
            poses.translate(0, -0.2F, 0.4F);
            poses.mulPose(Axis.XP.rotationDegrees(180));
            float inverse = 1 / state.dragonScale;
            poses.scale(inverse, inverse, inverse);
            state.banner.submit(poses, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
        } finally {
            poses.popPose();
        }
    }
}
