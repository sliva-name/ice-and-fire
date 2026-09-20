package com.github.alexthe666.iceandfire.datagen;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class DataGenerators {
    private static final PackLocationInfo LOCATION = new PackLocationInfo("iceandfire:data",
        Component.literal("Dynamically generated tags"), PackSource.DEFAULT, Optional.empty());
    static final PackResources resources = new PackResources();

    static {
        AddPackFindersEvent.BUS.addListener(DataGenerators::addPackFinders);
    }

    public static void addPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            // Port blocker: this event does not supply a loaded biome registry/tag lookup.
            // The legacy early-registry workaround cannot evaluate datapack biome tags reliably.
            createResources(ForgeRegistries.BIOMES.getKeys().stream().map(ForgeRegistries.BIOMES::getHolder)
                .flatMap(Optional::stream));
            event.addRepositorySource(packConsumer -> {
                Pack pack = Pack.readMetaAndCreate(LOCATION, new Pack.ResourcesSupplier() {
                    @Override
                    public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo location) {
                        return resources;
                    }

                    @Override
                    public net.minecraft.server.packs.PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                        return resources;
                    }
                }, PackType.SERVER_DATA, new PackSelectionConfig(true, Pack.Position.TOP, false));
                if (pack != null) {
                    packConsumer.accept(pack);
                }
            });
        }
    }

    public static void createResources(Stream<Holder<Biome>> biomeStream) {
        Map<TagKey<Biome>, TagBuilder> builders = new HashMap<>();
        builders.put(IafWorldRegistry.HAS_MAUSOLEUM, TagBuilder.create());
        builders.put(IafWorldRegistry.HAS_GRAVEYARD, TagBuilder.create());
        builders.put(IafWorldRegistry.HAS_GORGON_TEMPLE, TagBuilder.create());
        biomeStream.forEach(biome -> {
            Identifier id = biome.unwrapKey().orElseThrow().identifier();
            if (BiomeConfig.test(BiomeConfig.gorgonTempleBiomes, biome)) {
                builders.get(IafWorldRegistry.HAS_GORGON_TEMPLE).addElement(id);
            }
            if (BiomeConfig.test(BiomeConfig.graveyardBiomes, biome)) {
                builders.get(IafWorldRegistry.HAS_GRAVEYARD).addElement(id);
            }
            if (BiomeConfig.test(BiomeConfig.mausoleumBiomes, biome)) {
                builders.get(IafWorldRegistry.HAS_MAUSOLEUM).addElement(id);
            }
        });
        addBiomeTag("has_structure/mausoleum.json", builders.get(IafWorldRegistry.HAS_MAUSOLEUM));
        addBiomeTag("has_structure/graveyard.json", builders.get(IafWorldRegistry.HAS_GRAVEYARD));
        addBiomeTag("has_structure/gorgon_temple.json", builders.get(IafWorldRegistry.HAS_GORGON_TEMPLE));
    }

    public static void createResources(Registry<Biome> biomes) {
        createResources(biomes.listElements().map(biome -> (Holder<Biome>) biome));
    }

    static void addBiomeTag(String location, TagBuilder builder) {
        var entries = builder.build();
        if (entries.isEmpty()) {
            return;
        }
        JsonObject json = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(entries, builder.shouldReplace()))
            .getOrThrow().getAsJsonObject();
        resources.add(Identifier.fromNamespaceAndPath(IceAndFire.MODID, "tags/worldgen/biome/" + location), json);
    }

    public static class PackResources implements net.minecraft.server.packs.PackResources {
        private final Map<Identifier, JsonObject> data = new HashMap<>();

        public void add(Identifier location, JsonObject json) {
            data.put(location, json);
        }

        @Override
        public @Nullable IoSupplier<InputStream> getRootResource(String... path) {
            if (path.length == 1 && PACK_META.equals(path[0])) {
                JsonObject root = new JsonObject();
                root.add("pack", PackMetadataSection.SERVER_TYPE.codec().encodeStart(JsonOps.INSTANCE, metadata()).getOrThrow());
                return bytes(root);
            }
            return null;
        }

        private static IoSupplier<InputStream> bytes(JsonObject json) {
            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }

        @Override
        public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier location) {
            JsonObject json = type == PackType.SERVER_DATA ? data.get(location) : null;
            return json == null ? null : bytes(json);
        }

        @Override
        public void listResources(PackType type, String namespace, String directory, ResourceOutput output) {
            if (type == PackType.SERVER_DATA) {
                String prefix = directory.isEmpty() ? "" : directory + "/";
                data.forEach((id, json) -> {
                    if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix)) {
                        output.accept(id, bytes(json));
                    }
                });
            }
        }

        @Override
        public Set<String> getNamespaces(PackType type) {
            return type == PackType.SERVER_DATA ? Set.of(IceAndFire.MODID) : Set.of();
        }

        @Override
        public PackLocationInfo location() {
            return LOCATION;
        }

        private static PackMetadataSection metadata() {
            return new PackMetadataSection(LOCATION.title(),
                new InclusiveRange<>(SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA)));
        }

        @Override
        public <T> @Nullable T getMetadataSection(MetadataSectionType<T> type) {
            return PackMetadataSection.SERVER_TYPE.withValue(metadata()).unwrapToType(type).orElse(null);
        }

        @Override
        public void close() {
        }
    }
}
