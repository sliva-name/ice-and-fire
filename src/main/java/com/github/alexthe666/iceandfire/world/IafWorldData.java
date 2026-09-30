package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.world.gen.TypedFeature;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IafWorldData extends SavedData {
    public enum FeatureType {
        SURFACE,
        UNDERGROUND,
        OCEAN
    }

    private static final Codec<Pair<String, BlockPos>> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("id").forGetter(Pair::getFirst),
        BlockPos.CODEC.fieldOf("position").forGetter(Pair::getSecond)
    ).apply(instance, Pair::of));

    private static final Codec<Map<FeatureType, List<Pair<String, BlockPos>>>> MAP_CODEC =
        Codec.unboundedMap(Codec.STRING.xmap(FeatureType::valueOf, FeatureType::name), ENTRY_CODEC.listOf());

    public static final Codec<IafWorldData> CODEC = MAP_CODEC.xmap(IafWorldData::fromSaved, IafWorldData::snapshot);

    public static final SavedDataType<IafWorldData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(IceAndFire.MODID, "general"),
        IafWorldData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    // Per instance (one per dimension save), never static: a static map leaked positions between worlds and
    // dimensions, and worldgen threads call check() concurrently, so every access goes through the instance lock.
    private final Map<FeatureType, List<Pair<String, BlockPos>>> lastGenerated = new HashMap<>();

    public IafWorldData() { /* Nothing to do */ }

    private static IafWorldData fromSaved(Map<FeatureType, List<Pair<String, BlockPos>>> map) {
        IafWorldData data = new IafWorldData();
        // Codec.listOf() / unboundedMap decode to immutable collections.
        map.forEach((type, entries) -> data.lastGenerated.put(type, new ArrayList<>(entries)));
        return data;
    }

    private synchronized Map<FeatureType, List<Pair<String, BlockPos>>> snapshot() {
        Map<FeatureType, List<Pair<String, BlockPos>>> copy = new HashMap<>();
        lastGenerated.forEach((type, entries) -> copy.put(type, new ArrayList<>(entries)));
        return copy;
    }

    @Nullable
    public static IafWorldData get(final Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(world.dimension());
            SavedDataStorage storage = overworld.getDataStorage();
            synchronized (IafWorldData.class) {
                return storage.computeIfAbsent(TYPE);
            }
        }

        return null;
    }

    public boolean check(final TypedFeature feature, final BlockPos position, final String id) {
        return check(feature.getFeatureType(), position, id);
    }

    public synchronized boolean check(final FeatureType type, final BlockPos position, final String id) {
        List<Pair<String, BlockPos>> entries = lastGenerated.computeIfAbsent(type, key -> new ArrayList<>());

        boolean canGenerate = true;
        Pair<String, BlockPos> toRemove = null;

        for (Pair<String, BlockPos> entry : entries) {
            if (entry.getFirst().equals(id)) {
                toRemove = entry;
            }

            canGenerate = position.distSqr(entry.getSecond()) > IafConfig.dangerousWorldGenSeparationLimit * IafConfig.dangerousWorldGenSeparationLimit;
        }

        if (toRemove != null) {
            entries.remove(toRemove);
        }

        entries.add(Pair.of(id, position));
        this.setDirty();

        return canGenerate;
    }
}
