package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IafCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IceAndFire.MODID);

    public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register("items", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + IceAndFire.MODID))
        .icon(() -> new ItemStack(IafItemRegistry.DRAGON_SKULL_FIRE.get()))
        .displayItems((parameters, output) -> {
            for (RegistryObject<Item> entry : IafItemRegistry.ITEMS.getEntries()) {
                Item item = entry.get();
                if (item instanceof BlockItem) {
                    continue;
                }
                if (item instanceof ItemBestiary) {
                    acceptBestiary(output);
                    continue;
                }
                if (item instanceof ItemHippogryphEgg) {
                    acceptHippogryphEggs(output, item);
                    continue;
                }
                if (item instanceof ItemMyrmexEgg) {
                    acceptMyrmexEggs(output, item);
                    continue;
                }
                output.accept(item);
            }
        })
        .build());

    public static final RegistryObject<CreativeModeTab> BLOCKS = TABS.register("blocks", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.iceandfire.blocks"))
        .icon(() -> new ItemStack(IafBlockRegistry.DRAGON_SCALE_RED.get()))
        .displayItems((parameters, output) -> {
            for (RegistryObject<Item> entry : IafItemRegistry.ITEMS.getEntries()) {
                if (entry.get() instanceof BlockItem) {
                    output.accept(entry.get());
                }
            }
        })
        .build());

    private static void acceptBestiary(CreativeModeTab.Output output) {
        output.accept(IafItemRegistry.BESTIARY.get());
        ItemStack stack = new ItemStack(IafItemRegistry.BESTIARY.get());
        int[] pages = new int[EnumBestiaryPages.values().length];
        for (int i = 0; i < pages.length; i++) {
            pages[i] = i;
        }
        IafItemData.update(stack, tag -> tag.putIntArray("Pages", pages));
        output.accept(stack);
    }

    private static void acceptHippogryphEggs(CreativeModeTab.Output output, Item item) {
        for (EnumHippogryphTypes type : EnumHippogryphTypes.values()) {
            ItemStack stack = new ItemStack(item);
            CompoundTag tag = new CompoundTag();
            tag.putInt("EggOrdinal", type.ordinal());
            IafItemData.write(stack, tag);
            output.accept(stack);
        }
    }

    private static void acceptMyrmexEggs(CreativeModeTab.Output output, Item item) {
        for (int i = 0; i < 5; i++) {
            ItemStack stack = new ItemStack(item);
            CompoundTag tag = new CompoundTag();
            tag.putInt("EggOrdinal", i);
            IafItemData.write(stack, tag);
            output.accept(stack);
        }
    }
}
