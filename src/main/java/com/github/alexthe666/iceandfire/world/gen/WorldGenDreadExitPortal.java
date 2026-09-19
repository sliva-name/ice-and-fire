package com.github.alexthe666.iceandfire.world.gen;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

import net.minecraft.util.RandomSource;

public class WorldGenDreadExitPortal {
    private static final Identifier STRUCTURE = Identifier.fromNamespaceAndPath(IceAndFire.MODID, "dread_exit_portal");

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        /*
        MinecraftServer server = worldIn.getMinecraftServer();
        TemplateManager templateManager = worldIn.getSaveHandler().getStructureTemplateManager();
        PlacementSettings settings = new PlacementSettings().setRotation(Rotation.NONE);
        Template template = templateManager.getTemplate(server, STRUCTURE);
        Biome biome = worldIn.getBiome(position);
        template.addBlocksToWorld(worldIn, position, new DreadPortalProcessor(position, settings, biome), settings, 2);

         */
        return true;
    }
}