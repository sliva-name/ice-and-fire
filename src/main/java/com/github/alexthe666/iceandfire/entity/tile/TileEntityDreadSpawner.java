package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDreadMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityDreadSpawner extends BlockEntity implements Spawner {
    private final DreadSpawnerBaseLogic spawner = new DreadSpawnerBaseLogic() {
        @Override
        public void broadcastEvent(Level level, @NotNull BlockPos pos, int id) {
            level.blockEvent(pos, IafBlockRegistry.DREAD_SPAWNER.get(), id, 0);
        }

        @Override
        public void setNextSpawnData(@Nullable Level level, @NotNull BlockPos pos, @NotNull SpawnData nextSpawnData) {
            super.setNextSpawnData(level, pos, nextSpawnData);
            if (level != null) {
                BlockState state = level.getBlockState(pos);
                level.sendBlockUpdated(pos, state, state, 4);
            }
        }

        @Override
        @Nullable
        public BlockEntity getSpawnerBlockEntity() {
            return TileEntityDreadSpawner.this;
        }
    };

    public TileEntityDreadSpawner(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.DREAD_SPAWNER.get(), pos, state);
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.spawner.load(this.level, this.worldPosition, input);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        this.spawner.save(output);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TileEntityDreadSpawner entity) {
        entity.spawner.clientTick(level, pos);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityDreadSpawner entity) {
        if (!EntityDreadMob.canSpawnInDungeon(level, pos)) {
            return;
        }
        entity.spawner.serverTick((ServerLevel) level, pos);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveCustomOnly(registries);
        tag.remove("SpawnPotentials");
        return tag;
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        return this.spawner.onEventTriggered(this.level, id) || super.triggerEvent(id, param);
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.spawner.setEntityId(type, this.level, random, this.worldPosition);
        this.setChanged();
    }

    public @NotNull BaseSpawner getSpawner() {
        return this.spawner;
    }
}
