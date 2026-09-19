package com.github.alexthe666.citadel.client.model.container;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * Standalone JSON helpers from Citadel commit
 * 8018e44d8b569913ca828f31aa6c86163319e7a5. Minecraft item registry helpers
 * and side/nullability annotations are intentionally excluded.
 */
public class JsonUtils {
    public static boolean isString(JsonObject json, String memberName) {
        return isJsonPrimitive(json, memberName) && json.getAsJsonPrimitive(memberName).isString();
    }

    public static boolean isString(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isString();
    }

    public static boolean isNumber(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber();
    }

    public static boolean isBoolean(JsonObject json, String memberName) {
        return isJsonPrimitive(json, memberName) && json.getAsJsonPrimitive(memberName).isBoolean();
    }

    public static boolean isJsonArray(JsonObject json, String memberName) {
        return hasField(json, memberName) && json.get(memberName).isJsonArray();
    }

    public static boolean isJsonPrimitive(JsonObject json, String memberName) {
        return hasField(json, memberName) && json.get(memberName).isJsonPrimitive();
    }

    public static boolean hasField(JsonObject json, String memberName) {
        return json != null && json.get(memberName) != null;
    }

    public static String getString(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsString();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + toString(json));
    }

    public static String getString(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getString(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
    }

    public static String getString(JsonObject json, String memberName, String fallback) {
        return json.has(memberName) ? getString(json.get(memberName), memberName) : fallback;
    }

    public static boolean getBoolean(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + toString(json));
    }

    public static boolean getBoolean(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getBoolean(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
    }

    public static boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
        return json.has(memberName) ? getBoolean(json.get(memberName), memberName) : fallback;
    }

    public static float getFloat(JsonElement json, String memberName) {
        if (isNumber(json)) {
            return json.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + toString(json));
    }

    public static float getFloat(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getFloat(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Float");
    }

    public static float getFloat(JsonObject json, String memberName, float fallback) {
        return json.has(memberName) ? getFloat(json.get(memberName), memberName) : fallback;
    }

    public static int getInt(JsonElement json, String memberName) {
        if (isNumber(json)) {
            return json.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + toString(json));
    }

    public static int getInt(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getInt(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
    }

    public static int getInt(JsonObject json, String memberName, int fallback) {
        return json.has(memberName) ? getInt(json.get(memberName), memberName) : fallback;
    }

    public static JsonObject getJsonObject(JsonElement json, String memberName) {
        if (json.isJsonObject()) {
            return json.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + toString(json));
    }

    public static JsonObject getJsonObject(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getJsonObject(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject");
    }

    public static JsonObject getJsonObject(JsonObject json, String memberName, JsonObject fallback) {
        return json.has(memberName) ? getJsonObject(json.get(memberName), memberName) : fallback;
    }

    public static JsonArray getJsonArray(JsonElement json, String memberName) {
        if (json.isJsonArray()) {
            return json.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + toString(json));
    }

    public static JsonArray getJsonArray(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return getJsonArray(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
    }

    public static JsonArray getJsonArray(JsonObject json, String memberName, JsonArray fallback) {
        return json.has(memberName) ? getJsonArray(json.get(memberName), memberName) : fallback;
    }

    public static <T> T deserializeClass(JsonElement json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
        if (json != null) {
            return context.deserialize(json, adapter);
        }
        throw new JsonSyntaxException("Missing " + memberName);
    }

    public static <T> T deserializeClass(JsonObject json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
        if (json.has(memberName)) {
            return deserializeClass(json.get(memberName), memberName, context, adapter);
        }
        throw new JsonSyntaxException("Missing " + memberName);
    }

    public static <T> T deserializeClass(JsonObject json, String memberName, T fallback, JsonDeserializationContext context, Class<? extends T> adapter) {
        return json.has(memberName) ? deserializeClass(json.get(memberName), memberName, context, adapter) : fallback;
    }

    public static String toString(JsonElement json) {
        String text = String.valueOf(json);
        // Equivalent to StringUtils.abbreviateMiddle(text, "...", 10).
        if (text.length() > 10) {
            text = text.substring(0, 4) + "..." + text.substring(text.length() - 3);
        }
        if (json == null) {
            return "null (missing)";
        }
        if (json.isJsonNull()) {
            return "null (json)";
        }
        if (json.isJsonArray()) {
            return "an array (" + text + ")";
        }
        if (json.isJsonObject()) {
            return "an object (" + text + ")";
        }
        if (json.isJsonPrimitive()) {
            if (json.getAsJsonPrimitive().isNumber()) {
                return "a number (" + text + ")";
            }
            if (json.getAsJsonPrimitive().isBoolean()) {
                return "a boolean (" + text + ")";
            }
        }
        return text;
    }

    public static <T> T gsonDeserialize(Gson gson, Reader reader, Class<T> adapter, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return gson.getAdapter(adapter).read(jsonReader);
        } catch (IOException exception) {
            throw new JsonParseException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(Gson gson, Reader reader, Type type, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return (T) gson.getAdapter(TypeToken.get(type)).read(jsonReader);
        } catch (IOException exception) {
            throw new JsonParseException(exception);
        }
    }

    public static <T> T fromJson(Gson gson, String json, Type type, boolean lenient) {
        return fromJson(gson, new StringReader(json), type, lenient);
    }

    public static <T> T gsonDeserialize(Gson gson, String json, Class<T> adapter, boolean lenient) {
        return gsonDeserialize(gson, new StringReader(json), adapter, lenient);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Type type) {
        return fromJson(gson, reader, type, false);
    }

    public static <T> T gsonDeserialize(Gson gson, String json, Type type) {
        return fromJson(gson, json, type, false);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Class<T> adapter) {
        return gsonDeserialize(gson, reader, adapter, false);
    }

    public static <T> T gsonDeserialize(Gson gson, String json, Class<T> adapter) {
        return gsonDeserialize(gson, json, adapter, false);
    }
}
