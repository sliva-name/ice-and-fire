package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DragonPosWorldData extends SavedData {

    private static final Codec<Map<UUID, BlockPos>> DRAGON_MAP_CODEC = RecordCodecBuilder.<Map.Entry<UUID, BlockPos>>create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("DragonUUID").forGetter(Map.Entry::getKey),
        Codec.INT.fieldOf("DragonPosX").forGetter(entry -> entry.getValue().getX()),
        Codec.INT.fieldOf("DragonPosY").forGetter(entry -> entry.getValue().getY()),
        Codec.INT.fieldOf("DragonPosZ").forGetter(entry -> entry.getValue().getZ())
    ).apply(instance, (uuid, x, y, z) -> Map.entry(uuid, new BlockPos(x, y, z)))).listOf().xmap(
        list -> {
            Map<UUID, BlockPos> map = new HashMap<>();
            list.forEach(entry -> map.put(entry.getKey(), entry.getValue()));
            return map;
        },
        map -> List.copyOf(map.entrySet())
    );

    public static final Codec<DragonPosWorldData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("Tick").forGetter(data -> data.tickCounter),
        DRAGON_MAP_CODEC.fieldOf("DragonMap").forGetter(data -> data.lastDragonPositions)
    ).apply(instance, DragonPosWorldData::new));

    public static final SavedDataType<DragonPosWorldData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dragon_positions"),
        DragonPosWorldData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    protected final Map<UUID, BlockPos> lastDragonPositions = new HashMap<>();
    private Level world;
    private int tickCounter;

    public DragonPosWorldData() {
    }

    public DragonPosWorldData(Level world) {
        this.world = world;
        this.setDirty();
    }

    private DragonPosWorldData(int tickCounter, Map<UUID, BlockPos> positions) {
        this.tickCounter = tickCounter;
        this.lastDragonPositions.putAll(positions);
    }

    @Nullable
    public static DragonPosWorldData get(Level world) {
        if (world instanceof ServerLevel) {
            ServerLevel overworld = world.getServer().getLevel(world.dimension());

            SavedDataStorage storage = overworld.getDataStorage();
            DragonPosWorldData data = storage.computeIfAbsent(TYPE);
            if (data != null) {
                data.world = world;
                data.setDirty();
            }
            return data;
        }
        return null;
    }

    public void addDragon(UUID uuid, BlockPos pos) {
        lastDragonPositions.put(uuid, pos);
        this.setDirty();
    }

    public void removeDragon(UUID uuid) {
        lastDragonPositions.remove(uuid);
        this.setDirty();
    }

    public BlockPos getDragonPos(UUID uuid) {
        return lastDragonPositions.get(uuid);
    }

    public void debug() {
        IceAndFire.LOGGER.warn(lastDragonPositions.toString());
    }


    public void tick() {
        ++this.tickCounter;
    }
}
