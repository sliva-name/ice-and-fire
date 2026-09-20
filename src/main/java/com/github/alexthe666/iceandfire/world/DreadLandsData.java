package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class DreadLandsData extends SavedData {
    public static final Codec<DreadLandsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("Spawned", false).forGetter(data -> data.spawned),
        Codec.BOOL.optionalFieldOf("Defeated", false).forGetter(data -> data.defeated),
        UUIDUtil.CODEC.optionalFieldOf("QueenUUID").forGetter(data -> Optional.ofNullable(data.queenId)),
        UUIDUtil.CODEC.optionalFieldOf("DragonUUID").forGetter(data -> Optional.ofNullable(data.dragonId))
    ).apply(instance, DreadLandsData::new));

    public static final SavedDataType<DreadLandsData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dread_lands_rulers"),
        DreadLandsData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    private boolean spawned;
    private boolean defeated;
    @Nullable
    private UUID queenId;
    @Nullable
    private UUID dragonId;

    public DreadLandsData() {
    }

    private DreadLandsData(boolean spawned, boolean defeated, Optional<UUID> queenId, Optional<UUID> dragonId) {
        this.spawned = spawned;
        this.defeated = defeated;
        this.queenId = queenId.orElse(null);
        this.dragonId = dragonId.orElse(null);
    }

    @Nullable
    public static DreadLandsData get(Level world) {
        if (world instanceof ServerLevel server) {
            SavedDataStorage storage = server.getDataStorage();
            DreadLandsData data = storage.computeIfAbsent(TYPE);
            if (data != null) {
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public boolean hasSpawned() {
        return this.spawned;
    }

    public boolean isDefeated() {
        return this.defeated;
    }

    @Nullable
    public UUID getQueenId() {
        return this.queenId;
    }

    @Nullable
    public UUID getDragonId() {
        return this.dragonId;
    }

    public boolean tryBeginSpawn() {
        if (this.spawned || this.defeated) {
            return false;
        }
        this.spawned = true;
        this.setDirty();
        return true;
    }

    public void markSpawned() {
        this.spawned = true;
        this.setDirty();
    }

    public void setRulers(@Nullable UUID queenId, @Nullable UUID dragonId) {
        this.queenId = queenId;
        this.dragonId = dragonId;
        this.spawned = true;
        this.setDirty();
    }

    public void markDefeated() {
        this.defeated = true;
        this.spawned = true;
        this.setDirty();
    }
}
