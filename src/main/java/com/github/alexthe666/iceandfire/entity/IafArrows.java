package com.github.alexthe666.iceandfire.entity;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 26.2 {@code AbstractArrow} treats {@link ItemStack#EMPTY} as an invalid fired-from
 * weapon on dedicated server ({@code IllegalArgumentException: Invalid weapon
 * firing an arrow}). {@code null} means "no weapon".
 */
final class IafArrows {
    private IafArrows() {
    }

    static @Nullable ItemStack firedFrom(ItemStack weapon) {
        return weapon == null || weapon.isEmpty() ? null : weapon;
    }
}
