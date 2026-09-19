package com.github.alexthe666.citadel.client.model.basic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;

/**
 * Mutable Citadel part, adapted from AlexModGuy/Citadel commit
 * 8018e44d8b569913ca828f31aa6c86163319e7a5 (GNU LGPL).
 * Uses 26.1's actual cube tessellator rather than maintaining a second UV/normal implementation.
 */
public class BasicModelPart {
    public float textureWidth = 64, textureHeight = 32;
    public int textureOffsetX, textureOffsetY;
    public float rotationPointX, rotationPointY, rotationPointZ;
    public float rotateAngleX, rotateAngleY, rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    public final List<ModelPart.Cube> cubeList = new ArrayList<>();
    public final List<BasicModelPart> childModels = new ArrayList<>();

    public BasicModelPart(BasicEntityModel<?> model) {
        this(model.textureWidth, model.textureHeight, 0, 0);
    }

    public BasicModelPart(BasicEntityModel<?> model, int u, int v) {
        this(model.textureWidth, model.textureHeight, u, v);
    }

    public BasicModelPart(int width, int height, int u, int v) {
        setTextureSize(width, height);
        setTextureOffset(u, v);
    }

    public BasicModelPart getModelAngleCopy() {
        BasicModelPart copy = new BasicModelPart((int) textureWidth, (int) textureHeight, textureOffsetX, textureOffsetY);
        copy.copyModelAngles(this);
        return copy;
    }

    public void copyModelAngles(BasicModelPart other) {
        setPos(other.rotationPointX, other.rotationPointY, other.rotationPointZ);
        rotateAngleX = other.rotateAngleX;
        rotateAngleY = other.rotateAngleY;
        rotateAngleZ = other.rotateAngleZ;
    }

    public void addChild(BasicModelPart child) {
        if (child == this || child.contains(this)) {
            throw new IllegalArgumentException("A model hierarchy cannot contain cycles");
        }
        if (!childModels.contains(child)) childModels.add(child);
    }

    private boolean contains(BasicModelPart part) {
        return childModels.stream().anyMatch(child -> child == part || child.contains(part));
    }

    public BasicModelPart setTextureOffset(int u, int v) {
        textureOffsetX = u;
        textureOffsetY = v;
        return this;
    }

    public BasicModelPart setTextureSize(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Texture dimensions must be positive");
        textureWidth = width;
        textureHeight = height;
        return this;
    }

    public BasicModelPart addBox(String name, float x, float y, float z, int w, int h, int d, float grow, int u, int v) {
        setTextureOffset(u, v);
        addBox(x, y, z, w, h, d, grow);
        return this;
    }

    public BasicModelPart addBox(float x, float y, float z, float w, float h, float d) {
        return addBox(x, y, z, w, h, d, mirror);
    }

    public BasicModelPart addBox(float x, float y, float z, float w, float h, float d, boolean mirrored) {
        addBox(x, y, z, w, h, d, 0, mirrored);
        return this;
    }

    public void addBox(float x, float y, float z, float w, float h, float d, float grow) {
        addBox(x, y, z, w, h, d, grow, mirror);
    }

    public void addBox(float x, float y, float z, float w, float h, float d, float grow, boolean mirrored) {
        addCube(x, y, z, w, h, d, grow, grow, grow, mirrored);
    }

    public void addBox(float x, float y, float z, float w, float h, float d, float gx, float gy, float gz) {
        addCube(x, y, z, w, h, d, gx, gy, gz, mirror);
    }

    private void addCube(float x, float y, float z, float w, float h, float d, float gx, float gy, float gz, boolean mirrored) {
        cubeList.add(new ModelPart.Cube(textureOffsetX, textureOffsetY, x, y, z, w, h, d,
                gx, gy, gz, mirrored, textureWidth, textureHeight, EnumSet.allOf(Direction.class)));
    }

    public void setRotationPoint(float x, float y, float z) { setPos(x, y, z); }

