package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeGroupContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/**
 * Plain-main regression suite using the actual 26.1 client/Forge runtime (no GPU).
 * Pass the original src/main/resources directory as the sole argument. Requires
 * exactly 192 bundled .tbl assets; constructs geometry and native adapters for
 * every asset. Not wired into Gradle here: build configuration belongs to the parent.
 */
public final class TabulaModelTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        check(args.length == 1, "Pass the original resource directory");
        syntheticGraph();
        stateAnimation();
        Path root = Path.of(args[0]);
        List<Path> assets;
        try (var paths = Files.walk(root)) {
            assets = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".tbl")).sorted().toList();
        }
        check(assets.size() == 192, "Expected 192 bundled assets, got " + assets.size());
        int cubes = 0;
        for (Path path : assets) {
            try {
                TabulaModelContainer container = TabulaModelHandler.INSTANCE.loadTabulaModelArchive(
                        path.toString(), Files.newInputStream(path));
                TabulaModel<EntityRenderState> model = new TabulaModel<>(container);
                verifyGraph(container, model);
                verifyNativeGraph(model);
                cubes += list(model.getAllParts()).size();
            } catch (Exception | AssertionError failure) {
                throw new AssertionError("Asset failed: " + path, failure);
            }
        }
        check(cubes == 19766, "Bundled cube count changed: " + cubes);
        System.out.println("TabulaModelTest: " + assets.size() + " assets, " + cubes
                + " cubes, " + checks + " checks passed");
    }

    private static void syntheticGraph() {
        TabulaModelContainer container = parse("""
                {"textureWidth":64,"textureHeight":32,"scale":[2,3,4],
                 "cubes":[{"name":"same","identifier":"root","dimensions":[4,8,2],
                  "position":[16,2,3],"offset":[-1,-2,-3],"rotation":[90,-45,30],
                  "scale":[2,0,-3],"mcScale":5,"hidden":true,"opacity":25,
                  "txOffset":[4,8],"txMirror":true,
                  "children":[{"name":"child","identifier":"child","parentIdentifier":"wrong",
                   "position":[0,16,0],"dimensions":[0,4,4]}]}],
                 "cubeGroups":[{"hidden":true,"txMirror":true,
                  "cubes":[{"name":"same","identifier":"group","dimensions":[1,1,1],"position":[0,7,0]}],
                  "cubeGroups":[{"cubes":[{"name":"nested","identifier":"group","dimensions":[2,2,2]}]}]}]}
                """);
        TabulaModel<EntityRenderState> model = new TabulaModel<>(container);
        verifyGraph(container, model);
        List<AdvancedModelBox> boxes = list(model.getAllParts());
        check(boxes.size() == 4 && list(model.parts()).size() == 3, "nested organizational groups");
        check(model.getCube("same") == boxes.get(2), "duplicate name is last-wins");
        check(model.getCubeByIdentifier("group") == boxes.get(3), "duplicate identifier is last-wins");
        check(model.getCube("absent") == null && model.getCubeByIdentifier("absent") == null, "missing lookup");
        check(boxes.get(1).getParent() == boxes.get(0), "children override parentIdentifier metadata");
        check(boxes.get(0).showModel && boxes.get(2).showModel, "pinned hidden/opacity policy");
        check(!boxes.get(2).mirror, "group mirror does not override cube mirror");
        near(boxes.get(0).scaleX, 1, "pinned cube scale policy");
        near(boxes.get(0).cubeList.getFirst().polygons[2].normal().x(), 1, "mirror normal");
        near(boxes.get(0).cubeList.getFirst().polygons[0].vertices()[0].u(), 10f / 64, "mirrored UV U");
        near(boxes.get(0).cubeList.getFirst().polygons[0].vertices()[0].v(), 10f / 32, "mirrored UV V");
        // Default-pose coverage must not depend on either lookup map's uniqueness.
        for (AdvancedModelBox box : boxes) {
            box.rotationPointY += 100;
            box.rotateAngleX += 2;
            box.offsetZ = 5;
            box.setScale(2, 3, 4);
        }
        model.setupAnim(new EntityRenderState());
        verifyGraph(container, model);
        verifyNativeGraph(model);
        AdvancedModelBox parent = boxes.getFirst();
        parent.setScale(0, -2, 3);
        parent.offsetX = 4;
        model.updateDefaultPose();
        parent.setScale(1, 1, 1);
        parent.offsetX = 9;
        model.resetToDefaultPose();
        near(parent.scaleX, 0, "runtime zero scale captured");
        near(parent.scaleY, -2, "runtime negative scale captured");
        near(parent.offsetX, 4, "runtime offset captured");
        for (boolean scaleChildren : new boolean[]{false, true}) {
            parent.setShouldScaleChildren(scaleChildren);
            verifyNativeGraph(model);
        }
    }

    private static void stateAnimation() {
        TabulaModelContainer container = parse("""
                {"textureWidth":32,"textureHeight":32,"cubes":[
                 {"name":"jaw","identifier":"jaw","dimensions":[2,2,2],"rotation":[30,0,0]}]}
                """);
        Animation animation = Animation.create(10);
        int[] calls = {0};
        TabulaModel<State> model = new TabulaModel<>(container, (pose, state) -> {
            calls[0]++;
            pose.llibAnimator.update(state.animation, state.tick, state.partialTick);
            if (pose.llibAnimator.setAnimation(animation)) {
                pose.llibAnimator.startKeyframe(10);
                pose.llibAnimator.rotate(pose.getCube("jaw"), 1, 0, 0);
                pose.llibAnimator.endKeyframe();
            }
            pose.getCube("jaw").rotationPointY += state.lift;
        });
        State state = new State();
        state.animation = animation;
        state.tick = 5;
        state.partialTick = 0.5f;
        state.lift = 3;
        var adapter = model.asEntityModel();
        for (int i = 0; i < 2; i++) {
            adapter.setupAnim(state);
            float expected = (float) Math.toRadians(30) + net.minecraft.util.Mth.sin(0.55f * (float) Math.PI / 2);
            near(model.getCube("jaw").rotateAngleX, expected, "explicit timing, no pose accumulation");
            near(adapter.root().getChild("part0").xRot, expected, "callback synchronized to native graph");
            near(adapter.root().getChild("part0").y, 3, "typed state field applied");
        }
        check(calls[0] == 2, "one callback per setup");
        check(model.llibAnimator.getEntity() == null, "no live entity retained");
        state.animation = com.github.alexthe666.citadel.animation.IAnimatedEntity.NO_ANIMATION;
        state.lift = 0;
        adapter.setupAnim(state);
        near(model.getCube("jaw").rotateAngleX, (float) Math.toRadians(30), "inactive animation resets");
        check(new TabulaModel<EntityRenderState>(container).llibAnimator != model.llibAnimator, "per-model animator");
    }

    private static void verifyGraph(TabulaModelContainer container, TabulaModel<?> model) {
        List<TabulaCubeContainer> roots = new ArrayList<>(container.getCubes());
        for (TabulaCubeGroupContainer group : container.getCubeGroups()) collectRoots(group, roots);
        List<BasicModelPart> parts = list(model.parts());
        check(parts.size() == roots.size(), "root count");
        List<AdvancedModelBox> visited = new ArrayList<>();
        Map<String, AdvancedModelBox> names = new LinkedHashMap<>();
        Map<String, AdvancedModelBox> identifiers = new LinkedHashMap<>();
        for (int i = 0; i < roots.size(); i++) {
            verifyCube(roots.get(i), (AdvancedModelBox) parts.get(i), null, container, visited, names, identifiers);
        }
        check(visited.equals(list(model.getAllParts())), "all-parts preorder covers every physical cube");
        check(names.equals(model.getCubes()), "name lookup coverage");
        identifiers.forEach((id, box) -> check(model.getCubeByIdentifier(id) == box, "identifier lookup"));
        check(model.textureWidth == container.getTextureWidth() && model.texHeight == container.getTextureHeight(), "model atlas");
    }

    private static void collectRoots(TabulaCubeGroupContainer group, List<TabulaCubeContainer> roots) {
        roots.addAll(group.getCubes());
        for (TabulaCubeGroupContainer child : group.getCubeGroups()) collectRoots(child, roots);
    }

    private static void verifyCube(TabulaCubeContainer source, AdvancedModelBox box, AdvancedModelBox parent,
                                   TabulaModelContainer container, List<AdvancedModelBox> visited,
                                   Map<String, AdvancedModelBox> names, Map<String, AdvancedModelBox> identifiers) {
        visited.add(box);
        names.put(source.getName(), box);
        identifiers.put(source.getIdentifier(), box);
        check(java.util.Objects.equals(box.boxName, source.getName()), "boxName for animator pose matching");
        check(box.getParent() == parent && box.childModels.size() == source.getChildren().size(), "parent/child graph");
        double[] p = source.getPosition(), r = source.getRotation(), o = source.getOffset();
        near(box.rotationPointX, (float) p[0], "pivot X");
        near(box.rotationPointY, (float) p[1], "pivot Y");
        near(box.rotationPointZ, (float) p[2], "pivot Z");
        near(box.rotateAngleX, (float) Math.toRadians(r[0]), "radians X");
        near(box.rotateAngleY, (float) Math.toRadians(r[1]), "radians Y");
        near(box.rotateAngleZ, (float) Math.toRadians(r[2]), "radians Z");
        near(box.defaultPositionX, box.rotationPointX, "default pivot");
        near(box.defaultRotationZ, box.rotateAngleZ, "default rotation");
        near(box.offsetX + box.offsetY + box.offsetZ, 0, "geometry offset is not runtime offset");
        near(box.scaleX, 1, "pinned scale X");
        near(box.scaleY, 1, "pinned scale Y");
        near(box.scaleZ, 1, "pinned scale Z");
        int[] d = source.getDimensions(), uv = source.getTextureOffset();
        check(box.mirror == source.isTextureMirrorEnabled(), "mirror");
        check(box.textureOffsetX == uv[0] && box.textureOffsetY == uv[1], "texture offsets");
        check(box.cubeList.size() == 1, "one real native cube per Tabula cube, including planes");
        ModelPart.Cube expected = new ModelPart.Cube(uv[0], uv[1], (float) o[0], (float) o[1], (float) o[2],
                d[0], d[1], d[2], 0, 0, 0, source.isTextureMirrorEnabled(),
                container.getTextureWidth(), container.getTextureHeight(), EnumSet.allOf(Direction.class));
        ModelPart.Cube actual = box.cubeList.getFirst();
        near(actual.minX, (float) o[0], "local geometry offset");
        near(actual.maxZ, (float) o[2] + d[2], "local geometry dimension");
        check(actual.polygons.length == 6, "six native faces");
        for (int face = 0; face < 6; face++) {
            check(actual.polygons[face].normal().equals(expected.polygons[face].normal()), "face normal");
            check(java.util.Arrays.equals(actual.polygons[face].vertices(), expected.polygons[face].vertices()), "native positions, UVs and winding");
        }
        for (int i = 0; i < source.getChildren().size(); i++) {
            verifyCube(source.getChildren().get(i), (AdvancedModelBox) box.childModels.get(i), box,
                    container, visited, names, identifiers);
        }
    }

    private static void verifyNativeGraph(TabulaModel<?> model) {
        Map<ModelPart.Cube, Matrix4f> expected = new IdentityHashMap<>();
        for (BasicModelPart root : model.parts()) collectTransforms((AdvancedModelBox) root, new PoseStack(), expected);
        var adapter = model.asEntityModel();
        check(adapter.allParts().size() == list(model.getAllParts()).size() * 3 + 1, "complete native tree cached at construction");
        adapter.root().visit(new PoseStack(), (pose, path, index, cube) -> {
            Matrix4f transform = expected.remove(cube);
            check(transform != null && transform.equals(pose.pose(), 0.0001f), "native cube/pose graph: " + path);
        });
        check(expected.isEmpty(), "every cube reached exactly once by native traversal");
    }

    private static void collectTransforms(AdvancedModelBox box, PoseStack poses, Map<ModelPart.Cube, Matrix4f> cubes) {
        poses.pushPose();
        poses.translate((box.rotationPointX + box.offsetX) / 16, (box.rotationPointY + box.offsetY) / 16,
                (box.rotationPointZ + box.offsetZ) / 16);
        poses.mulPose(new org.joml.Quaternionf().rotationZYX(box.rotateAngleZ, box.rotateAngleY, box.rotateAngleX));
        Matrix4f geometry = new Matrix4f(poses.last().pose()).scale(box.scaleX, box.scaleY, box.scaleZ);
        for (ModelPart.Cube cube : box.cubeList) check(cubes.put(cube, geometry) == null, "unique cube identity");
        if (box.scaleChildren) poses.scale(box.scaleX, box.scaleY, box.scaleZ);
        for (BasicModelPart child : box.childModels) collectTransforms((AdvancedModelBox) child, poses, cubes);
        poses.popPose();
    }

    private static TabulaModelContainer parse(String json) {
        return TabulaModelHandler.INSTANCE.loadTabulaModel(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private static <T> List<T> list(Iterable<T> values) {
        List<T> result = new ArrayList<>();
        values.forEach(result::add);
        return result;
    }

    private static void near(float actual, float expected, String message) {
        check(Float.isFinite(actual) && Math.abs(actual - expected) < 0.0001f, message + ": " + actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static final class State extends EntityRenderState {
        Animation animation;
        int tick;
        float partialTick;
        float lift;
    }
}
