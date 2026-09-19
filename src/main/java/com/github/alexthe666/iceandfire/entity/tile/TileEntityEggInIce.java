package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.entity.util.IafOwners;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TileEntityEggInIce extends BlockEntity {
    public EnumDragonEgg type;
    public int age;
    public int ticksExisted;
    @Nullable
    public UUID ownerUUID;
    // boolean to prevent time in a bottle shenanigans
    private boolean spawned;

    public TileEntityEggInIce(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.EGG_IN_ICE.get(), pos, state);
    }

    public static void tickEgg(Level level, BlockPos pos, BlockState state, TileEntityEggInIce entityEggInIce) {
        entityEggInIce.age++;
        if (entityEggInIce.age >= IafConfig.dragonEggTime && entityEggInIce.type != null && !entityEggInIce.spawned) {
            if (!level.isClientSide()) {
                EntityIceDragon dragon = new EntityIceDragon(level);
                dragon.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setVariant(entityEggInIce.type.ordinal() - 4);
                dragon.setGender(ThreadLocalRandom.current().nextBoolean());
                dragon.setTame(true, false);
                dragon.setHunger(50);
                IafOwners.setUUID(dragon, entityEggInIce.ownerUUID);
                level.addFreshEntity(dragon);
                entityEggInIce.spawned = true;
                level.destroyBlock(pos, false);
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }

        }
        entityEggInIce.ticksExisted++;
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput tag) {
        if (type != null) {
            tag.putByte("Color", (byte) type.ordinal());
        } else {
            tag.putByte("Color", (byte) 0);
        }
        tag.putInt("Age", age);
        if (ownerUUID == null) {
            tag.putString("OwnerUUID", "");
        } else {
            tag.store("OwnerUUID", UUIDUtil.CODEC, ownerUUID);
        }
    }

    @Override
    public void loadAdditional(@NotNull ValueInput tag) {
        super.loadAdditional(tag);
        type = EnumDragonEgg.values()[tag.getByteOr("Color", (byte) 0)];
        age = tag.getIntOr("Age", 0);
        UUID s = null;

        if (tag.read("OwnerUUID", UUIDUtil.CODEC).isPresent()) {
            s = tag.read("OwnerUUID", UUIDUtil.CODEC).orElseThrow();
        } else {
            try {
                String s1 = tag.getStringOr("OwnerUUID", "");
                s = OldUsersConverter.convertMobOwnerIfNecessary(getLevel() == null ? null : getLevel().getServer(), s1);
            } catch (Exception ignored) {
            }
        }
        if (s != null) {
            ownerUUID = s;
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null && getLevel() != null) {
            this.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, getLevel().registryAccess(), pkt.getTag()));
        }
    }

    public void spawnEgg() {
        if (type != null) {
            EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG.get(), getLevel());
            egg.setEggType(type);
            egg.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            if (!level.isClientSide()) {
                level.addFreshEntity(egg);
            }
        }
    }
}
