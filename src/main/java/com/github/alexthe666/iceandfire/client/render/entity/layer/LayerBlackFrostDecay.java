package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
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
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class LayerBlackFrostDecay extends RenderLayer<DragonRenderState, EntityModel<DragonRenderState>> {
    private static final Identifier DECAY = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/icedragon/ice_skeleton_5.png");
    private final EntityModel<DragonRenderState> decay;

    public LayerBlackFrostDecay(RenderDragonBase renderer) {
        super(renderer);
        try {
            var container = TabulaModelHandlerHelper.loadTabulaModel("/assets/iceandfire/models/tabula/icedragon/icedragon_Ground");
            TabulaModel<DragonRenderState> cropped = new TabulaModel<>(container, (model, state) -> {
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
            DragonEyeCropper.retainNamedAndSubtrees(cropped,
                new String[]{"Neck1", "Neck2", "Neck3", "Jaw"},
                new String[]{"Tail1", "armL1", "armR1"});
            decay = cropped.asEntityModel();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot load Black Frost decay geometry", exception);
        }
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light, DragonRenderState state, float yaw, float pitch) {
        if (!state.blackFrost || state.modelDead) {
            return;
        }
        collector.order(1).submitModel(decay, state, poses, RenderTypes.entityCutoutCull(DECAY),
            light, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
    }
}
