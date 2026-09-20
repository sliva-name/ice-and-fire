package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityDreadPortal extends BlockEntity {
    private long age;
    private BlockPos exitPortal;
    private String exitDimension;
    private boolean exactTeleport;

    public TileEntityDreadPortal(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.DREAD_PORTAL.get(), pos, state);
    }

    public void setExit(ResourceKey<Level> dimension, BlockPos pos) {
        this.exitDimension = dimension.identifier().toString();
        this.exitPortal = pos.immutable();
        this.exactTeleport = true;
        this.setChanged();
    }

    @Nullable
    public BlockPos getExitPortal() {
        return this.exitPortal;
    }

    @Nullable
    public String getExitDimension() {
        return this.exitDimension;
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput compound) {
        super.saveAdditional(compound);
        compound.putLong("Age", this.age);
        if (this.exitPortal != null) {
            compound.putInt("ExitX", this.exitPortal.getX());
            compound.putInt("ExitY", this.exitPortal.getY());
            compound.putInt("ExitZ", this.exitPortal.getZ());
        }
        if (this.exitDimension != null) {
            compound.putString("ExitDimension", this.exitDimension);
        }
        if (this.exactTeleport) {
            compound.putBoolean("ExactTeleport", this.exactTeleport);
        }
    }

    @Override
    public void loadAdditional(@NotNull ValueInput compound) {
        super.loadAdditional(compound);
        this.age = compound.getLongOr("Age", 0L);
        this.exactTeleport = compound.getBooleanOr("ExactTeleport", false);
        String storedDim = compound.getStringOr("ExitDimension", "");
        this.exitDimension = storedDim.isEmpty() ? null : storedDim;
        if (this.exactTeleport) {
            this.exitPortal = new BlockPos(
                compound.getIntOr("ExitX", 0),
                compound.getIntOr("ExitY", 0),
                compound.getIntOr("ExitZ", 0)
            );
        } else {
            this.exitPortal = null;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityDreadPortal dreadPortal) {
        ++dreadPortal.age;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean shouldRenderFace(Direction face) {
        if (this.level == null) {
            return true;
        }
        return !this.level.getBlockState(this.worldPosition.relative(face)).is(IafBlockRegistry.DREAD_PORTAL.get());
    }
}
