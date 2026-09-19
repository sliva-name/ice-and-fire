package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class TileEntityGhostChest extends ChestBlockEntity {

    public TileEntityGhostChest(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.GHOST_CHEST.get(), pos, state);
    }

    @Override
    public void loadAdditional(@NotNull ValueInput nbt) {
        super.loadAdditional(nbt);
    }

    @Override
    public void startOpen(@NotNull net.minecraft.world.entity.ContainerUser user) {
        super.startOpen(user);
        Level world = this.getLevel();
        if (world == null || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        net.minecraft.world.entity.LivingEntity opener = user.getLivingEntity();
        Player player = opener instanceof Player p ? p : null;
        EntityGhost ghost = IafEntityRegistry.GHOST.get().create(world, EntitySpawnReason.SPAWNER);
        if (ghost == null) {
            return;
        }
        ghost.snapTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F,
            ThreadLocalRandom.current().nextFloat() * 360F, 0);
        if (world instanceof ServerLevel server) {
            ghost.finalizeSpawn(server, server.getCurrentDifficultyAt(this.worldPosition), EntitySpawnReason.SPAWNER, null);
            if (player != null && !player.isCreative()) {
                ghost.setTarget(player);
            }
            ghost.setPersistenceRequired();
            world.addFreshEntity(ghost);
        }
        ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
        ghost.setHomeTo(this.worldPosition, 4);
        ghost.setFromChest(true);
    }

    @Override
    protected void signalOpenCount(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int p_155336_, int p_155337_) {
        super.signalOpenCount(level, pos, state, p_155336_, p_155337_);
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }
}
