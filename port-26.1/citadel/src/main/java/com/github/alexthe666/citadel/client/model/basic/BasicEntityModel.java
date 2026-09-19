package com.github.alexthe666.citadel.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Adapted from AlexModGuy/Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5 (GNU LGPL).
 * Animation now consumes extracted render state, never a live entity.
 *
 * <p>Vanilla Model rendering is final in 26.1. Construct an {@link #asEntityModel()}
 * adapter after building the parts, and submit that adapter to vanilla renderers.
 * The adapter owns a fixed native tree; setupAnim synchronizes the mutable Citadel pose.
 * Direct rendering remains available for callers that already own a vertex consumer.
 * Native resetPose restores the pose captured at adapter construction, including scale.
 * Rebuild after topology changes; native rendering does not invoke custom part render overrides.
 */
public abstract class BasicEntityModel<S extends EntityRenderState> {
    public int textureWidth = 64, textureHeight = 32;
    private final Function<Identifier, RenderType> renderType;

    protected BasicEntityModel() { this(RenderTypes::entityCutout); }

    protected BasicEntityModel(Function<Identifier, RenderType> renderType) {
        this.renderType = renderType;
    }

    public abstract Iterable<BasicModelPart> parts();
    public abstract void setupAnim(S state);

    public RenderType renderType(Identifier texture) { return renderType.apply(texture); }

    public void renderToBuffer(PoseStack poses, VertexConsumer buffer, int light, int overlay, int color) {
        for (BasicModelPart part : parts()) part.render(poses, buffer, light, overlay, color);
    }

    public void renderToBuffer(PoseStack poses, VertexConsumer buffer, int light, int overlay) {
        renderToBuffer(poses, buffer, light, overlay, -1);
    }

    public void renderToBuffer(PoseStack poses, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        renderToBuffer(poses, buffer, light, overlay, BasicModelPart.packColor(r, g, b, a));
    }

    public final EntityModel<S> asEntityModel() {
        List<BasicModelPart> roots = new ArrayList<>();
        parts().forEach(roots::add);
        List<Runnable> synchronizers = new ArrayList<>();
        Map<String, ModelPart> children = new LinkedHashMap<>();
        var seen = Collections.newSetFromMap(new IdentityHashMap<BasicModelPart, Boolean>());
        for (int i = 0; i < roots.size(); i++) children.put("part" + i, roots.get(i).bake(synchronizers, seen));
        synchronizers.forEach(Runnable::run);
        ModelPart nativeRoot = new ModelPart(List.of(), children);
        for (ModelPart part : nativeRoot.getAllParts()) {
            // ModelPart.storePose() in 26.1 omits scale, so capture all nine components explicitly.
            part.setInitialPose(new PartPose(part.x, part.y, part.z, part.xRot, part.yRot, part.zRot,
                    part.xScale, part.yScale, part.zScale));
        }
        return new EntityModel<S>(nativeRoot, renderType) {
            @Override
            public void setupAnim(S state) {
                super.setupAnim(state);
                BasicEntityModel.this.setupAnim(state);
                List<BasicModelPart> currentRoots = new ArrayList<>();
                parts().forEach(currentRoots::add);
                if (!roots.equals(currentRoots)) throw new IllegalStateException("Model roots changed; recreate the adapter");
                synchronizers.forEach(Runnable::run);
            }
        };
    }
}
