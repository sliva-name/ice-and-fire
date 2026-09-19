package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;


public class HomePosition {
    int x;
    int y;
    int z;
    BlockPos pos;
    String dimension;

    public HomePosition(CompoundTag compound) {
        read(compound);
    }

    public HomePosition(CompoundTag compound, Level world) {
        read(compound, world);
    }

    public HomePosition(ValueInput input) {
        read(input);
    }

    public HomePosition(ValueInput input, Level world) {
        read(input, world);
    }

    public HomePosition(BlockPos pos, Level world) {
        this(pos.getX(), pos.getY(), pos.getZ(), world);
    }

    public HomePosition(int x, int y, int z, Level world) {
        this.x = x;
        this.y = y;
        this.z = z;
        pos = new BlockPos(x, y, z);
        this.dimension = DragonUtils.getDimensionName(world);
    }

    public BlockPos getPosition() {
        return pos;
    }

    public String getDimension() {
        return dimension == null ? "" : dimension;
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putInt("HomeAreaX", this.x);
        compound.putInt("HomeAreaY", this.y);
        compound.putInt("HomeAreaZ", this.z);
        if (dimension != null)
            compound.putString("HomeDimension", this.dimension);
        return compound;
    }

    public HomePosition read(CompoundTag compound, Level world) {
        read(compound);
        if (this.dimension == null)
            this.dimension = DragonUtils.getDimensionName(world);
        return this;
    }

    public HomePosition read(CompoundTag compound) {
        if (compound.contains("HomeAreaX"))
            this.x = compound.getIntOr("HomeAreaX", 0);
        if (compound.contains("HomeAreaY"))
            this.y = compound.getIntOr("HomeAreaY", 0);
        if (compound.contains("HomeAreaZ"))
            this.z = compound.getIntOr("HomeAreaZ", 0);
        pos = new BlockPos(x, y, z);
        if (compound.contains("HomeDimension"))
            this.dimension = compound.getStringOr("HomeDimension", "");
        return this;
    }

    public void write(ValueOutput output) {
        output.putInt("HomeAreaX", this.x);
        output.putInt("HomeAreaY", this.y);
        output.putInt("HomeAreaZ", this.z);
        if (dimension != null) {
            output.putString("HomeDimension", this.dimension);
        }
    }

    public HomePosition read(ValueInput input, Level world) {
        read(input);
        if (this.dimension == null) {
            this.dimension = DragonUtils.getDimensionName(world);
        }
        return this;
    }

    public HomePosition read(ValueInput input) {
        this.x = input.getIntOr("HomeAreaX", 0);
        this.y = input.getIntOr("HomeAreaY", 0);
        this.z = input.getIntOr("HomeAreaZ", 0);
        pos = new BlockPos(x, y, z);
        String homeDimension = input.getStringOr("HomeDimension", "");
        if (!homeDimension.isEmpty()) {
            this.dimension = homeDimension;
        }
        return this;
    }
}

