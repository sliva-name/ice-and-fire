/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Original author: gegy1000, since 1.0.0.
 * See citadel/NOTICE.md for attribution and distribution restrictions.
 */
package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeGroupContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

/**
 * Tabula entity geometry and named poses, with a 26.1 render-state animator.
 * Build the complete model before calling {@link #asEntityModel()}.
 *
 * <p>The pinned entity renderer uses nested children (not parentIdentifier),
 * root cubes before groups, and last-wins name/identifier lookup. Groups are
 * organizational, not transform nodes. Like that renderer, this deliberately
 * ignores serialized model/cube scale, mcScale, hidden, opacity and group mirror:
 * those editor fields did not affect its geometry. Box scale remains available
 * at runtime and participates in default-pose capture/reset. This is not a
 * general Tabula editor renderer or a player for the container's animation DTOs.
 *
 * <p>Unlike upstream, all physical boxes participate in default poses, even
 * when names collide. Lookup maps and traversal lists are read-only; the boxes
 * themselves remain mutable. Each setup restores the default pose before the
 * callback, including when no animator is supplied.
 */
public class TabulaModel<S extends EntityRenderState> extends AdvancedEntityModel<S> {
    private final Map<String, AdvancedModelBox> cubes = new LinkedHashMap<>();
    private final Map<String, AdvancedModelBox> identifierMap = new LinkedHashMap<>();
    private final List<BasicModelPart> rootBoxes = new ArrayList<>();
    private final List<AdvancedModelBox> allBoxes = new ArrayList<>();
    private final Map<String, AdvancedModelBox> cubeView = Collections.unmodifiableMap(cubes);
    private final List<BasicModelPart> rootView = Collections.unmodifiableList(rootBoxes);
    private final List<AdvancedModelBox> allView = Collections.unmodifiableList(allBoxes);
    private final ITabulaModelAnimator<S> tabulaAnimator;
    public final ModelAnimator llibAnimator;

    public TabulaModel(TabulaModelContainer container) {
        this(container, null);
    }

    public TabulaModel(TabulaModelContainer container, ITabulaModelAnimator<S> tabulaAnimator) {
        this.texWidth = this.textureWidth = container.getTextureWidth();
        this.texHeight = this.textureHeight = container.getTextureHeight();
        this.tabulaAnimator = tabulaAnimator;
        this.llibAnimator = ModelAnimator.create();
        for (TabulaCubeContainer cube : container.getCubes()) {
            parseCube(cube, null);
        }
        for (TabulaCubeGroupContainer group : container.getCubeGroups()) {
            parseCubeGroup(group);
        }
        updateDefaultPose();
    }

    private void parseCubeGroup(TabulaCubeGroupContainer group) {
        for (TabulaCubeContainer cube : group.getCubes()) {
            parseCube(cube, null);
        }
        for (TabulaCubeGroupContainer child : group.getCubeGroups()) {
            parseCubeGroup(child);
        }
    }

    private void parseCube(TabulaCubeContainer cube, AdvancedModelBox parent) {
        AdvancedModelBox box = createBox(cube);
        cubes.put(cube.getName(), box);
        identifierMap.put(cube.getIdentifier(), box);
        allBoxes.add(box);
        if (parent == null) {
            rootBoxes.add(box);
        } else {
            parent.addChild(box);
        }
        for (TabulaCubeContainer child : cube.getChildren()) {
            parseCube(child, box);
        }
    }

    private AdvancedModelBox createBox(TabulaCubeContainer cube) {
        int[] textureOffset = cube.getTextureOffset();
        double[] position = cube.getPosition();
        double[] rotation = cube.getRotation();
        double[] offset = cube.getOffset();
        int[] dimensions = cube.getDimensions();
        AdvancedModelBox box = new AdvancedModelBox(this, cube.getName());
        box.setTextureOffset(textureOffset[0], textureOffset[1]);
        box.mirror = cube.isTextureMirrorEnabled();
        box.setPos((float) position[0], (float) position[1], (float) position[2]);
        // Tabula offset is local cube geometry, not AdvancedModelBox's runtime translation.
        box.addBox((float) offset[0], (float) offset[1], (float) offset[2],
                dimensions[0], dimensions[1], dimensions[2], 0);
        box.rotateAngleX = (float) Math.toRadians(rotation[0]);
        box.rotateAngleY = (float) Math.toRadians(rotation[1]);
        box.rotateAngleZ = (float) Math.toRadians(rotation[2]);
        return box;
    }

    @Override
    public void setupAnim(S state) {
        super.setupAnim(state);
        if (tabulaAnimator != null) {
            tabulaAnimator.setRotationAngles(this, state);
        }
    }

    /** Last box of this name in construction order, or null if absent. */
    public AdvancedModelBox getCube(String name) { return cubes.get(name); }

    /** Last box of this identifier in construction order, or null if absent. */
    public AdvancedModelBox getCubeByIdentifier(String identifier) { return identifierMap.get(identifier); }

    public Map<String, AdvancedModelBox> getCubes() { return cubeView; }

    public List<BasicModelPart> getRootBoxes() { return rootView; }

    @Override
    public Iterable<BasicModelPart> parts() { return rootView; }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() { return allView; }
}
