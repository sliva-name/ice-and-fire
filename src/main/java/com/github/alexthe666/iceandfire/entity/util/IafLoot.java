package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * 1.18 {@code getDefaultLootTable()} returned {@link Identifier}.
 * 26.1 drop tables are {@link ResourceKey}s; paths stay {@code iceandfire:entities/...}.
 */
public final class IafLoot {
    private IafLoot() {
    }

    public static ResourceKey<LootTable> table(Identifier id) {
        return ResourceKey.create(Registries.LOOT_TABLE, id);
    }

    public static ResourceKey<LootTable> table(String path) {
        return table(Identifier.fromNamespaceAndPath("iceandfire", path));
    }
}
