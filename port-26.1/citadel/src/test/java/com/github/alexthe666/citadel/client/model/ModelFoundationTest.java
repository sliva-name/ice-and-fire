package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Plain-main regression suite. Requires the real 26.1 client/Forge runtime classpath, not a GPU. */
public final class ModelFoundationTest {
    private static int checks;

    public static void main(String[] args) {
        geometry();
        hierarchy();
        posesAndAnimation();
        renderStateAdapter();
        System.out.println("ModelFoundationTest: " + checks + " checks passed");
    }

    private static void geometry() {
        BasicModelPart part = new BasicModelPart(64, 32, 4, 8);
        part.addBox(0, 0, 0, 4, 8, 2, 1, 2, 3);
        Capture vertices = render(part);
        check(vertices.vertices.size() == 24, "six textured quads");
        near(vertices.min(0), -1f / 16, "grow X");
        near(vertices.max(0), 5f / 16, "grow X max");
        near(vertices.min(1), -2f / 16, "grow Y");
        near(vertices.max(2), 5f / 16, "grow Z");
        near(vertices.vertices.get(0).u, 10f / 64, "down face U");
        near(vertices.vertices.get(0).v, 8f / 32, "down face V");
        near(vertices.vertices.get(0).ny, -1, "down face normal");
        check(vertices.vertices.get(0).color == 0x80402010, "packed color");
        check(vertices.vertices.get(0).light == 123 && vertices.vertices.get(0).overlay == 456, "light and overlay");
        near(part.cubeList.get(0).minX, 0, "nominal cube bound excludes growth");
        near(part.cubeList.get(0).maxX, 4, "nominal max bound");

        BasicModelPart mirrored = new BasicModelPart(64, 32, 4, 8);
        mirrored.addBox(0, 0, 0, 4, 8, 2, true);
        near(render(mirrored).vertices.get(8).nx, 1, "mirror flips X normals");
        var polygon = mirrored.cubeList.get(0).polygons[2];
        var a = polygon.vertices()[0];
        var b = polygon.vertices()[1];
        var c = polygon.vertices()[2];
        Vector3f cross = new Vector3f(b.x() - a.x(), b.y() - a.y(), b.z() - a.z())
                .cross(new Vector3f(c.x() - b.x(), c.y() - b.y(), c.z() - b.z())).normalize();
        near(cross.dot(polygon.normal()), 1, "mirrored winding agrees with normal");

        BasicModelPart plane = new BasicModelPart(32, 32, 0, 0);
        plane.addBox(0, 0, 0, 0, 4, 4);
        check(render(plane).vertices.size() == 24, "zero-width wing geometry is retained");
        near(render(plane).max(0), 0, "plane has no thickness");
        check(BasicModelPart.packColor(1, 0.5f, 0, 1) == 0xffff7f00, "legacy color conversion");
    }

    private static void hierarchy() {
        Fixture model = new Fixture();
        model.root.setPos(16, 0, 0);
        model.root.rotateAngleZ = (float) Math.PI / 2;
        model.child.setPos(16, 0, 0);
        model.root.setScale(2, 3, 4);
        Capture vertices = render(model.root);
        near(vertices.min(0), 0, "unscaled child rotated bounds");
        near(vertices.min(1), 1, "child pivot inherits rotation, not scale");
        model.root.setShouldScaleChildren(true);
        vertices = render(model.root);
        near(vertices.min(0), -2, "scaled child width");
        near(vertices.min(1), 2, "scaled child pivot");
        model.root.setShouldScaleChildren(false);
        model.root.setScale(0, -2, 3);
        vertices = render(model.root);
        near(vertices.min(0), 0, "zero parent scale does not collapse independent child");
        near(vertices.min(1), 1, "negative parent scale does not corrupt independent child");
        model.root.showModel = false;
        check(render(model.root).vertices.isEmpty(), "hidden parent hides subtree");
        model.root.showModel = true;
        model.root.addChild(model.child);
        check(model.root.childModels.size() == 1, "addChild is not duplicated");
        expect(IllegalArgumentException.class, () -> model.child.addChild(model.root));
        expect(IllegalArgumentException.class, () -> model.root.setParent(model.child));
        check(model.child.getParent() == model.root && model.root.getParent() == null, "failed cycle leaves hierarchy intact");
        model.child.setParent(null);
        check(model.root.childModels.isEmpty(), "detach removes child");
        model.child.setParent(model.root);
        check(model.child.getParent() == model.root, "reparent updates both sides");
        PoseStack attachment = new PoseStack();
        model.child.translateAndRotateWithParents(attachment);
        Vector3f origin = attachment.last().pose().transformPosition(0, 0, 0, new Vector3f());
        near(origin.x, 1, "attachment X");
        near(origin.y, 1, "attachment Y");

        model.child.rotateAngleX = 0.3f;
        model.child.rotateAngleY = -0.5f;
        model.child.rotateAngleZ = 0.7f;
        PoseStack actual = new PoseStack();
        model.child.translateAndRotate(actual);
        Matrix4f expected = new Matrix4f().translation(1, 0, 0).rotateZ(0.7f).rotateY(-0.5f).rotateX(0.3f);
        check(actual.last().pose().equals(expected, 0.00001f), "rotation order is Z Y X");
        BasicModelPart copy = model.child.getModelAngleCopy();
        near(copy.rotateAngleX, 0.3f, "angle copy");
        near(copy.rotationPointX, 16, "pivot copy");
        check(copy.cubeList.isEmpty(), "angle copy does not duplicate geometry");
    }

