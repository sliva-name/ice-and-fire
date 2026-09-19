package com.github.alexthe666.iceandfire.entity;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityDreadMob extends Monster implements IDreadMob {
    protected static final EntityDataAccessor<String> COMMANDER_UNIQUE_ID = SynchedEntityData.defineId(EntityDreadMob.class, EntityDataSerializers.STRING);

    public EntityDreadMob(EntityType<? extends Monster> t, Level worldIn) {
        super(t, worldIn);
    }

    public static Entity necromancyEntity(LivingEntity entity) {
        Entity lichSummoned = null;
        if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.ARTHROPOD)) {
            lichSummoned = new EntityDreadScuttler(IafEntityRegistry.DREAD_SCUTTLER.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 1.5F);
            if (entity.level() instanceof ServerLevelAccessor server) {
                ((EntityDreadScuttler) lichSummoned).finalizeSpawn(server, server.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            }
            ((EntityDreadScuttler) lichSummoned).setSize(readInScale);
            return lichSummoned;
        }
        if (entity instanceof Zombie || entity instanceof IHumanoid) {
            lichSummoned = new EntityDreadGhoul(IafEntityRegistry.DREAD_GHOUL.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 0.6F);
            if (entity.level() instanceof ServerLevelAccessor server) {
                ((EntityDreadGhoul) lichSummoned).finalizeSpawn(server, server.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            }
            ((EntityDreadGhoul) lichSummoned).setSize(readInScale);
            return lichSummoned;
        }
        if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD) || entity instanceof AbstractSkeleton || entity instanceof Player) {
            lichSummoned = new EntityDreadThrall(IafEntityRegistry.DREAD_THRALL.get(), entity.level());
            EntityDreadThrall thrall = (EntityDreadThrall) lichSummoned;
            if (entity.level() instanceof ServerLevelAccessor server) {
                thrall.finalizeSpawn(server, server.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            }
            thrall.setCustomArmorHead(false);
            thrall.setCustomArmorChest(false);
            thrall.setCustomArmorLegs(false);
            thrall.setCustomArmorFeet(false);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                thrall.setItemSlot(slot, entity.getItemBySlot(slot));
            }
            return thrall;
        }
        if (entity instanceof AbstractHorse) {
            lichSummoned = new EntityDreadHorse(IafEntityRegistry.DREAD_HORSE.get(), entity.level());
            return lichSummoned;
        }
        if (entity instanceof Animal) {
            lichSummoned = new EntityDreadBeast(IafEntityRegistry.DREAD_BEAST.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 1.2F);
            if (entity.level() instanceof ServerLevelAccessor server) {
                ((EntityDreadBeast) lichSummoned).finalizeSpawn(server, server.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            }
            ((EntityDreadBeast) lichSummoned).setSize(readInScale);
            return lichSummoned;
        }
        return lichSummoned;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COMMANDER_UNIQUE_ID, "");
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        if (this.getCommanderId() != null) {
            compound.store("CommanderUUID", UUIDUtil.CODEC, this.getCommanderId());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.read("CommanderUUID", UUIDUtil.CODEC).isPresent()) {
            uuid = compound.read("CommanderUUID", UUIDUtil.CODEC).orElseThrow();
        } else {
            String s = compound.getStringOr("CommanderUUID", "");
            uuid = this.level() instanceof net.minecraft.server.level.ServerLevel server
                ? OldUsersConverter.convertMobOwnerIfNecessary(server.getServer(), s)
                : null;
        }

        if (uuid != null) {
            try {
                this.setCommanderId(uuid);
            } catch (Throwable throwable) {
            }
        }

    }


    public boolean iafIsAlliedTo(@NotNull Entity entityIn) {
        return entityIn instanceof IDreadMob || super.isAlliedTo(entityIn);
    }

    @Nullable
    public UUID getCommanderId() {
        String stored = this.entityData.get(COMMANDER_UNIQUE_ID);
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(stored);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public void setCommanderId(@Nullable UUID uuid) {
        this.entityData.set(COMMANDER_UNIQUE_ID, uuid == null ? "" : uuid.toString());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.getCommander() instanceof EntityDreadLich) {
            EntityDreadLich lich = (EntityDreadLich) this.getCommander();
            if (lich.getTarget() != null && lich.getTarget().isAlive()) {
                this.setTarget(lich.getTarget());
            }
        }
    }

    @Override
    public Entity getCommander() {
        try {
            UUID uuid = this.getCommanderId();
            LivingEntity player = uuid == null ? null : this.level().getPlayerByUUID(uuid);
            if (player != null) {
                return player;
            } else {
                if (!this.level().isClientSide()) {
                    Entity entity = this.level().getServer().getLevel(this.level().dimension()).getEntity(uuid);
                    if (entity instanceof LivingEntity) {
                        return entity;
                    }
                }
            }
        } catch (IllegalArgumentException var2) {
            return null;
        }
        return null;
    }

    public void onKillEntity(LivingEntity LivingEntityIn) {
        Entity commander = this instanceof EntityDreadLich ? this : this.getCommander();
        if (commander != null && !(LivingEntityIn instanceof EntityDragonBase)) {// zombie dragons!!!!
            Entity summoned = necromancyEntity(LivingEntityIn);
            if (summoned != null) {
                summoned.copyPosition(LivingEntityIn);
                if (!this.level().isClientSide()) {
                    this.level().addFreshEntity(summoned);
                }
                if (commander instanceof EntityDreadLich) {
                    ((EntityDreadLich) commander).setMinionCount(((EntityDreadLich) commander).getMinionCount() + 1);
                }
                if (summoned instanceof EntityDreadMob) {
                    ((EntityDreadMob) summoned).setCommanderId(commander.getUUID());
                }
            }
        }

    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!isRemoved() && this.getCommander() != null && this.getCommander() instanceof EntityDreadLich) {
            EntityDreadLich lich = (EntityDreadLich) this.getCommander();
            lich.setMinionCount(lich.getMinionCount() - 1);
        }
        super.remove(reason);
    }
}
