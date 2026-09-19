package com.github.alexthe666.citadel.client.model;

import com.github.alexthe666.citadel.client.model.container.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Plain-main test, no Minecraft, Forge or JUnit required. Run with this subset
 * and real Gson on the classpath; arguments are resource roots to scan recursively.
 * Include original src/main/resources on the classpath to test resource loading.
 * Assertions are explicit, so -ea is not required. No files are created or changed.
 */
public final class TabulaAssetsTest {
    private static final TabulaModelHandler HANDLER = TabulaModelHandler.INSTANCE;
    private static final Gson GSON = new Gson();

    private TabulaAssetsTest() {
    }

    public static void main(String[] args) throws Exception {
        check(args.length > 0, "Pass at least one resource directory");
        testContainers();
        testStreamsAndFailures();
        testJsonUtils();
        int assets = 0;
        int cubes = 0;
        for (String arg : args) {
            Path root = Path.of(arg);
            check(Files.isDirectory(root), "Not a resource directory: " + root);
            List<Path> paths;
            try (var files = Files.walk(root)) {
                paths = files.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".tbl")).sorted().toList();
            }
            for (Path path : paths) {
                try {
                    cubes += testAsset(path);
                    assets++;
                } catch (Exception | AssertionError failure) {
                    throw new AssertionError("Asset failed: " + path, failure);
                }
            }
        }
        check(assets > 0, "No .tbl assets found; refusing a vacuous pass");
        String resource = "assets/iceandfire/models/tabula/firedragon/firedragon_Ground";
        TabulaModelContainer relative = HANDLER.loadTabulaModel(resource);
        TabulaModelContainer absolute = HANDLER.loadTabulaModel("/" + resource + ".tbl");
        check(GSON.toJsonTree(relative).equals(GSON.toJsonTree(absolute)), "Classpath normalization");
        check(HANDLER.getCubeByName("HeadFront", relative) != null, "IAF eye-layer cube lookup");
        System.out.println("PASS: " + assets + " .tbl assets, " + cubes
                + " cubes; JSON/ZIP, classpath, hierarchy, animation DTOs, defaults, JsonUtils and failure/lifecycle tests");
    }

    private static int testAsset(Path path) throws Exception {
        TabulaModelContainer archive = HANDLER.loadTabulaModelArchive(path.toString(), Files.newInputStream(path));
        JsonObject raw;
        TabulaModelContainer json;
        try (ZipFile zip = new ZipFile(path.toFile(), StandardCharsets.UTF_8)) {
            ZipEntry entry = zip.getEntry("model.json");
            check(entry != null, "Missing root model.json");
            byte[] bytes;
            try (InputStream input = zip.getInputStream(entry)) {
                bytes = input.readAllBytes();
            }
            raw = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            json = HANDLER.loadTabulaModel(new ByteArrayInputStream(bytes));
        }
        JsonElement serialized = GSON.toJsonTree(archive);
        check(serialized.equals(GSON.toJsonTree(json)), "ZIP and raw JSON disagree");
        comparePresentFields(raw, serialized, "model");
        check(archive.getTextureWidth() > 0 && archive.getTextureHeight() > 0, "Texture size");
        check(archive.getScale().length == 3, "Model scale");
        List<String> expected = new ArrayList<>();
        collectModel(raw, expected);
        List<TabulaCubeContainer> actual = HANDLER.getAllCubes(archive);
        check(!actual.isEmpty(), "No cubes");
        check(expected.equals(actual.stream().map(TabulaCubeContainer::getIdentifier).toList()), "Traversal order/coverage");
        for (TabulaCubeContainer cube : actual) {
            check(cube.getDimensions().length == 3, "Cube dimensions");
            check(cube.getPosition().length == 3 && cube.getRotation().length == 3, "Cube pose");
            check(cube.getOffset().length == 3 && cube.getScale().length == 3, "Cube offset/scale");
            check(cube.getTextureOffset().length == 2, "Cube UV");
            TabulaCubeContainer byName = actual.stream()
                    .filter(c -> java.util.Objects.equals(c.getName(), cube.getName())).findFirst().orElseThrow();
            TabulaCubeContainer byId = actual.stream()
                    .filter(c -> java.util.Objects.equals(c.getIdentifier(), cube.getIdentifier())).findFirst().orElseThrow();
            check(HANDLER.getCubeByName(cube.getName(), archive) == byName, "Name lookup");
            check(HANDLER.getCubeByIdentifier(cube.getIdentifier(), archive) == byId, "Identifier lookup");
        }
        return actual.size();
    }

    // Check every serialized DTO field present in the source, including nested
    // arrays/maps; unrelated Tabula editor metadata is deliberately not a DTO field.
    private static void comparePresentFields(JsonElement raw, JsonElement actual, String path) {
        if (raw.isJsonObject() && actual.isJsonObject()) {
            for (Map.Entry<String, JsonElement> field : actual.getAsJsonObject().entrySet()) {
                if (raw.getAsJsonObject().has(field.getKey())) {
                    comparePresentFields(raw.getAsJsonObject().get(field.getKey()), field.getValue(), path + "." + field.getKey());
                }
            }
        } else if (raw.isJsonArray() && actual.isJsonArray()) {
            check(raw.getAsJsonArray().size() == actual.getAsJsonArray().size(), path + " length");
            for (int i = 0; i < raw.getAsJsonArray().size(); i++) {
                comparePresentFields(raw.getAsJsonArray().get(i), actual.getAsJsonArray().get(i), path + "[" + i + "]");
            }
        } else {
            check(raw.equals(actual), path + " value: " + raw + " != " + actual);
        }
    }

    private static void collectModel(JsonObject model, List<String> result) {
        if (model.has("cubeGroups")) {
            for (JsonElement group : model.getAsJsonArray("cubeGroups")) {
                collectGroup(group.getAsJsonObject(), result);
            }
        }
        collectCubes(model, "cubes", result);
    }

    private static void collectGroup(JsonObject group, List<String> result) {
        collectCubes(group, "cubes", result);
        if (group.has("cubeGroups")) {
            for (JsonElement child : group.getAsJsonArray("cubeGroups")) {
                collectGroup(child.getAsJsonObject(), result);
            }
        }
    }

    private static void collectCubes(JsonObject owner, String field, List<String> result) {
        if (owner.has(field)) {
            for (JsonElement element : owner.getAsJsonArray(field)) {
                JsonObject cube = element.getAsJsonObject();
                result.add(cube.has("identifier") ? cube.get("identifier").getAsString() : null);
                collectCubes(cube, "children", result);
            }
        }
    }

    private static void testContainers() {
        String fixture = """
                {"modelName":"Drágon 雪","authorName":"author","projVersion":4,
                 "metadata":["credit"],"scale":[2,3,4],"textureWidth":128,"textureHeight":64,"cubeCount":4,
                 "cubeGroups":[{"name":"group","identifier":"g","txMirror":true,"hidden":true,
                   "cubes":[{"name":"parent","identifier":"p","parentIdentifier":"outside",
                     "dimensions":[1,2,3],"position":[4,5,6],"offset":[7,8,9],"rotation":[10,11,12],
                     "scale":[1,2,3],"txOffset":[13,14],"txMirror":true,"mcScale":0.25,"opacity":75,"hidden":true,
                     "children":[{"name":"child","identifier":"c","parentIdentifier":"p"}]}],
                   "cubeGroups":[{"name":"nested","identifier":"n","cubes":[{"name":"leaf","identifier":"l"}]}]}],
                 "cubes":[{"name":"root","identifier":"r"}],
                 "anims":[{"name":"walk","identifier":"a","loops":true,"sets":{"p":[
                   {"name":"step","identifier":"s","startKey":2,"length":5,
                    "posChange":[1,2,3],"rotChange":[4,5,6],"scaleChange":[7,8,9],"opacityChange":10,
                    "posOffset":[11,12,13],"rotOffset":[14,15,16],"scaleOffset":[17,18,19],"opacityOffset":20,
                    "progressionCoords":[[0,0],[0.5,0.75],[1,1]],"hidden":true}]}}]}
                """;
        TabulaModelContainer model = parse(fixture);
        check(model.getName().equals("Drágon 雪") && model.getAuthor().equals("author"), "UTF-8 metadata");
        check(model.getProjectVersion() == 4 && model.getMetadata()[0].equals("credit"), "Version/metadata");
        check(model.getCubeCount() == 4 && Arrays.equals(model.getScale(), new double[]{2, 3, 4}), "Model values");
        TabulaCubeGroupContainer group = model.getCubeGroups().getFirst();
        check(group.getName().equals("group") && group.getIdentifier().equals("g"), "Group identity");
        check(group.isHidden() && group.isTextureMirrorEnabled(), "Group flags");
        check(group.getCubeGroups().getFirst().getIdentifier().equals("n"), "Nested groups");
        TabulaCubeContainer cube = group.getCubes().getFirst();
        check(cube.getParentIdentifier().equals("outside"), "Parent identifier must not be rewritten");
        check(cube.isHidden() && cube.isTextureMirrorEnabled(), "Cube flags");
        check(cube.getMCScale() == 0.25 && cube.getOpacity() == 75, "Cube scalar values");
        check(Arrays.equals(cube.getDimensions(), new int[]{1, 2, 3}), "Dimensions getter");
        check(Arrays.equals(cube.getPosition(), new double[]{4, 5, 6}), "Position getter");
        check(Arrays.equals(cube.getOffset(), new double[]{7, 8, 9}), "Offset getter");
        check(Arrays.equals(cube.getRotation(), new double[]{10, 11, 12}), "Rotation getter");
        check(Arrays.equals(cube.getScale(), new double[]{1, 2, 3}), "Scale getter");
        check(Arrays.equals(cube.getTextureOffset(), new int[]{13, 14}), "UV getter");
        check(HANDLER.getAllCubes(model).stream().map(TabulaCubeContainer::getIdentifier).toList()
                .equals(List.of("p", "c", "l", "r")), "Group/cube traversal order");
        check(HANDLER.getCubeByIdentifier("c", model) == cube.getChildren().getFirst(), "Child lookup");
        check(HANDLER.getCubeByName("absent", model) == null, "Missing lookup");
        HANDLER.getAllCubes(model).clear();
        check(HANDLER.getAllCubes(model).size() == 4, "Traversal must return a fresh list");
        TabulaAnimationContainer animation = model.getAnimations().getFirst();
        check(animation.getName().equals("walk") && animation.getIdentifier().equals("a") && animation.doesLoop(), "Animation");
        TabulaAnimationComponentContainer component = animation.getComponents().get("p").getFirst();
        check(component.getName().equals("step") && component.getIdentifier().equals("s"), "Component identity");
        check(component.getStartKey() == 2 && component.getLength() == 5 && component.getEndKey() == 7, "Key range");
        check(Arrays.equals(component.getPositionChange(), new double[]{1, 2, 3}), "Position change");
        check(Arrays.equals(component.getRotationChange(), new double[]{4, 5, 6}), "Rotation change");
        check(Arrays.equals(component.getScaleChange(), new double[]{7, 8, 9}), "Scale change");
        check(Arrays.equals(component.getPositionOffset(), new double[]{11, 12, 13}), "Position offset");
        check(Arrays.equals(component.getRotationOffset(), new double[]{14, 15, 16}), "Rotation offset");
        check(Arrays.equals(component.getScaleOffset(), new double[]{17, 18, 19}), "Scale offset");
        check(component.getOpacityChange() == 10 && component.getOpacityOffset() == 20 && component.isHidden(), "Opacity/hidden");
        check(Arrays.equals(component.getProgressionCoords().get(1), new double[]{0.5, 0.75}), "Progression coordinates");
        comparePresentFields(JsonParser.parseString(fixture), GSON.toJsonTree(model), "fixture");
        TabulaModelContainer defaults = parse("{}");
        check(defaults.getScale()[0] == 1 && defaults.getCubeGroups().isEmpty()
                && defaults.getCubes().isEmpty() && defaults.getAnimations().isEmpty(), "Model defaults");
        TabulaCubeContainer child = cube.getChildren().getFirst();
        check(child.getChildren().isEmpty() && child.getOpacity() == 100 && child.getMCScale() == 1, "Cube defaults");
        TabulaCubeContainer constructed = new TabulaCubeContainer("new", "new-id", "parent-id", new int[]{1, 2, 3},
                new double[3], new double[3], new double[3], new double[]{1, 1, 1}, new int[]{4, 5}, true, 50, 0.5, true);
        constructed.getChildren().add(child);
        TabulaModelContainer constructedModel = new TabulaModelContainer("model", "author", 32, 16, List.of(constructed), 4);
        check(constructedModel.getCubeCount() == 2, "Constructor recursively counts children");
        check(constructed.getParentIdentifier().equals("parent-id") && constructed.getTextureOffset()[1] == 5
                && constructed.isTextureMirrorEnabled() && constructed.getOpacity() == 50
                && constructed.getMCScale() == 0.5 && constructed.isHidden(), "Public cube constructor");
        TextureOffset offset = new TextureOffset(5, 9);
        check(offset.textureOffsetX == 5 && offset.textureOffsetY == 9, "TextureOffset");
    }

    private static void testStreamsAndFailures() throws Exception {
        TrackingStream json = new TrackingStream("{\"modelName\":\"雪\"}".getBytes(StandardCharsets.UTF_8));
        check(HANDLER.loadTabulaModel(json).getName().equals("雪"), "Raw UTF-8 JSON");
        check(!json.closed, "Raw JSON caller retains stream ownership");
        byte[] archive = zip("model.json", "{\"modelName\":\"雪\"}");
        TrackingStream owned = new TrackingStream(archive);
        check(HANDLER.loadTabulaModelArchive("fixture.tbl", owned).getName().equals("雪"), "Archive parse");
        check(owned.closed, "Archive closes input");
        try (ZipInputStream positioned = new ZipInputStream(new ByteArrayInputStream(archive))) {
            positioned.getNextEntry();
            positioned.getNextEntry();
            check(HANDLER.loadTabulaModel(positioned).getName().equals("雪"), "Original IAF helper's positioned ZIP API");
        }
        for (byte[] invalid : List.of(zip("other.json", "{}"), zip("folder/model.json", "{}"),
                zip("model.json", "{"), zip("model.json", "null"), zip("model.json", "[]"),
                new byte[]{1, 2, 3}, Arrays.copyOf(archive, 40))) {
            TrackingStream input = new TrackingStream(invalid);
            IOException failure = expect(IOException.class, () -> HANDLER.loadTabulaModelArchive("broken.tbl", input));
            check(failure.getMessage().contains("broken.tbl"), "Archive error includes name");
            check(input.closed, "Failure closes archive input");
        }
        for (String invalid : List.of("", "null", "{", "[]", "{} {}")) {
            expect(JsonParseException.class, () -> parse(invalid));
        }
        expect(IOException.class, () -> HANDLER.loadTabulaModel("missing/tabula-test-resource"));
        expect(IOException.class, () -> HANDLER.loadTabulaModelArchive("missing.tbl", null));
    }

    private static void testJsonUtils() {
        JsonObject json = JsonParser.parseString("{\"s\":\"text\",\"n\":3,\"b\":true,\"a\":[1,2],\"o\":{},\"nil\":null}").getAsJsonObject();
        check(JsonUtils.isString(json, "s") && JsonUtils.isString(json.get("s")), "String predicates");
        check(JsonUtils.isNumber(json.get("n")) && JsonUtils.isBoolean(json, "b"), "Primitive predicates");
        check(JsonUtils.isJsonArray(json, "a") && JsonUtils.isJsonPrimitive(json, "n"), "Array/primitive predicates");
        check(!JsonUtils.hasField(null, "s") && JsonUtils.hasField(json, "nil"), "Field presence");
        check(JsonUtils.getString(json, "s").equals("text") && JsonUtils.getString(json, "missing", "fallback").equals("fallback"), "String getter");
        check(JsonUtils.getInt(json, "n") == 3 && JsonUtils.getInt(json, "missing", 7) == 7, "Integer getter");
        check(JsonUtils.getFloat(json, "n") == 3 && JsonUtils.getFloat(json, "missing", 7.5F) == 7.5F, "Float getter");
        check(JsonUtils.getBoolean(json, "b") && !JsonUtils.getBoolean(json, "missing", false), "Boolean getter");
        check(JsonUtils.getJsonObject(json, "o").isEmpty() && JsonUtils.getJsonObject(json, "missing", null) == null, "Object getter");
        check(JsonUtils.getJsonArray(json, "a").size() == 2 && JsonUtils.getJsonArray(json, "missing", null) == null, "Array getter");
        check(JsonUtils.deserializeClass(json, "n", HANDLER, Integer.class) == 3, "Context deserialize");
        check(JsonUtils.deserializeClass(json, "missing", 9, HANDLER, Integer.class) == 9, "Context fallback");
        check(JsonUtils.toString(null).equals("null (missing)") && JsonUtils.toString(json.get("nil")).equals("null (json)"), "Null descriptions");
        check(JsonUtils.toString(JsonParser.parseString("[123456789012345]")).equals("an array ([123...45])"), "Abbreviation");
        Type type = new TypeToken<List<Integer>>() { }.getType();
        List<Integer> list = JsonUtils.fromJson(GSON, "[1,2]", type, false);
        check(list.equals(List.of(1, 2)), "Generic fromJson");
        check(JsonUtils.fromJson(GSON, new StringReader("[1,2]"), type).equals(list), "Reader Type overload");
        check(JsonUtils.gsonDeserialize(GSON, "[1,2]", type).equals(list), "String Type overload");
        check(JsonUtils.fromJson(GSON, new StringReader("3"), Integer.class) == 3, "Reader Class overload");
        check(JsonUtils.gsonDeserialize(GSON, "3", Integer.class) == 3, "String Class overload");
        check(JsonUtils.gsonDeserialize(GSON, "{unquoted:3}", JsonObject.class, true).get("unquoted").getAsInt() == 3, "Lenient helper");
        expect(JsonSyntaxException.class, () -> JsonUtils.getInt(json, "s"));
        expect(JsonSyntaxException.class, () -> JsonUtils.getString(json, "missing"));
        expect(JsonSyntaxException.class, () -> JsonUtils.getJsonArray(json, "n"));
        expect(JsonSyntaxException.class, () -> JsonUtils.deserializeClass(json, "missing", HANDLER, Integer.class));
    }

    private static TabulaModelContainer parse(String json) {
        return HANDLER.loadTabulaModel(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private static byte[] zip(String entry, String json) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes, StandardCharsets.UTF_8)) {
            zip.putNextEntry(new ZipEntry("texture.png"));
            zip.write(new byte[]{1, 2, 3});
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry(entry));
            zip.write(json.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        return bytes.toByteArray();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static <T extends Throwable> T expect(Class<T> type, ThrowingAction action) {
        try {
            action.run();
        } catch (Throwable failure) {
            if (type.isInstance(failure)) {
                return type.cast(failure);
            }
            throw new AssertionError("Expected " + type.getName() + ", got " + failure, failure);
        }
        throw new AssertionError("Expected " + type.getName());
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    private static final class TrackingStream extends ByteArrayInputStream {
        private boolean closed;

        private TrackingStream(byte[] bytes) {
            super(bytes);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
