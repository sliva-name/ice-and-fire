package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.container.TabulaCubeContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaCubeGroupContainer;
import com.github.alexthe666.citadel.client.model.container.TabulaModelContainer;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Standalone entity-model subset of Citadel's Tabula loader, pinned to
 * 8018e44d8b569913ca828f31aa6c86163319e7a5. No block-model or renderer dependencies.
 *
 * @author pau101
 * @since 1.0.0
 */
public enum TabulaModelHandler implements JsonDeserializationContext {
    INSTANCE;

    private final Gson gson = new Gson();

    /**
     * Loads a classpath .tbl resource, accepting an optional leading slash and
     * optional .tbl suffix. The opened resource is always closed.
     *
     * @throws IOException if the resource is missing, unreadable or invalid
     */
    public TabulaModelContainer loadTabulaModel(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith(".tbl")) {
            path += ".tbl";
        }
        InputStream stream = TabulaModelHandler.class.getResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException("Tabula model resource not found: " + path);
        }
        return this.loadTabulaModelArchive(path, stream);
    }

    /**
     * Loads UTF-8 model.json, not a ZIP archive. The caller owns the stream;
     * this also accepts a ZipInputStream already positioned at model.json,
     * as used by Ice and Fire's TabulaModelHandlerHelper.
     *
     * @throws JsonParseException if the JSON cannot be read as a model
     */
    public TabulaModelContainer loadTabulaModel(InputStream stream) {
        Objects.requireNonNull(stream, "model.json stream");
        TabulaModelContainer model = this.gson.fromJson(
                new InputStreamReader(stream, StandardCharsets.UTF_8), TabulaModelContainer.class);
        if (model == null) {
            throw new JsonSyntaxException("Expected a Tabula model object, found empty or null JSON");
        }
        return model;
    }

    /**
     * Loads the root model.json entry of a .tbl ZIP. Other entries (including
     * textures) are skipped, never extracted. Takes ownership of the stream
     * and closes it on success or failure. The name is used in diagnostics.
     *
     * @throws IOException if the archive or its model.json is missing or invalid
     */
    public TabulaModelContainer loadTabulaModelArchive(String name, InputStream stream) throws IOException {
        if (stream == null) {
            throw new FileNotFoundException("Tabula model archive not found: " + name);
        }
        try (ZipInputStream zip = new ZipInputStream(stream, StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory() && "model.json".equals(entry.getName())) {
                    TabulaModelContainer model = this.loadTabulaModel(zip);
                    // Consume the entry so ZIP CRC/truncation errors are reported.
                    zip.closeEntry();
                    return model;
                }
            }
            throw new IOException("No model.json present");
        } catch (IOException | JsonParseException exception) {
            throw new IOException("Failed to load Tabula archive " + name + ": " + exception.getMessage(), exception);
        }
    }

    public TabulaCubeContainer getCubeByName(String name, TabulaModelContainer model) {
        for (TabulaCubeContainer cube : this.getAllCubes(model)) {
            if (Objects.equals(cube.getName(), name)) {
                return cube;
            }
        }
        return null;
    }

    public TabulaCubeContainer getCubeByIdentifier(String identifier, TabulaModelContainer model) {
        for (TabulaCubeContainer cube : this.getAllCubes(model)) {
            if (Objects.equals(cube.getIdentifier(), identifier)) {
                return cube;
            }
        }
        return null;
    }

    /** Returns a fresh list in upstream order: groups first, then root cube trees. */
    public List<TabulaCubeContainer> getAllCubes(TabulaModelContainer model) {
        List<TabulaCubeContainer> cubes = new ArrayList<>();
        for (TabulaCubeGroupContainer group : model.getCubeGroups()) {
            this.traverse(group, cubes);
        }
        for (TabulaCubeContainer cube : model.getCubes()) {
            this.traverse(cube, cubes);
        }
        return cubes;
    }

    private void traverse(TabulaCubeGroupContainer group, List<TabulaCubeContainer> cubes) {
        for (TabulaCubeContainer cube : group.getCubes()) {
            this.traverse(cube, cubes);
        }
        for (TabulaCubeGroupContainer child : group.getCubeGroups()) {
            this.traverse(child, cubes);
        }
    }

    private void traverse(TabulaCubeContainer cube, List<TabulaCubeContainer> cubes) {
        cubes.add(cube);
        for (TabulaCubeContainer child : cube.getChildren()) {
            this.traverse(child, cubes);
        }
    }

    @Override
    public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
        return this.gson.fromJson(json, type);
    }
}
