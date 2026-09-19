package com.github.alexthe666.iceandfire.item;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 1.18 {@code ItemStack} NBT accessors. Mutations go through
 * {@link CustomData#update} so they persist on {@link DataComponents#CUSTOM_DATA}.
 */
public final class IafItemData {
    private IafItemData() {
    }

    public static boolean has(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA)
            && !stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).isEmpty();
    }

    public static void ensure(ItemStack stack) {
        if (!has(stack)) {
            write(stack, new CompoundTag());
        }
    }

    public static CompoundTag copy(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static void write(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> editor) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, editor);
    }
}
