package com.github.alexthe666.iceandfire.loot;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IafLootRegistry {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> FUNCTIONS =
        DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, IceAndFire.MODID);

    public static final MapCodec<CustomizeToDragon> CUSTOMIZE_TO_DRAGON = CustomizeToDragon.CODEC;
    public static final MapCodec<CustomizeToSeaSerpent> CUSTOMIZE_TO_SERPENT = CustomizeToSeaSerpent.CODEC;

    public static final RegistryObject<MapCodec<? extends LootItemFunction>> CUSTOMIZE_TO_DRAGON_TYPE =
        FUNCTIONS.register("customize_to_dragon", () -> CUSTOMIZE_TO_DRAGON);
    public static final RegistryObject<MapCodec<? extends LootItemFunction>> CUSTOMIZE_TO_SERPENT_TYPE =
        FUNCTIONS.register("customize_to_sea_serpent", () -> CUSTOMIZE_TO_SERPENT);
}
