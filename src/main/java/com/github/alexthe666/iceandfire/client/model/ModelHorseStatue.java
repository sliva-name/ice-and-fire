package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class ModelHorseStatue extends AdvancedEntityModel<EntityRenderState> {
    private final Map<String, AdvancedModelBox> allParts = new LinkedHashMap<>();
    private final AdvancedModelBox root;

    public ModelHorseStatue(ModelPart part) {
        root = copyPart(part, "");
        // Reuse baked cubes (including their UVs), rather than rebuilding vanilla horse geometry.
        part.visit(new PoseStack(), (pose, path, index, cube) -> {
            ModelPart source = part;
            AdvancedModelBox target = root;
            String currentPath = "";
            if (!path.isEmpty()) {
                for (String name : path.substring(1).split("/")) {
                    source = source.getChild(name);
                    currentPath += "/" + name;
                    AdvancedModelBox child = allParts.get(currentPath);
                    if (child == null) {
                        child = copyPart(source, currentPath);
                        target.addChild(child);
                    }
                    target = child;
                }
            }
            if (!source.skipDraw) {
                target.cubeList.add(cube);
            }
        });
        updateDefaultPose();
    }

    private AdvancedModelBox copyPart(ModelPart source, String path) {
        AdvancedModelBox target = new AdvancedModelBox(this, 0, 0);
        target.setPos(source.x, source.y, source.z);
        setRotateAngle(target, source.xRot, source.yRot, source.zRot);
        target.setScale(source.xScale, source.yScale, source.zScale);
        target.setShouldScaleChildren(true);
        target.showModel = source.visible;
        allParts.put(path, target);
        return target;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return allParts.values();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return List.of(root);
    }

    @Override
    public void setupAnim(EntityRenderState state) {
        // Statues retain the supplied horse pose and never run the live horse animation.
        super.setupAnim(state);
    }
}
