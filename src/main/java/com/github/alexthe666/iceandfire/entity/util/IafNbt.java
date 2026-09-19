package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;
import java.util.UUID;

/**
 * 26.1 ValueInput/Output helpers that keep the 1.18 key names and defaults.
 */
public final class IafNbt {
    private IafNbt() {
    }

    public static void putUuid(ValueOutput output, String key, UUID uuid) {
        output.store(key, UUIDUtil.CODEC, uuid);
    }

    public static Optional<UUID> getUuid(ValueInput input, String key) {
        return input.read(key, UUIDUtil.CODEC);
    }

    public static void saveItems(ValueOutput output, String key, Container inventory) {
        ValueOutput.ValueOutputList list = output.childrenList(key);
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                ValueOutput child = list.addChild();
                child.putByte("Slot", (byte) i);
                child.store(ItemStack.MAP_CODEC, stack);
            }
        }
    }

    public static void loadItems(ValueInput input, String key, Container inventory, int maxSlotInclusive) {
        for (ValueInput child : input.childrenListOrEmpty(key)) {
            int slot = child.getByteOr("Slot", (byte) 0) & 255;
            if (slot <= maxSlotInclusive && slot < inventory.getContainerSize()) {
                child.read(ItemStack.MAP_CODEC).ifPresent(stack -> inventory.setItem(slot, stack));
            }
        }
    }

    public static void loadItems(ValueInput input, String key, Container inventory) {
        loadItems(input, key, inventory, inventory.getContainerSize() - 1);
    }
}
