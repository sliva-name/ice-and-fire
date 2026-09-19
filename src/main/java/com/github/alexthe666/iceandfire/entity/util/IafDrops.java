package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * 1.18 {@code spawnAtLocation} overloads. The extra number on an {@link ItemStack}
 * was a Y offset; on an {@link ItemLike} it was a count.
 */
public final class IafDrops {
    private IafDrops() {
    }

    public static ItemEntity spawn(Entity entity, ItemStack stack) {
        return spawn(entity, stack, 0.0F);
    }

    public static ItemEntity spawn(Entity entity, ItemStack stack, float yOffset) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return null;
        }
        return entity.spawnAtLocation(server, stack, yOffset);
    }

    public static ItemEntity spawn(Entity entity, ItemLike item) {
        return spawn(entity, item, 1);
    }

    public static ItemEntity spawn(Entity entity, ItemLike item, int count) {
        if (!(entity.level() instanceof ServerLevel server)) {
            return null;
        }
        return entity.spawnAtLocation(server, new ItemStack(item, count));
    }
}
