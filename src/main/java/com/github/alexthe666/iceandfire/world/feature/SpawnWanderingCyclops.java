package com.github.alexthe666.iceandfire.world.feature;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class SpawnWanderingCyclops extends Feature<NoneFeatureConfiguration> {

    public SpawnWanderingCyclops(Codec<NoneFeatureConfiguration> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel worldIn = context.level();
        net.minecraft.util.RandomSource rand = context.random();
        BlockPos position = context.origin();

        position = worldIn.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, position.offset(8, 0, 8));

        if (IafConfig.generateWanderingCyclops && IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position)) {
            if (rand.nextInt(IafConfig.spawnWanderingCyclopsChance + 1) == 0 && rand.nextInt(12) == 0) {
                EntityCyclops cyclops = IafEntityRegistry.CYCLOPS.get().create(worldIn.getLevel(), net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
                cyclops.setPos(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                cyclops.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(position), EntitySpawnReason.SPAWNER, null);
                worldIn.addFreshEntity(cyclops);
                for (int i = 0; i < 3 + rand.nextInt(3); i++) {
                    Sheep sheep = EntityType.SHEEP.create(worldIn.getLevel(), net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
                    sheep.setPos(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                    sheep.setColor(Sheep.getRandomSheepColor(worldIn, position));
                    worldIn.addFreshEntity(sheep);
                }
            }
        }

        return false;
    }
}