    public void setPos(float x, float y, float z) {
        rotationPointX = x;
        rotationPointY = y;
        rotationPointZ = z;
    }

    public void translateRotate(PoseStack poses) {
        poses.translate(rotationPointX / 16, rotationPointY / 16, rotationPointZ / 16);
        poses.mulPose(new Quaternionf().rotationZYX(rotateAngleZ, rotateAngleY, rotateAngleX));
    }

    public void translateAndRotate(PoseStack poses) {
        translateRotate(poses);
        applyScale(poses);
    }

    protected float renderScaleX() { return 1; }
    protected float renderScaleY() { return 1; }
    protected float renderScaleZ() { return 1; }
    protected boolean scalesChildren() { return true; }

    private void applyScale(PoseStack poses) {
        poses.scale(renderScaleX(), renderScaleY(), renderScaleZ());
    }

    protected void copyTransformTo(ModelPart part) {
        part.setPos(rotationPointX, rotationPointY, rotationPointZ);
        part.setRotation(rotateAngleX, rotateAngleY, rotateAngleZ);
        part.visible = showModel;
    }

    public void render(PoseStack poses, VertexConsumer buffer, int light, int overlay) {
        render(poses, buffer, light, overlay, -1);
    }

    public void render(PoseStack poses, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        render(poses, buffer, light, overlay, packColor(r, g, b, a));
    }

    public static int packColor(float r, float g, float b, float a) {
        return channel(a) << 24 | channel(r) << 16 | channel(g) << 8 | channel(b);
    }

    private static int channel(float value) { return (int) (Math.clamp(value, 0, 1) * 255); }

    public void render(PoseStack poses, VertexConsumer buffer, int light, int overlay, int color) {
        if (!showModel) return;
        poses.pushPose();
        try {
            translateRotate(poses);
            // Isolate geometry scaling: reciprocal scaling breaks zero and negative scales.
            poses.pushPose();
            try {
                applyScale(poses);
                for (ModelPart.Cube cube : cubeList) cube.compile(poses.last(), buffer, light, overlay, color);
            } finally {
                poses.popPose();
            }
            if (scalesChildren()) applyScale(poses);
            for (BasicModelPart child : childModels) child.render(poses, buffer, light, overlay, color);
        } finally {
            poses.popPose();
        }
    }

    public ModelPart.Cube getRandomCube(Random random) { return cubeList.get(random.nextInt(cubeList.size())); }
    public ModelPart.Cube getRandomCube(RandomSource random) { return cubeList.get(random.nextInt(cubeList.size())); }

    ModelPart bake(List<Runnable> synchronizers, java.util.Set<BasicModelPart> seen) {
        if (!seen.add(this)) throw new IllegalArgumentException("Native model parts must form a tree, without shared children");
        Map<String, ModelPart> children = new LinkedHashMap<>();
        List<BasicModelPart> originalChildren = List.copyOf(childModels);
        List<ModelPart.Cube> originalCubes = List.copyOf(cubeList);
        for (int i = 0; i < childModels.size(); i++) children.put("part" + i, childModels.get(i).bake(synchronizers, seen));
        ModelPart descendants = new ModelPart(List.of(), children);
        ModelPart geometry = new ModelPart(originalCubes, Map.of());
        ModelPart transform = new ModelPart(List.of(), Map.of("geometry", geometry, "children", descendants));
        synchronizers.add(() -> {
            if (!originalChildren.equals(childModels) || !originalCubes.equals(cubeList)) {
                throw new IllegalStateException("Model topology changed; create a new asEntityModel() adapter");
            }
            copyTransformTo(transform);
            geometry.xScale = renderScaleX();
            geometry.yScale = renderScaleY();
            geometry.zScale = renderScaleZ();
            descendants.xScale = scalesChildren() ? renderScaleX() : 1;
            descendants.yScale = scalesChildren() ? renderScaleY() : 1;
            descendants.zScale = scalesChildren() ? renderScaleZ() : 1;
        });
        return transform;
    }
}
