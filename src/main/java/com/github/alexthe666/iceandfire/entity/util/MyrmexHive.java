package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.network.chat.Component;
import net.minecraft.core.UUIDUtil;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;
import java.util.*;

public class MyrmexHive {
    private final List<BlockPos> foodRooms = Lists.newArrayList();
    private final List<BlockPos> babyRooms = Lists.newArrayList();
    private final List<BlockPos> miscRooms = Lists.newArrayList();
    private final List<BlockPos> allRooms = Lists.newArrayList();
    private final Map<BlockPos, Direction> entrances = Maps.newHashMap();
    private final Map<BlockPos, Direction> entranceBottoms = Maps.newHashMap();
    private final Map<UUID, Integer> playerReputation = Maps.newHashMap();
    private final List<HiveAggressor> villageAgressors = Lists.newArrayList();
    private final List<UUID> myrmexList = Lists.newArrayList();
    public UUID hiveUUID;
    public String colonyName = "";
    public boolean reproduces = true;
    public boolean hasOwner = false;
    public UUID ownerUUID = null;
    private Level world;
    private BlockPos centerHelper = BlockPos.ZERO;
    private BlockPos center = BlockPos.ZERO;
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numMyrmex;
    private int noBreedTicks;
    private int wanderRadius = 16;

    public MyrmexHive() {
        this.hiveUUID = UUID.randomUUID();
    }

    public MyrmexHive(Level worldIn) {
        this.world = worldIn;
        this.hiveUUID = UUID.randomUUID();
    }

    public MyrmexHive(Level worldIn, BlockPos center, int radius) {
        this.world = worldIn;
        this.center = center;
        this.villageRadius = radius;
        this.hiveUUID = UUID.randomUUID();
    }

    public static BlockPos getGroundedPos(LevelAccessor world, BlockPos pos) {
        BlockPos current = pos;
        while (world.isEmptyBlock(current.below()) && current.getY() > 0) {
            current = current.below();
        }
        return current;
    }

    public static MyrmexHive fromNBT(CompoundTag hive) {
        MyrmexHive hive1 = new MyrmexHive();
        hive1.readVillageDataFromNBT(hive);
        return hive1;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        this.writeVillageDataToNBT(tag);
        return tag;
    }

    public void setWorld(Level worldIn) {
        this.world = worldIn;
    }

    public void tick(int tickCounterIn, Level world) {
        this.tickCounter++;
        this.removeDeadAndOldAgressors();
        if (tickCounter % 20 == 0) {
            this.updateNumMyrmex(world);
        }
    }

    private void updateNumMyrmex(Level world) {
        this.numMyrmex = this.myrmexList.size();
        if (this.numMyrmex == 0) {
            this.playerReputation.clear();
        }
    }

    @Nullable
    public EntityMyrmexQueen getQueen() {
        List<EntityMyrmexQueen> ourQueens = new ArrayList<>();
        if (!world.isClientSide()) {
            ServerLevel serverWorld = world.getServer().getLevel(world.dimension());
            List<? extends EntityMyrmexQueen> allQueens = serverWorld.getEntities(IafEntityRegistry.MYRMEX_QUEEN.get(),
                EntitySelector.NO_SPECTATORS);
            for (Entity queen : allQueens) {
                if (queen instanceof EntityMyrmexQueen && ((EntityMyrmexQueen) queen).getHive().equals(this)) {
                    ourQueens.add(((EntityMyrmexQueen) queen));
                }
            }
        }

        return ourQueens.isEmpty() ? null : ourQueens.get(0);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public BlockPos getCenterGround() {
        return getGroundedPos(this.world, this.center);
    }

    public int getVillageRadius() {
        return this.villageRadius;
    }

    public int getNumMyrmex() {
        return this.numMyrmex;
    }

    public int getWanderRadius(){
        return this.wanderRadius;
    }
    public void setWanderRadius(int wanderRadius){
        this.wanderRadius = Math.min(wanderRadius,IafConfig.myrmexMaximumWanderRadius);
    }

    public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
        return this.center.distSqr(pos) < this.villageRadius * this.villageRadius;
    }

    public boolean isAnnihilated() {
        return false;
    }

