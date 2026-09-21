package com.github.alexthe666.iceandfire;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.config.ConfigHolder;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.IafVillagerRegistry;
import com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry;
import com.github.alexthe666.iceandfire.inventory.IafContainerRegistry;
import com.github.alexthe666.iceandfire.item.IafCreativeTabs;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.loot.IafLootRegistry;
import com.github.alexthe666.iceandfire.message.*;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import com.github.alexthe666.iceandfire.recipe.IafRecipeSerializers;
import com.github.alexthe666.iceandfire.world.IafPlacementFilterRegistry;
import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.Priority;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(IceAndFire.MODID)
public class IceAndFire {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "iceandfire";
    public static final SimpleChannel NETWORK_WRAPPER = IafNetwork.create();
    public static boolean DEBUG = false;
    public static String VERSION = "UNKNOWN";
    public static CommonProxy PROXY = createProxy();

    public IceAndFire() {
        VERSION = "UNKNOWN";
        BusGroup modBus = FMLJavaModLoadingContext.get().getModBusGroup();


        final ModLoadingContext modLoadingContext = ModLoadingContext.get();

        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        PROXY.init();

        ServerStartedEvent.BUS.addListener(this::onServerStarted);
        ServerAboutToStartEvent.BUS.addListener(Priority.LOWEST, IceAndFire::onServerAboutToStart);

        IafEntityRegistry.ENTITIES.register(modBus);
        IafItemRegistry.ITEMS.register(modBus);
        IafCreativeTabs.TABS.register(modBus);
        IafBlockRegistry.BLOCKS.register(modBus);
        IafTileEntityRegistry.TYPES.register(modBus);
        IafPlacementFilterRegistry.PLACEMENT_MODIFIER_TYPES.register(modBus);
        IafWorldRegistry.FEATURES.register(modBus);
        IafWorldRegistry.STRUCTURES.register(modBus);
        IafWorldRegistry.STRUCTURE_PIECES.register(modBus);
        IafWorldRegistry.BIOME_MODIFIERS.register(modBus);
        IafLootRegistry.FUNCTIONS.register(modBus);
        IafContainerRegistry.CONTAINERS.register(modBus);
        IafRecipeSerializers.SERIALIZERS.register(modBus);
        IafRecipeSerializers.RECIPE_TYPES.register(modBus);
        IafProcessors.PROCESSORS.register(modBus);
        IafSoundRegistry.SOUNDS.register(modBus);

        IafVillagerRegistry.POI_TYPES.register(modBus);
        IafVillagerRegistry.PROFESSIONS.register(modBus);

        FMLCommonSetupEvent.getBus(modBus).addListener(this::setup);
        FMLLoadCompleteEvent.getBus(modBus).addListener(this::setupComplete);
        FMLClientSetupEvent.getBus(modBus).addListener(this::setupClient);
    }


    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        // Features and spawners inject from IafBiomeModifier (26.1 cannot write MobSpawnSettings.spawners).
        IafVillagerRegistry.addScribeHouses(event.getServer().registryAccess());
    }

    public void onServerStarted(ServerStartedEvent event) {
        LOGGER.info(IafWorldRegistry.LOADED_FEATURES);
        LOGGER.info(IafEntityRegistry.LOADED_ENTITIES);
    }

    private static CommonProxy createProxy() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return new CommonProxy();
        }
        try {
            return (CommonProxy) Class.forName("com.github.alexthe666.iceandfire.client.ClientProxy")
                .getDeclaredConstructor()
                .newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create client proxy", e);
        }
    }

    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendMSGToAll(MSG message) {
        NETWORK_WRAPPER.send(message, PacketDistributor.ALL.noArg());
    }

    public static <MSG> void sendMSGToPlayer(MSG message, ServerPlayer player) {
        NETWORK_WRAPPER.send(message, PacketDistributor.PLAYER.with(player));
    }

    private void setup(final FMLCommonSetupEvent event) {
        IafRecipeRegistry.preInit(event);
        event.enqueueWork(() -> {
            PROXY.setup();
            IafVillagerRegistry.setup();
            IafItemRegistry.setRepairMaterials();
            IafWorldRegistry.registerStructureConfiguredFeatures();
            IafWorldRegistry.registerConfiguredFeatures();
        });
    }

    private void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientInit());
    }

    private void setupComplete(final FMLLoadCompleteEvent event) {
        PROXY.postInit();
    }

}