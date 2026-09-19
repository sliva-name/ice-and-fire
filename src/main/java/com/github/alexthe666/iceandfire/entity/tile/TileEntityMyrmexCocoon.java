package com.github.alexthe666.iceandfire.entity.tile;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityMyrmexCocoon extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> chestContents = NonNullList.withSize(18, ItemStack.EMPTY);

    public TileEntityMyrmexCocoon(BlockPos pos, BlockState state) {
        super(IafTileEntityRegistry.MYRMEX_COCOON.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 18;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void loadAdditional(@NotNull ValueInput compound) {
        super.loadAdditional(compound);
        this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(compound)) {
            ContainerHelper.loadAllItems(compound, this.chestContents);
        }
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput compound) {
        if (!this.trySaveLootTable(compound)) {
            ContainerHelper.saveAllItems(compound, this.chestContents);
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.myrmex_cocoon");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new ChestMenu(MenuType.GENERIC_9x2, id, player, this, 2);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ChestMenu(MenuType.GENERIC_9x2, id, playerInventory, this, 2);
    }


    @Override
    public int getMaxStackSize() {
        return 64;
    }


    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> itemsIn) {
        this.chestContents = itemsIn;
    }

    public void startOpen(Player player) {
        this.unpackLootTable(null);
        player.level().playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.SLIME_JUMP, SoundSource.BLOCKS, 1, 1, false);
    }

    public void stopOpen(Player player) {
        this.unpackLootTable(null);
        player.level().playLocalSound(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.BLOCKS, 1, 1, false);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        if (packet.getTag() != null && getLevel() != null) {
            this.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, getLevel().registryAccess(), packet.getTag()));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    public boolean isFull(ItemStack heldStack) {
        for (ItemStack itemstack : chestContents) {
            if (itemstack.isEmpty() || heldStack != null && !heldStack.isEmpty() && ItemStack.isSameItem(itemstack, heldStack) && itemstack.getCount() + heldStack.getCount() < itemstack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
}