    public void addOrRenewAgressor(LivingEntity LivingEntityIn, int agressiveLevel) {
        for (HiveAggressor hive$villageaggressor : this.villageAgressors) {
            if (hive$villageaggressor.agressor == LivingEntityIn) {
                hive$villageaggressor.agressionTime = this.tickCounter;
                return;
            }
        }

        this.villageAgressors.add(new HiveAggressor(LivingEntityIn, this.tickCounter, agressiveLevel));
    }

    @Nullable
    public LivingEntity findNearestVillageAggressor(LivingEntity LivingEntityIn) {
        double d0 = Double.MAX_VALUE;
        int previousAgressionLevel = 0;
        HiveAggressor hive$villageaggressor = null;
        for (int i = 0; i < this.villageAgressors.size(); ++i) {
            HiveAggressor hive$villageaggressor1 = this.villageAgressors.get(i);
            double d1 = hive$villageaggressor1.agressor.distanceToSqr(LivingEntityIn);
            int agressionLevel = hive$villageaggressor1.agressionLevel;

            if (d1 <= d0 || agressionLevel > previousAgressionLevel) {
                hive$villageaggressor = hive$villageaggressor1;
                d0 = d1;
            }
            previousAgressionLevel = agressionLevel;
        }

        return hive$villageaggressor == null ? null : hive$villageaggressor.agressor;
    }

    public Player getNearestTargetPlayer(LivingEntity villageDefender, Level world) {
        double d0 = Double.MAX_VALUE;
        Player PlayerEntity = null;

        for (UUID s : this.playerReputation.keySet()) {
            if (this.isPlayerReputationLowEnoughToFight(s)) {
                Player PlayerEntity1 = world.getPlayerByUUID(s);

                if (PlayerEntity1 != null) {
                    double d1 = PlayerEntity1.distanceToSqr(villageDefender);

                    if (d1 <= d0) {
                        PlayerEntity = PlayerEntity1;
                        d0 = d1;
                    }
                }
            }
        }

        return PlayerEntity;
    }

    private void removeDeadAndOldAgressors() {
        Iterator<HiveAggressor> iterator = this.villageAgressors.iterator();

        while (iterator.hasNext()) {
            HiveAggressor hive$villageaggressor = iterator.next();

            if (!hive$villageaggressor.agressor.isAlive() || Math.abs(this.tickCounter - hive$villageaggressor.agressionTime) > 300) {
                iterator.remove();
            }
        }
    }

    public int getPlayerReputation(UUID playerName) {
        Integer integer = this.playerReputation.get(playerName);
        return integer == null ? 0 : integer.intValue();
    }

