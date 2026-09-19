package com.github.alexthe666.iceandfire.world.structure;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

/**
 * 1.18 pool keys. Generation now goes through {@link IafJigsawStructures}.
 */
public class Pool {
    static final ResourceKey<StructureTemplatePool> dread_pool = IafJigsawStructures.DREAD_POOL;
    static final ResourceKey<StructureTemplatePool> graveyard_pool = IafJigsawStructures.GRAVEYARD_POOL;
    static final ResourceKey<StructureTemplatePool> gorgon_pool = IafJigsawStructures.GORGON_POOL;
}
