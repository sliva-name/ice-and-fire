package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.world.gen.processor.VillageHouseProcessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import com.github.alexthe666.iceandfire.entity.util.IafItemListing;
import com.github.alexthe666.iceandfire.entity.util.IafOffers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.Collectors;

public class IafVillagerRegistry {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, IceAndFire.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, IceAndFire.MODID);
    public static final RegistryObject<PoiType> SCRIBE_POI = POI_TYPES.register("scribe", () -> new PoiType(ImmutableSet.copyOf(IafBlockRegistry.LECTERN.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static final RegistryObject<VillagerProfession> SCRIBE = PROFESSIONS.register("scribe", () -> new VillagerProfession(
        net.minecraft.network.chat.Component.translatable("entity.iceandfire.villager.scribe"),
        holder -> holder.value() == SCRIBE_POI.get(),
        holder -> holder.value() == SCRIBE_POI.get(),
        ImmutableSet.of(),
        ImmutableSet.of(),
        SoundEvents.VILLAGER_WORK_LIBRARIAN,
        new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>()));

    private static final String[] VILLAGE_TYPES = new String[]{"plains", "desert", "snowy", "savanna", "taiga"};
    private static final Holder<StructureProcessorList> HOUSE_PROCESSOR = Holder.direct(genVillageHouseProcessor());

    private static StructureProcessorList genVillageHouseProcessor() {
        RuleProcessor mossify = new RuleProcessor(ImmutableList.of(new ProcessorRule(new RandomBlockMatchTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.defaultBlockState())));
        return new StructureProcessorList(ImmutableList.of(mossify, new VillageHouseProcessor()));
    }

    public static void setup() {
        // 26.1 village pools are registry/datapack objects; scribe house injection stays in addStructureToPool once those APIs are wired.
        if (IafConfig.villagerHouseWeight > 0) {
            for (String type : VILLAGE_TYPES) {
                addStructureToPool(Identifier.parse("village/" + type + "/houses"), Identifier.fromNamespaceAndPath("iceandfire", "village/" + type + "_scriber_1"), IafConfig.villagerHouseWeight);
            }
        }
    }

    public static void addScribeTrades(Int2ObjectMap<List<IafItemListing>> trades) {
        final float emeraldForItemsMultiplier = 0.05F; //Values taken from VillagerTrades.java
        final float itemForEmeraldMultiplier = 0.05F;
        final float rareItemForEmeraldMultiplier = 0.2F;
        trades.get(1).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 1), new ItemStack(IafItemRegistry.MANUSCRIPT.get(), 4), 25, 2, emeraldForItemsMultiplier));
        trades.get(1).add((entity, random) -> IafOffers.of(new ItemStack(Items.BOOKSHELF, 3), new ItemStack(Items.EMERALD, 1), 8, 3, itemForEmeraldMultiplier));
        trades.get(1).add((entity, random) -> IafOffers.of(new ItemStack(Items.PAPER, 15), new ItemStack(Items.EMERALD, 2), 4, 4, itemForEmeraldMultiplier));
        trades.get(1).add((entity, random) -> IafOffers.of(new ItemStack(IafBlockRegistry.ASH.get(), 10), new ItemStack(Items.EMERALD, 1), 8, 4, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.SILVER_INGOT.get(), 5), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(IafBlockRegistry.FIRE_LILY.get(), 8), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(IafBlockRegistry.LIGHTNING_LILY.get(), 7), new ItemStack(Items.EMERALD, 3), 2, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 3), new ItemStack(IafBlockRegistry.FROST_LILY.get(), 4), 3, 3, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlockRegistry.DRAGON_ICE_SPIKES.get(), 7), 2, 3, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.SAPPHIRE_GEM.get()), new ItemStack(Items.EMERALD, 2), 30, 3, rareItemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlockRegistry.JAR_EMPTY.get(), 1), 3, 4, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 2), new ItemStack(IafItemRegistry.MYRMEX_DESERT_RESIN.get(), 1), 40, 2, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 2), new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_RESIN.get(), 1), 40, 2, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.AMYTHEST_GEM.get()), new ItemStack(Items.EMERALD, 3), 20, 3, rareItemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.DRAGON_BONE.get(), 6), new ItemStack(Items.EMERALD, 1), 7, 4, itemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.CHAIN.get(), 2), new ItemStack(Items.EMERALD, 3), 4, 2, itemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItemRegistry.PIXIE_DUST.get(), 2), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItemRegistry.FIRE_DRAGON_FLESH.get(), 2), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 7), new ItemStack(IafItemRegistry.ICE_DRAGON_FLESH.get(), 1), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItemRegistry.LIGHTNING_DRAGON_FLESH.get(), 1), 8, 3, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 10), new ItemStack(IafItemRegistry.DRAGON_BONE.get(), 2), 20, 5, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItemRegistry.SHINY_SCALES.get(), 1), 5, 2, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.DREAD_SHARD.get(), 5), new ItemStack(Items.EMERALD, 1), 10, 4, itemForEmeraldMultiplier));
        trades.get(4).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER.get(), 12), 3, 6, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItemRegistry.TROLL_TUSK.get(), 12), 7, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 15), new ItemStack(IafItemRegistry.SERPENT_FANG.get(), 3), 20, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> IafOffers.of(new ItemStack(Items.EMERALD, 12), new ItemStack(IafItemRegistry.HYDRA_FANG.get(), 1), 20, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> IafOffers.of(new ItemStack(IafItemRegistry.ECTOPLASM.get(), 6), new ItemStack(Items.EMERALD, 1), 7, 3, itemForEmeraldMultiplier));
    }

    private static void addStructureToPool(Identifier pool, Identifier toAdd, int weight) {
        // 1.18 mutated BuiltinRegistries.TEMPLATE_POOL in-place. 26.1 pools are frozen registry objects.
        if (HOUSE_PROCESSOR == null || weight <= 0 || pool == null || toAdd == null) {
            return;
        }
    }

}
