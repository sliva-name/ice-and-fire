package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import java.util.HashSet;

/** Shared by the eye layer and its plain-main test; crops a Tabula copy to the head chain. */
public final class DragonEyeCropper {
    private DragonEyeCropper() {}

    /**
     * Keeps only the named box and its ancestors as renderable geometry. Ancestor boxes stay
     * connected so the cropped tree reproduces the full model's head transform. Non-chain boxes
     * lose their cubes but remain in the read-only lookup maps.
     */
    public static void retainNamedAndSubtrees(TabulaModel<?> model, String[] exactNames, String[] subtreeRoots) {
        var geometry = new HashSet<AdvancedModelBox>();
        var keep = new HashSet<AdvancedModelBox>();
        for (String name : exactNames) {
            var box = model.getCube(name);
            if (box != null) {
                geometry.add(box);
                keep.add(box);
                for (var parent = box.getParent(); parent != null; parent = parent.getParent()) {
                    keep.add(parent);
                }
            }
        }
        for (String name : subtreeRoots) {
            var root = model.getCube(name);
            if (root == null) {
                continue;
            }
            addSubtree(root, geometry);
            keep.addAll(geometry);
            for (var parent = root.getParent(); parent != null; parent = parent.getParent()) {
                keep.add(parent);
            }
        }
        if (geometry.isEmpty()) {
            throw new IllegalArgumentException("Dragon model has none of the Black Frost decay parts");
        }
        for (var box : model.getAllParts()) {
            box.childModels.removeIf(child -> !keep.contains(child));
            if (!geometry.contains(box)) {
                box.cubeList.clear();
            }
        }
    }

    private static void addSubtree(AdvancedModelBox box, HashSet<AdvancedModelBox> out) {
        if (!out.add(box)) {
            return;
        }
        for (var child : box.childModels) {
            if (child instanceof AdvancedModelBox advanced) {
                addSubtree(advanced, out);
            }
        }
    }

    public static void retainChain(TabulaModel<?> model, String... names) {
        var keep = new HashSet<AdvancedModelBox>();
        for (String name : names) {
            for (var box = model.getCube(name); box != null; box = box.getParent()) keep.add(box);
        }
        if (keep.isEmpty()) throw new IllegalArgumentException("Dragon model has none of " + String.join(", ", names));
        for (var box : model.getAllParts()) {
            box.childModels.removeIf(child -> !keep.contains(child));
            if (!keep.contains(box)) box.cubeList.clear();
        }
    }
}
