package com.github.alexthe666.iceandfire.entity.tile;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class IafTileEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister
        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, IceAndFire.MODID);

    //@formatter:off
    public static final RegistryObject<BlockEntityType<TileEntityLectern>> IAF_LECTERN = registerTileEntity(TileEntityLectern::new, "lectern", IafBlockRegistry.LECTERN);
    public static final RegistryObject<BlockEntityType<TileEntityPodium>> PODIUM = registerTileEntity(TileEntityPodium::new, "podium", IafBlockRegistry.PODIUM_OAK, IafBlockRegistry.PODIUM_BIRCH, IafBlockRegistry.PODIUM_SPRUCE, IafBlockRegistry.PODIUM_JUNGLE, IafBlockRegistry.PODIUM_DARK_OAK, IafBlockRegistry.PODIUM_ACACIA);
    public static final RegistryObject<BlockEntityType<TileEntityEggInIce>> EGG_IN_ICE = registerTileEntity(TileEntityEggInIce::new, "egginice", IafBlockRegistry.EGG_IN_ICE);
    public static final RegistryObject<BlockEntityType<TileEntityPixieHouse>> PIXIE_HOUSE = registerTileEntity(TileEntityPixieHouse::new, "pixie_house", IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_RED, IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_BROWN, IafBlockRegistry.PIXIE_HOUSE_OAK, IafBlockRegistry.PIXIE_HOUSE_BIRCH, IafBlockRegistry.PIXIE_HOUSE_SPRUCE, IafBlockRegistry.PIXIE_HOUSE_DARK_OAK);
    public static final RegistryObject<BlockEntityType<TileEntityJar>> PIXIE_JAR = registerTileEntity(TileEntityJar::new, "pixie_jar", IafBlockRegistry.JAR_EMPTY, IafBlockRegistry.JAR_PIXIE_0, IafBlockRegistry.JAR_PIXIE_1, IafBlockRegistry.JAR_PIXIE_2, IafBlockRegistry.JAR_PIXIE_3, IafBlockRegistry.JAR_PIXIE_4);
    public static final RegistryObject<BlockEntityType<TileEntityMyrmexCocoon>> MYRMEX_COCOON = registerTileEntity(TileEntityMyrmexCocoon::new, "myrmex_cocoon", IafBlockRegistry.DESERT_MYRMEX_COCOON, IafBlockRegistry.JUNGLE_MYRMEX_COCOON);
    public static final RegistryObject<BlockEntityType<TileEntityDragonforge>> DRAGONFORGE_CORE = registerTileEntity(TileEntityDragonforge::new, "dragonforge_core", IafBlockRegistry.DRAGONFORGE_FIRE_CORE, IafBlockRegistry.DRAGONFORGE_ICE_CORE, IafBlockRegistry.DRAGONFORGE_FIRE_CORE_DISABLED, IafBlockRegistry.DRAGONFORGE_ICE_CORE_DISABLED, IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE, IafBlockRegistry.DRAGONFORGE_LIGHTNING_CORE_DISABLED);
    public static final RegistryObject<BlockEntityType<TileEntityDragonforgeBrick>> DRAGONFORGE_BRICK = registerTileEntity(TileEntityDragonforgeBrick::new, "dragonforge_brick", IafBlockRegistry.DRAGONFORGE_FIRE_BRICK, IafBlockRegistry.DRAGONFORGE_ICE_BRICK, IafBlockRegistry.DRAGONFORGE_LIGHTNING_BRICK);
    public static final RegistryObject<BlockEntityType<TileEntityDragonforgeInput>> DRAGONFORGE_INPUT = registerTileEntity(TileEntityDragonforgeInput::new, "dragonforge_input", IafBlockRegistry.DRAGONFORGE_FIRE_INPUT, IafBlockRegistry.DRAGONFORGE_ICE_INPUT, IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT);
    public static final RegistryObject<BlockEntityType<TileEntityDreadPortal>> DREAD_PORTAL = registerTileEntity(TileEntityDreadPortal::new, "dread_portal", IafBlockRegistry.DREAD_PORTAL);
    public static final RegistryObject<BlockEntityType<TileEntityDreadSpawner>> DREAD_SPAWNER = registerTileEntity(TileEntityDreadSpawner::new, "dread_spawner", IafBlockRegistry.DREAD_SPAWNER);
    public static final RegistryObject<BlockEntityType<TileEntityGhostChest>> GHOST_CHEST = registerTileEntity(TileEntityGhostChest::new, "ghost_chest", IafBlockRegistry.GHOST_CHEST);


    @SafeVarargs
    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTileEntity(
            BlockEntityType.BlockEntitySupplier<T> factory, String entityName, RegistryObject<? extends Block>... blocks) {
        return TYPES.register(entityName, () -> {
            java.util.LinkedHashSet<Block> resolved = new java.util.LinkedHashSet<>();
            for (RegistryObject<? extends Block> block : blocks) {
                resolved.add(block.get());
            }
            return new BlockEntityType<>(factory, Set.copyOf(resolved));
        });
    }
}
