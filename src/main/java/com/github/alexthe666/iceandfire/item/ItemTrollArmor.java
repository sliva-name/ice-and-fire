package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemTrollArmor extends Item implements IafArmorIdentity {

    public EnumTroll troll;
    private final EquipmentSlot armorSlot;

    public ItemTrollArmor(EnumTroll troll, CustomArmorMaterial material, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.troll = troll;
        this.armorSlot = slot;
    }

    public static String getName(EnumTroll troll, EquipmentSlot slot) {
        return "%s_troll_leather_%s".formatted(troll.name().toLowerCase(Locale.ROOT), getArmorPart(slot));
    }

    @Override
    public CustomArmorMaterial iafMaterial() {
        return troll.material;
    }


    private static String getArmorPart(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "helmet";
            case CHEST -> "chestplate";
            case LEGS -> "leggings";
            case FEET -> "boots";
            default -> "";
        };
    }

    // Textures: assets/iceandfire/equipment/<material>.json (1.18 getArmorTexture is gone in 26.1).
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        IafArmors.initClient(this, consumer);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull net.minecraft.world.item.component.TooltipDisplay display, @NotNull java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.troll_leather_armor_" + getArmorPart(armorSlot) + ".desc").withStyle(ChatFormatting.GREEN));
    }
}
