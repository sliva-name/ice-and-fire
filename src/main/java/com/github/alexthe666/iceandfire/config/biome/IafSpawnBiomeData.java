package com.github.alexthe666.iceandfire.config.biome;


import com.github.alexthe666.iceandfire.IceAndFire;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

// Port blocker: BIOME_CATEGORY/BIOME_DICT have no equivalent API in 26.1.
// Their matching branches must not silently become false or guessed tag mappings.
public class IafSpawnBiomeData extends com.github.alexthe666.citadel.config.biome.SpawnBiomeData {

    private List<List<SpawnBiomeEntry>> biomes = new ArrayList<>();
    private static final int CITADEL_FORMAT = 0;
    private static final String CITADEL_FORMAT_STRING = "citadel_format";
    private int citadelFormat = CITADEL_FORMAT;

    public IafSpawnBiomeData() {
    }

    public void setCitadelFormat(int format) {
        citadelFormat = format;
    }

    private IafSpawnBiomeData(SpawnBiomeEntry[][] biomesRead) {
        biomes = new ArrayList<>();
        for (SpawnBiomeEntry[] innerArray : biomesRead) {
            biomes.add(Arrays.asList(innerArray));
        }
    }

    public IafSpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
        if (biomes.isEmpty() || biomes.size() < pool + 1) {
            biomes.add(new ArrayList<>());
        }
        biomes.get(pool).add(new SpawnBiomeEntry(type, negate, value));
        return this;
    }

    @Deprecated
    public boolean matches(Biome biomeIn) {
        return this.matches((Holder<Biome>) null, Identifier.parse(String.valueOf(biomeIn)));
    }

    public boolean matches(String category, Identifier registryName) {
        for (List<SpawnBiomeEntry> all : biomes) {
            boolean overall = true;
            for (SpawnBiomeEntry cond : all) {
                if (!cond.matches(category, registryName)) {
                    overall = false;
                }
            }
            if (overall) {
                return true;
            }
        }
        return false;
    }

    public boolean matches(@Nullable Holder<Biome> biomeHolder, Identifier registryName) {
        for (List<SpawnBiomeEntry> all : biomes) {
            boolean overall = true;
            for (SpawnBiomeEntry cond : all) {
                if (!cond.matches(biomeHolder, registryName)) {
                    overall = false;
                }
            }
            if (overall) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<IafSpawnBiomeData>, JsonSerializer<IafSpawnBiomeData> {

        @Override
        public IafSpawnBiomeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            int citadelFormat = GsonHelper.getAsInt(jsonobject, CITADEL_FORMAT_STRING, -1);

            if (citadelFormat != IafSpawnBiomeData.CITADEL_FORMAT) {
                throw new InvalidCitadelFormatException("The file contained the %s format version, while we expected a %s format version".formatted(citadelFormat, CITADEL_FORMAT));
            }
            SpawnBiomeEntry[][] biomesRead = GsonHelper.getAsObject(jsonobject, "biomes", new SpawnBiomeEntry[0][0], context, SpawnBiomeEntry[][].class);
            return new IafSpawnBiomeData(biomesRead);
        }

        @Override
        public JsonElement serialize(IafSpawnBiomeData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonobject = new JsonObject();
            jsonobject.add(CITADEL_FORMAT_STRING, context.serialize(src.citadelFormat));
            jsonobject.add("biomes", context.serialize(src.biomes));
            return jsonobject;
        }
    }

    private class SpawnBiomeEntry {
        BiomeEntryType type;
        boolean negate;
        String value;

        public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
            this.type = type;
            this.negate = remove;
            this.value = value;
        }

        public boolean matches(@Nullable Holder<Biome> biomeHolder, Identifier registryName) {
            if(type.isDepreciated()){
                // TODO: 1.19
                IceAndFire.LOGGER.debug("biome config: BIOME_DICT is no longer fully supported in 1.18+. They will only work for structures.");
                return false;
            }else{
                if(type == BiomeEntryType.BIOME_TAG){
                    if (biomeHolder != null) {
                        TagKey<Biome> tag = TagKey.create(Registries.BIOME, Identifier.parse(value));
                        boolean hasTag = biomeHolder.is(tag) || biomeHolder.tags().anyMatch(
                            biomeTagKey -> biomeTagKey.location() != null && biomeTagKey.location().toString().equals(value));
                        return hasTag ? !negate : negate;
                    }
                    return negate;
                }
                else if (type == BiomeEntryType.BIOME_CATEGORY)
                {
                    if (matchesCategoryTag(biomeHolder, this.value)) {
                        return !this.negate;
                    } else {
                        return this.negate;
                    }
                }
                else {
                    if (registryName.toString().equals(value)) {
                        return !negate;
                    }
                    return negate;
                }
            }
        }
        public boolean matches(String category, Identifier registryName) {
            if (this.type == BiomeEntryType.BIOME_DICT) {
                return this.negate;
            }
            else if (this.type == BiomeEntryType.BIOME_CATEGORY) {
                if (category != null && category.toLowerCase(Locale.ROOT).equals(this.value)) {
                    return !this.negate;
                } else {
                    return this.negate;
                }
            } else if (registryName.toString().equals(this.value)) {
                return !this.negate;
            } else {
                return this.negate;
            }
        }

        private static boolean matchesCategoryTag(Holder<Biome> biomeHolder, String category) {
            TagKey<Biome> tag = switch (category.toLowerCase(Locale.ROOT)) {
                case "forest" -> BiomeTags.IS_FOREST;
                case "icy" -> net.minecraftforge.common.Tags.Biomes.IS_SNOWY;
                case "taiga" -> BiomeTags.IS_TAIGA;
                case "jungle" -> BiomeTags.IS_JUNGLE;
                case "mesa" -> BiomeTags.IS_BADLANDS;
                case "savanna" -> BiomeTags.IS_SAVANNA;
                case "ocean" -> BiomeTags.IS_OCEAN;
                case "river" -> BiomeTags.IS_RIVER;
                case "beach" -> BiomeTags.IS_BEACH;
                case "nether" -> BiomeTags.IS_NETHER;
                case "the_end", "end" -> BiomeTags.IS_END;
                case "mountain" -> BiomeTags.IS_MOUNTAIN;
                default -> null;
            };
            return tag != null && biomeHolder.is(tag);
        }
    }

    static class InvalidCitadelFormatException extends JsonParseException {
        InvalidCitadelFormatException(String s) { super(s);}
    }
}