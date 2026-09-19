package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.world.item.Item;

public class ItemFishingSpear extends Item {

    public ItemFishingSpear() {
        super(IafItemRegistry.defaultBuilder().durability(64));
    }
}
