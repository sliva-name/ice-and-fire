package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class VillageHouseProcessor extends StructureProcessor {

    public static final Identifier LOOT = Identifier.fromNamespaceAndPath("iceandfire", "chest/village_scribe");
    public static final VillageHouseProcessor INSTANCE = new VillageHouseProcessor();
    public static final MapCodec<VillageHouseProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    public VillageHouseProcessor() {
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader worldReader, @NotNull BlockPos pos, @NotNull BlockPos pos2, StructureTemplate.@NotNull StructureBlockInfo infoIn1, StructureTemplate.StructureBlockInfo infoIn2, @NotNull StructurePlaceSettings settings, @Nullable StructureTemplate template) {
        RandomSource random = settings.getRandom(infoIn2.pos());
        if (infoIn2.state().getBlock() == Blocks.CHEST) {
            CompoundTag tag = new CompoundTag();
            tag.putString("LootTable", LOOT.toString());
            tag.putLong("LootTableSeed", random.nextLong());
            return new StructureTemplate.StructureBlockInfo(infoIn2.pos(), infoIn2.state(), tag);
        }
        return infoIn2;
    }

    @Override
    protected @NotNull StructureProcessorType getType() {
        return IafProcessors.VILLAGEHOUSEPROCESSOR.get();
    }
}
