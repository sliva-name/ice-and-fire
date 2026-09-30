package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityLectern;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemBestiary;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ContainerLectern extends AbstractContainerMenu {
    private final Container tileFurnace;
    private final int[] possiblePagesInt = new int[3];

    public ContainerLectern(int i, Inventory playerInventory) {
        this(i, new SimpleContainer(2), playerInventory, new SimpleContainerData(0));
    }


    public ContainerLectern(int id, Container furnaceInventory, Inventory playerInventory, ContainerData vars) {
        super(IafContainerRegistry.IAF_LECTERN_CONTAINER.get(), id);
        this.tileFurnace = furnaceInventory;
        this.addSlot(new SlotLectern(furnaceInventory, 0, 15, 47) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return super.mayPlace(stack) && !stack.isEmpty() && stack.getItem() instanceof ItemBestiary;
            }
        });
        this.addSlot(new Slot(furnaceInventory, 1, 35, 47) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return super.mayPlace(stack) && !stack.isEmpty() && stack.getItem() == IafItemRegistry.MANUSCRIPT.get();
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    private int getPageField(int i) {
        // The server menu wraps the real lectern; the client menu only has a stub and reads the referenced tile.
        BlockEntity lecternTile = this.tileFurnace instanceof TileEntityLectern ? (TileEntityLectern) this.tileFurnace : IceAndFire.PROXY.getRefrencedTE();
        if (lecternTile instanceof TileEntityLectern lectern) {
            return lectern.selectedPages[i] == null ? -1 : lectern.selectedPages[i].ordinal();
        }
        return -1;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    public void onUpdate() {
        possiblePagesInt[0] = getPageField(0);
        possiblePagesInt[1] = getPageField(1);
        possiblePagesInt[2] = getPageField(2);
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return this.tileFurnace.stillValid(playerIn);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.tileFurnace.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.tileFurnace.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(0).mayPlace(itemstack1) && !this.getSlot(0).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(1).mayPlace(itemstack1) && !this.getSlot(1).hasItem()) {
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.tileFurnace.getContainerSize() <= 5 || !this.moveItemStackTo(itemstack1, 5, this.tileFurnace.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    public int getManuscriptAmount() {
        ItemStack itemstack = this.tileFurnace.getItem(1);
        return itemstack.isEmpty() || itemstack.getItem() != IafItemRegistry.MANUSCRIPT.get() ? 0 : itemstack.getCount();
    }

    public EnumBestiaryPages[] getPossiblePages() {
        possiblePagesInt[0] = getPageField(0);
        possiblePagesInt[1] = getPageField(1);
        possiblePagesInt[2] = getPageField(2);
        EnumBestiaryPages[] pages = new EnumBestiaryPages[3];
        if (this.tileFurnace.getItem(0).getItem() == IafItemRegistry.BESTIARY.get()) {
            if (possiblePagesInt[0] < 0) {
                pages[0] = null;
            } else {
                pages[0] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length - 1, possiblePagesInt[0])];
            }
            if (possiblePagesInt[1] < 0) {
                pages[1] = null;
            } else {
                pages[1] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length - 1, possiblePagesInt[1])];
            }
            if (possiblePagesInt[2] < 0) {
                pages[2] = null;
            } else {
                pages[2] = EnumBestiaryPages.values()[Math.min(EnumBestiaryPages.values().length - 1, possiblePagesInt[2])];
            }
        }
        return pages;
    }

    @Override
    public boolean clickMenuButton(Player playerIn, int id) {
        if (id < 0 || id >= possiblePagesInt.length) {
            return false;
        }
        possiblePagesInt[0] = getPageField(0);
        possiblePagesInt[1] = getPageField(1);
        possiblePagesInt[2] = getPageField(2);
        ItemStack bestiary = this.tileFurnace.getItem(0);
        ItemStack manuscripts = this.tileFurnace.getItem(1);
        int cost = 3;

        if (bestiary.getItem() != IafItemRegistry.BESTIARY.get() || this.possiblePagesInt[id] < 0) {
            return false;
        }
        if (!playerIn.isCreative() && (manuscripts.getItem() != IafItemRegistry.MANUSCRIPT.get() || manuscripts.getCount() < cost)) {
            return false;
        }
        EnumBestiaryPages page = getPossiblePages()[id];
        if (page == null) {
            return false;
        }
        if (playerIn.level().isClientSide()) {
            // Client prediction only: the server unlocks the page, charges the manuscripts and syncs the result.
            return true;
        }
        if (!(this.tileFurnace instanceof TileEntityLectern lectern)) {
            return false;
        }
        EnumBestiaryPages.addPage(page, bestiary);
        if (!playerIn.isCreative()) {
            manuscripts.shrink(cost);
            if (manuscripts.isEmpty()) {
                this.tileFurnace.setItem(1, ItemStack.EMPTY);
            }
        }
        this.tileFurnace.setItem(0, bestiary);
        this.tileFurnace.setChanged();
        lectern.randomizePages(bestiary, manuscripts);
        this.slotsChanged(this.tileFurnace);
        playerIn.level().playSound(null, playerIn.blockPosition(), IafSoundRegistry.BESTIARY_PAGE, SoundSource.BLOCKS, 1.0F, playerIn.level().getRandom().nextFloat() * 0.1F + 0.9F);
        return true;
    }
}
