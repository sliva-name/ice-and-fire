package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class IafContainerRegistry {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister
        .create(ForgeRegistries.MENU_TYPES, IceAndFire.MODID);

    public static final RegistryObject<MenuType<ContainerLectern>> IAF_LECTERN_CONTAINER = register(
        () -> new MenuType<>(ContainerLectern::new, FeatureFlags.DEFAULT_FLAGS), "iaf_lectern");
    public static final RegistryObject<MenuType<ContainerPodium>> PODIUM_CONTAINER = register(
        () -> new MenuType<>(ContainerPodium::new, FeatureFlags.DEFAULT_FLAGS), "podium");
    public static final RegistryObject<MenuType<ContainerDragon>> DRAGON_CONTAINER = register(
        () -> new MenuType<>(ContainerDragon::new, FeatureFlags.DEFAULT_FLAGS), "dragon");
    public static final RegistryObject<MenuType<ContainerHippogryph>> HIPPOGRYPH_CONTAINER = register(
        () -> new MenuType<>(ContainerHippogryph::new, FeatureFlags.DEFAULT_FLAGS), "hippogryph");
    public static final RegistryObject<MenuType<HippocampusContainerMenu>> HIPPOCAMPUS_CONTAINER = register(
        () -> new MenuType<>(HippocampusContainerMenu::new, FeatureFlags.DEFAULT_FLAGS), "hippocampus");
    public static final RegistryObject<MenuType<ContainerDragonForge>> DRAGON_FORGE_CONTAINER = register(
        () -> new MenuType<>(ContainerDragonForge::new, FeatureFlags.DEFAULT_FLAGS), "dragon_forge");

    public static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(Supplier<MenuType<C>> type,
                                                                                         String name) {
        return CONTAINERS.register(name, type);
    }

}