    private static void posesAndAnimation() {
        Fixture model = new Fixture();
        AdvancedModelBox box = model.child;
        box.setPos(1, 2, 3);
        box.rotateAngleX = 0.25f;
        box.offsetZ = 4;
        box.setScale(2, 3, 4);
        model.updateDefaultPose();
        box.setPos(9, 9, 9); box.rotateAngleX = 2; box.offsetZ = 8; box.setScale(1, 1, 1);
        model.resetToDefaultPose();
        near(box.rotationPointY, 2, "default pivot");
        near(box.rotateAngleX, 0.25f, "default rotation");
        near(box.offsetZ, 4, "default offset");
        near(box.scaleY, 3, "default scale");
        model.setMovementScale(2);
        model.walk(box, 1, 0.5f, true, 0, 0.25f, 0, 0.5f);
        near(box.rotateAngleX, -0.375f, "walk movement scale, weight and invert");
        model.swing(box, 1, 0.5f, false, 0, 0, 0, 1);
        model.flap(box, 1, 0.5f, false, 0, 0, 0, 1);
        near(box.rotateAngleY, 1, "swing axis");
        near(box.rotateAngleZ, 1, "flap axis");
        model.bob(box, 1, 0.5f, false, 0, 1);
        near(box.rotationPointY, 1, "bob movement scale");
        near(model.moveBox(1, 2, false, 0, 1), -2, "moveBox historical formula");
        model.resetToDefaultPose();
        model.progressRotation(box, 5, 1.25f, 0, 0, 10);
        near(box.rotateAngleX, 0.75f, "absolute progress subtracts default");
        model.progressPosition(box, 5, 5, 2, 3, 10);
        near(box.rotationPointX, 3, "absolute position progress");
        model.progressRotationPrev(box, 5, 1, 0, 0, 10);
        model.progressPositionPrev(box, 5, 2, 0, 0, 10);
        near(box.rotateAngleX, 1.25f, "relative rotation progress");
        near(box.rotationPointX, 4, "relative position progress");
        model.resetToDefaultPose();
        model.faceTarget(90, 45, 2, model.root, box);
        near(box.rotateAngleY, (float) Math.PI / 8, "distributed target yaw");
        model.resetToDefaultPose();
        AdvancedModelBox[] chain = {model.root, box};
        model.chainSwing(chain, 1, 0.5f, 2, 0, 1);
        model.chainWave(chain, 1, 0.5f, 2, 0, 1);
        model.chainFlap(chain, 1, 0.5f, 2, 0, 1);
        near(model.root.rotateAngleY, 1, "chain amplitude");
        near(box.rotateAngleY, 0, "chain phase");
        near(model.root.rotateAngleX, 1, "chain wave axis");
        near(model.root.rotateAngleZ, 1, "chain flap axis");
        AdvancedModelBox target = new AdvancedModelBox(model);
        target.setPos(5, 6, 7); target.offsetZ = 8;
        box.transitionTo(target, 5, 10);
        near(box.rotationPointX, 3, "transition pivot");
        near(box.offsetZ, 6, "transition offset");
        expect(IllegalArgumentException.class, () -> box.transitionTo(target, 1, 0));
        expect(IllegalArgumentException.class, () -> model.faceTarget(1, 1, 0, box));
        expect(IllegalArgumentException.class, () -> model.progressPosition(box, 1, 0, 0, 0, 0));
    }

