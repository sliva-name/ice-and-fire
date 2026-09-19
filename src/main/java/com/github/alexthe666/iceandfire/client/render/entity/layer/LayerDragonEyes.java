package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.util.TabulaModelHandlerHelper;
import com.github.alexthe666.iceandfire.client.render.entity.DragonEyeCropper;
import com.github.alexthe666.iceandfire.client.render.entity.DragonRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import java.io.IOException;


public class LayerDragonEyes extends RenderLayer<DragonRenderState, EntityModel<DragonRenderState>> {
    private final EntityModel<DragonRenderState> head;

    public LayerDragonEyes(RenderDragonBase renderer, int type) {
        super(renderer);
        String kind = type == 1 ? "ice" : type == 2 ? "lightning" : "fire";
        try {
            var container = TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/" + kind + "dragon/" + kind + "dragon_Ground");
            TabulaModel<DragonRenderState> cropped = new TabulaModel<>(container, (model, state) -> {
                // Re-evaluate from this submission's state, never a previous dragon's mutable pose.
                var source = renderer.dragonModel();
                source.setupAnim(state);
                for (AdvancedModelBox box : model.getAllParts()) {
                    var original = source.getCube(box.boxName);
                    box.copyModelAngles(original);
                    box.offsetX = original.offsetX;
                    box.offsetY = original.offsetY;
                    box.offsetZ = original.offsetZ;
                }
            });
            DragonEyeCropper.retainChain(cropped, "HeadFront");
            head = cropped.asEntityModel();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load dragon eye geometry: " + kind, exception);
        }
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, DragonRenderState state, float yaw, float pitch) {
        if (state.eyeTexture != null) {
            collector.order(1).submitModel(head, state, poses, RenderTypes.eyes(state.eyeTexture),
                light, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
        }
    }
}
