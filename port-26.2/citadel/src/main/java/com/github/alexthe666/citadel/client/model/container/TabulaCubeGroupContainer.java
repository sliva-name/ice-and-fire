package com.github.alexthe666.citadel.client.model.container;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gegy1000
 * @since 1.0.0
 * @see <a href="https://github.com/AlexModGuy/Citadel/tree/8018e44d8b569913ca828f31aa6c86163319e7a5">Pinned Citadel source</a>
 */
public class TabulaCubeGroupContainer {
    private String name;
    private String identifier;
    private List<TabulaCubeContainer> cubes = new ArrayList<>();
    private List<TabulaCubeGroupContainer> cubeGroups = new ArrayList<>();
    private boolean txMirror;
    private boolean hidden;

    public String getName() { return this.name; }
    public String getIdentifier() { return this.identifier; }
    public List<TabulaCubeContainer> getCubes() { return this.cubes; }
    public List<TabulaCubeGroupContainer> getCubeGroups() { return this.cubeGroups; }
    public boolean isTextureMirrorEnabled() { return this.txMirror; }
    public boolean isHidden() { return this.hidden; }
}