    private static void renderStateAdapter() {
        Fixture model = new Fixture();
        model.root.setPos(16, 0, 0);
        model.root.rotateAngleZ = 0.3f;
        model.root.setScale(2, 3, 4);
        model.root.offsetY = 8;
        model.child.setPos(0, 16, 0);
        model.updateDefaultPose();
        var adapter = model.asEntityModel();
        check(adapter.allParts().size() == 7, "native tree constructed before Model caches allParts");
        State state = new State();
        state.angle = 0.7f;
        for (boolean scaleChildren : new boolean[]{false, true}) {
            model.root.scaleChildren = scaleChildren;
            adapter.setupAnim(state);
            Capture direct = render(model.root);
            Capture nativeVertices = new Capture();
            adapter.renderToBuffer(new PoseStack(), nativeVertices, 123, 456, 0x80402010);
            check(direct.vertices.equals(nativeVertices.vertices), "adapter matches direct rendering including transforms, UVs and normals");
            adapter.setupAnim(state);
            near(model.child.rotateAngleY, 0.7f, "state setup resets before animation, no accumulation");
        }
        adapter.resetPose();
        var nativeTransform = adapter.root().getChild("part0");
        near(nativeTransform.x, 16, "native reset restores construction pivot");
        near(nativeTransform.y, 8, "native reset restores construction offset");
        near(nativeTransform.getChild("geometry").yScale, 3, "native reset restores scale");
        near(nativeTransform.getChild("children").xScale, 1, "native reset restores construction scale policy");
        model.root.showModel = false;
        adapter.setupAnim(state);
        Capture hidden = new Capture();
        adapter.renderToBuffer(new PoseStack(), hidden, 0, 0);
        check(hidden.vertices.isEmpty(), "adapter synchronizes visibility");
        model.child.addBox(0, 0, 0, 1, 1, 1);
        expect(IllegalStateException.class, () -> adapter.setupAnim(state));
        check(model.asEntityModel().allParts().size() == 7, "adapter can be rebuilt after geometry changes");
    }

    private static Capture render(BasicModelPart part) {
        Capture capture = new Capture();
        PoseStack poses = new PoseStack();
        Matrix4f before = new Matrix4f(poses.last().pose());
        part.render(poses, capture, 123, 456, 0x80402010);
        check(before.equals(poses.last().pose()), "render restores caller's pose");
        return capture;
    }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001f, message + ": " + actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void expect(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); } catch (Throwable error) {
            check(type.isInstance(error), "expected " + type + ", got " + error);
            return;
        }
        throw new AssertionError("Expected " + type);
    }

    private static final class State extends EntityRenderState { float angle; }

    private static final class Fixture extends AdvancedEntityModel<State> {
        final AdvancedModelBox root = new AdvancedModelBox(this, "root");
        final AdvancedModelBox child = new AdvancedModelBox(this, 0, 0);
        Fixture() { root.addChild(child); child.addBox(0, 0, 0, 16, 16, 16); updateDefaultPose(); }
        @Override public Iterable<BasicModelPart> parts() { return List.of(root); }
        @Override public Iterable<AdvancedModelBox> getAllParts() { return List.of(root, child); }
        @Override public void setupAnim(State state) { super.setupAnim(state); child.rotateAngleY += state.angle; }
    }

    private record Vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {}

    private static final class Capture implements VertexConsumer {
        final List<Vertex> vertices = new ArrayList<>();
        @Override public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
            vertices.add(new Vertex(x, y, z, color, u, v, overlay, light, nx, ny, nz));
        }
        float min(int axis) { return (float) vertices.stream().mapToDouble(v -> axis == 0 ? v.x : axis == 1 ? v.y : v.z).min().orElseThrow(); }
        float max(int axis) { return (float) vertices.stream().mapToDouble(v -> axis == 0 ? v.x : axis == 1 ? v.y : v.z).max().orElseThrow(); }
        @Override public VertexConsumer addVertex(float x, float y, float z) { throw new AssertionError("Expected full vertex"); }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) { throw new AssertionError(); }
        @Override public VertexConsumer setColor(int color) { throw new AssertionError(); }
        @Override public VertexConsumer setUv(float u, float v) { throw new AssertionError(); }
        @Override public VertexConsumer setUv1(int u, int v) { throw new AssertionError(); }
        @Override public VertexConsumer setUv2(int u, int v) { throw new AssertionError(); }
        @Override public VertexConsumer setNormal(float x, float y, float z) { throw new AssertionError(); }
        @Override public VertexConsumer setLineWidth(float width) { throw new AssertionError(); }
    }
}