    private UUID findUUID(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public int modifyPlayerReputation(UUID playerName, int reputation) {
        int i = this.getPlayerReputation(playerName);
        int j = Mth.clamp(i + reputation, 0, 100);
        if (this.hasOwner && playerName.equals(ownerUUID)) {
            j = 100;
        }
        Player player = null;
        try {
            player = world.getPlayerByUUID(playerName);
        } catch (Exception e) {
            IceAndFire.LOGGER.warn("Myrmex Hive could not find player with associated UUID");
        }
        if (player != null) {
            if (j - i != 0) {
                player.sendOverlayMessage(Component.translatable(j - i >= 0 ? "myrmex.message.raised_reputation" : "myrmex.message.lowered_reputation", Math.abs(j - i), j));
            }
            if (i < 25 && j >= 25) {
                player.sendSystemMessage(Component.translatable("myrmex.message.peaceful"));
            }
            if (i >= 25 && j < 25) {
                player.sendSystemMessage(Component.translatable("myrmex.message.hostile"));
            }
            if (i < 50 && j >= 50) {
                player.sendSystemMessage(Component.translatable("myrmex.message.trade"));
            }
            if (i >= 50 && j < 50) {
                player.sendSystemMessage(Component.translatable("myrmex.message.no_trade"));
            }
            if (i < 75 && j >= 75) {
                player.sendSystemMessage(Component.translatable("myrmex.message.can_use_staff"));
            }
            if (i >= 75 && j < 75) {
                player.sendSystemMessage(Component.translatable("myrmex.message.cant_use_staff"));
            }
        }

        this.playerReputation.put(playerName, j);
        return j;
    }

    public boolean isPlayerReputationTooLowToTrade(UUID uuid) {
        return this.getPlayerReputation(uuid) < 50;
    }

    public boolean canPlayerCommandHive(UUID uuid) {
        return this.getPlayerReputation(uuid) >= 75;
    }

    public boolean isPlayerReputationLowEnoughToFight(UUID uuid) {
        return this.getPlayerReputation(uuid) < 25;
    }

    /**
     * Read this village's data from NBT.
     */
    public void readVillageDataFromNBT(CompoundTag compound) {
        this.numMyrmex = compound.getIntOr("PopSize", 0);
        this.reproduces = compound.getBooleanOr("Reproduces", false);
        this.hasOwner = compound.getBooleanOr("HasOwner", false);
        if (compound.read("OwnerUUID", UUIDUtil.CODEC).isPresent()) {
            this.ownerUUID = compound.read("OwnerUUID", UUIDUtil.CODEC).orElseThrow();
        }
        this.colonyName = compound.getStringOr("ColonyName", "");
        this.villageRadius = compound.getIntOr("Radius", 0);
        if (compound.read("WanderRadius", UUIDUtil.CODEC).isPresent()) {
            this.wanderRadius = compound.getIntOr("WanderRadius", 0);
        }
        this.lastAddDoorTimestamp = compound.getIntOr("Stable", 0);
        this.tickCounter = compound.getIntOr("Tick", 0);
        this.noBreedTicks = compound.getIntOr("MTick", 0);
        this.center = new BlockPos(compound.getIntOr("CX", 0), compound.getIntOr("CY", 0), compound.getIntOr("CZ", 0));
        this.centerHelper = new BlockPos(compound.getIntOr("ACX", 0), compound.getIntOr("ACY", 0), compound.getIntOr("ACZ", 0));
        ListTag hiveMembers = compound.getListOrEmpty("HiveMembers");
        this.myrmexList.clear();
        for (int i = 0; i < hiveMembers.size(); ++i) {
            CompoundTag CompoundNBT = hiveMembers.getCompoundOrEmpty(i);
            this.myrmexList.add(CompoundNBT.read("MyrmexUUID", UUIDUtil.CODEC).orElseThrow());
        }
        ListTag foodRoomList = compound.getListOrEmpty("FoodRooms");
        this.foodRooms.clear();
        for (int i = 0; i < foodRoomList.size(); ++i) {
            CompoundTag CompoundNBT = foodRoomList.getCompoundOrEmpty(i);
            this.foodRooms.add(new BlockPos(CompoundNBT.getIntOr("X", 0), CompoundNBT.getIntOr("Y", 0), CompoundNBT.getIntOr("Z", 0)));
        }
        ListTag babyRoomList = compound.getListOrEmpty("BabyRooms");
        this.babyRooms.clear();
        for (int i = 0; i < babyRoomList.size(); ++i) {
            CompoundTag CompoundNBT = babyRoomList.getCompoundOrEmpty(i);
            this.babyRooms.add(new BlockPos(CompoundNBT.getIntOr("X", 0), CompoundNBT.getIntOr("Y", 0), CompoundNBT.getIntOr("Z", 0)));
        }
        ListTag miscRoomList = compound.getListOrEmpty("MiscRooms");
        this.miscRooms.clear();
        for (int i = 0; i < miscRoomList.size(); ++i) {
            CompoundTag CompoundNBT = miscRoomList.getCompoundOrEmpty(i);
            this.miscRooms.add(new BlockPos(CompoundNBT.getIntOr("X", 0), CompoundNBT.getIntOr("Y", 0), CompoundNBT.getIntOr("Z", 0)));
        }
        ListTag entrancesList = compound.getListOrEmpty("Entrances");
        this.entrances.clear();
        for (int i = 0; i < entrancesList.size(); ++i) {
            CompoundTag CompoundNBT = entrancesList.getCompoundOrEmpty(i);
            this.entrances.put(new BlockPos(CompoundNBT.getIntOr("X", 0), CompoundNBT.getIntOr("Y", 0), CompoundNBT.getIntOr("Z", 0)), Direction.from2DDataValue(CompoundNBT.getIntOr("Facing", 0)));
        }

        ListTag entranceBottomsList = compound.getListOrEmpty("EntranceBottoms");
        this.entranceBottoms.clear();
        for (int i = 0; i < entranceBottomsList.size(); ++i) {
            CompoundTag CompoundNBT = entranceBottomsList.getCompoundOrEmpty(i);
            this.entranceBottoms.put(new BlockPos(CompoundNBT.getIntOr("X", 0), CompoundNBT.getIntOr("Y", 0), CompoundNBT.getIntOr("Z", 0)), Direction.from2DDataValue(CompoundNBT.getIntOr("Facing", 0)));
        }
        hiveUUID = compound.read("HiveUUID", UUIDUtil.CODEC).orElseThrow();
        ListTag nbttaglist1 = compound.getListOrEmpty("Players");
        this.playerReputation.clear();
        for (int j = 0; j < nbttaglist1.size(); ++j) {
            CompoundTag CompoundNBT1 = nbttaglist1.getCompoundOrEmpty(j);

            if (CompoundNBT1.read("UUID", UUIDUtil.CODEC).isPresent()) {
                this.playerReputation.put(CompoundNBT1.read("UUID", UUIDUtil.CODEC).orElseThrow(), CompoundNBT1.getIntOr("S", 0));
            } else {
                //World is never set here, so this will always be offline UUIDs, sadly there is no way to convert this.
                this.playerReputation.put(findUUID(CompoundNBT1.getStringOr("Name", "")), CompoundNBT1.getIntOr("S", 0));
            }
        }
    }

    /**
     * Write this village's data to NBT.
     */
    public void writeVillageDataToNBT(CompoundTag compound) {
        compound.putInt("PopSize", this.numMyrmex);
        compound.putBoolean("Reproduces", this.reproduces);
        compound.putBoolean("HasOwner", this.hasOwner);
        if (this.ownerUUID != null) {
            compound.store("OwnerUUID", UUIDUtil.CODEC, this.ownerUUID);
        }
        compound.putString("ColonyName", this.colonyName);
        compound.putInt("Radius", this.villageRadius);
        compound.putInt("WanderRadius", this.wanderRadius);
        compound.putInt("Stable", this.lastAddDoorTimestamp);
        compound.putInt("Tick", this.tickCounter);
        compound.putInt("MTick", this.noBreedTicks);
        compound.putInt("CX", this.center.getX());
        compound.putInt("CY", this.center.getY());
        compound.putInt("CZ", this.center.getZ());
        compound.putInt("ACX", this.centerHelper.getX());
        compound.putInt("ACY", this.centerHelper.getY());
        compound.putInt("ACZ", this.centerHelper.getZ());
        ListTag hiveMembers = new ListTag();
        for (UUID memberUUID : this.myrmexList) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.store("MyrmexUUID", UUIDUtil.CODEC, memberUUID);
            hiveMembers.add(CompoundNBT);
        }
        compound.put("HiveMembers", hiveMembers);
        ListTag foodRoomList = new ListTag();
        for (BlockPos pos : this.foodRooms) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            foodRoomList.add(CompoundNBT);
        }
        compound.put("FoodRooms", foodRoomList);
        ListTag babyRoomList = new ListTag();
        for (BlockPos pos : this.babyRooms) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            babyRoomList.add(CompoundNBT);
        }
        compound.put("BabyRooms", babyRoomList);
        ListTag miscRoomList = new ListTag();
        for (BlockPos pos : this.miscRooms) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            miscRoomList.add(CompoundNBT);
        }
        compound.put("MiscRooms", miscRoomList);
        ListTag entrancesList = new ListTag();
        for (Map.Entry<BlockPos, Direction> entry : this.entrances.entrySet()) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putInt("X", entry.getKey().getX());
            CompoundNBT.putInt("Y", entry.getKey().getY());
            CompoundNBT.putInt("Z", entry.getKey().getZ());
            CompoundNBT.putInt("Facing", entry.getValue().get2DDataValue());
            entrancesList.add(CompoundNBT);
        }
        compound.put("Entrances", entrancesList);

        ListTag entranceBottomsList = new ListTag();
        for (Map.Entry<BlockPos, Direction> entry : this.entranceBottoms.entrySet()) {
            CompoundTag CompoundNBT = new CompoundTag();
            CompoundNBT.putInt("X", entry.getKey().getX());
            CompoundNBT.putInt("Y", entry.getKey().getY());
            CompoundNBT.putInt("Z", entry.getKey().getZ());
            CompoundNBT.putInt("Facing", entry.getValue().get2DDataValue());
            entranceBottomsList.add(CompoundNBT);
        }
        compound.put("EntranceBottoms", entranceBottomsList);
        compound.store("HiveUUID", UUIDUtil.CODEC, this.hiveUUID);
        ListTag nbttaglist1 = new ListTag();

        for (UUID s : this.playerReputation.keySet()) {
            CompoundTag CompoundNBT1 = new CompoundTag();

            try {
                {
                    CompoundNBT1.store("UUID", UUIDUtil.CODEC, s);
                    CompoundNBT1.putInt("S", this.playerReputation.get(s).intValue());
                    nbttaglist1.add(CompoundNBT1);
                }
            } catch (RuntimeException var9) {
            }
        }

        compound.put("Players", nbttaglist1);
    }

    public void addRoom(BlockPos center, WorldGenMyrmexHive.RoomType roomType) {
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD && !this.foodRooms.contains(center)) {
            this.foodRooms.add(center);
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY && !this.babyRooms.contains(center)) {
            this.babyRooms.add(center);
        } else if (!this.miscRooms.contains(center) && !this.miscRooms.contains(center)) {
            this.miscRooms.add(center);
        }
        this.allRooms.add(center);
    }

    public void addRoomWithMessage(Player player, BlockPos center, WorldGenMyrmexHive.RoomType roomType) {
        List<BlockPos> allCurrentRooms = new ArrayList<>(getAllRooms());
        allCurrentRooms.addAll(this.getEntrances().keySet());
        allCurrentRooms.addAll(this.getEntranceBottoms().keySet());
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD) {
            if (!this.foodRooms.contains(center) && !allCurrentRooms.contains(center)) {
                this.foodRooms.add(center);
                player.sendSystemMessage(Component.translatable("myrmex.message.added_food_room", center.getX(), center.getY(), center.getZ()));
            } else {
                player.sendSystemMessage(Component.translatable("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()));

            }
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY) {
            if (!this.babyRooms.contains(center) && !allCurrentRooms.contains(center)) {
                this.babyRooms.add(center);
                player.sendSystemMessage(Component.translatable("myrmex.message.added_nursery_room", center.getX(), center.getY(), center.getZ()));
            } else {
                player.sendSystemMessage(Component.translatable("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()));
            }
        } else if (!this.miscRooms.contains(center) && !allCurrentRooms.contains(center)) {
            this.miscRooms.add(center);
            player.sendSystemMessage(Component.translatable("myrmex.message.added_misc_room", center.getX(), center.getY(), center.getZ()));
        } else {
            player.sendSystemMessage(Component.translatable("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()));
        }
    }

    public void addEnteranceWithMessage(Player player, boolean bottom, BlockPos center, Direction facing) {
        List<BlockPos> allCurrentRooms = new ArrayList<>(getAllRooms());
        allCurrentRooms.addAll(this.getEntrances().keySet());
        allCurrentRooms.addAll(this.getEntranceBottoms().keySet());
        if (bottom) {
            if (allCurrentRooms.contains(center)) {
                player.sendSystemMessage(Component.translatable("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()));
            } else {
                this.getEntranceBottoms().put(center, facing);
                player.sendSystemMessage(Component.translatable("myrmex.message.added_enterance_bottom", center.getX(), center.getY(), center.getZ()));
            }
        } else {
            if (allCurrentRooms.contains(center)) {
                player.sendSystemMessage(Component.translatable("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()));
            } else {
                this.getEntrances().put(center, facing);
                player.sendSystemMessage(Component.translatable("myrmex.message.added_enterance_surface", center.getX(), center.getY(), center.getZ()));
            }
        }
    }

    public List<BlockPos> getRooms(WorldGenMyrmexHive.RoomType roomType) {
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD) {
            return foodRooms;
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY) {
            return babyRooms;
        } else {
            return miscRooms;
        }
    }

    public List<BlockPos> getAllRooms() {
        allRooms.clear();
        allRooms.add(center);
        allRooms.addAll(foodRooms);
        allRooms.addAll(babyRooms);
        allRooms.addAll(miscRooms);
        return allRooms;
    }

    public BlockPos getRandomRoom(RandomSource random, BlockPos returnPos) {
        List<BlockPos> rooms = getAllRooms();
        return rooms.isEmpty() ? returnPos : rooms.get(random.nextInt(Math.max(rooms.size() - 1, 1)));
    }

    public BlockPos getRandomRoom(WorldGenMyrmexHive.RoomType roomType, RandomSource random, BlockPos returnPos) {
        List<BlockPos> rooms = getRooms(roomType);
        return rooms.isEmpty() ? returnPos : rooms.get(random.nextInt(Math.max(rooms.size() - 1, 1)));
    }

    public BlockPos getClosestEntranceToEntity(Entity entity, RandomSource random, boolean randomize) {
        Map.Entry<BlockPos, Direction> closest = getClosestEntrance(entity);
        if (closest != null) {
            if (randomize) {
                BlockPos pos = closest.getKey().relative(closest.getValue(), random.nextInt(7) + 7).above(4);
                return pos.offset(10 - random.nextInt(20), 0, 10 - random.nextInt(20));
            } else {
                return closest.getKey().relative(closest.getValue(), 3);
            }
        }
        return entity.blockPosition();
    }

    public BlockPos getClosestEntranceBottomToEntity(Entity entity, RandomSource random) {
        Map.Entry<BlockPos, Direction> closest = null;
        for (Map.Entry<BlockPos, Direction> entry : this.entranceBottoms.entrySet()) {
            Vec3i vec = new Vec3i(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (closest == null || closest.getKey().distSqr(vec) > entry.getKey().distSqr(vec)) {
                closest = entry;
            }
        }
        return closest != null ? closest.getKey() : entity.blockPosition();
    }

    public Player getOwner(Level world) {
        if (hasOwner) {
            return world.getPlayerByUUID(ownerUUID);
        }
        return null;
    }

    public Map<BlockPos, Direction> getEntrances() {
        return entrances;
    }

    public Map<BlockPos, Direction> getEntranceBottoms() {
        return entranceBottoms;
    }

    private Map.Entry<BlockPos, Direction> getClosestEntrance(Entity entity) {
        Map.Entry<BlockPos, Direction> closest = null;
        for (Map.Entry<BlockPos, Direction> entry : this.entrances.entrySet()) {
            Vec3i vec = new Vec3i(Mth.floor(entity.getX()), Mth.floor(entity.getY()), Mth.floor(entity.getZ()));
            if (closest == null || closest.getKey().distSqr(vec) > entry.getKey().distSqr(vec)) {
                closest = entry;
            }
        }
        return closest;
    }

    public void setDefaultPlayerReputation(int defaultReputation) {
        for (UUID s : this.playerReputation.keySet()) {
            this.modifyPlayerReputation(s, defaultReputation);
        }
    }

    public boolean repopulate() {
        int roomCount = this.getAllRooms().size();
        return this.numMyrmex < Math.min(IafConfig.myrmexColonySize, roomCount * 9) && reproduces;
    }

    public void addMyrmex(EntityMyrmexBase myrmex) {
        if (!this.myrmexList.contains(myrmex.getUUID())) {
            this.myrmexList.add(myrmex.getUUID());
        }
    }

    public void removeRoom(BlockPos pos) {
        this.foodRooms.remove(pos);
        this.miscRooms.remove(pos);
        this.babyRooms.remove(pos);
        this.allRooms.remove(pos);
        this.getEntrances().remove(pos);
        this.getEntranceBottoms().remove(pos);
    }

    @Override
    public String toString() {
        return "MyrmexHive(x=" + this.center.getX() + ",y=" + this.center.getY() + ",z=" + this.center.getZ() + "), population=" + this.getNumMyrmex() + "\nUUID: " + hiveUUID;
    }

    public boolean equals(MyrmexHive hive) {
        return this.hiveUUID.equals(hive.hiveUUID);
    }

    class HiveAggressor {
        public LivingEntity agressor;
        public int agressionTime;
        public int agressionLevel;

        HiveAggressor(LivingEntity agressorIn, int agressionTimeIn, int agressionLevel) {
            this.agressor = agressorIn;
            this.agressionTime = agressionTimeIn;
            this.agressionLevel = agressionLevel;
        }
    }
}
