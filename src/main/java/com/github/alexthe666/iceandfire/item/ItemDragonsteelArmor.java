package com.github.alexthe666.iceandfire.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import static com.github.alexthe666.iceandfire.item.IafItemRegistry.*;

public class ItemDragonsteelArmor extends Item implements IafArmorIdentity, IProtectAgainstDragonItem {

    private final com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial;
    private final EquipmentSlot armorSlot;

    public ItemDragonsteelArmor(com.github.alexthe666.citadel.server.item.CustomArmorMaterial material, int renderIndex, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.iafMaterial = material;
        this.armorSlot = slot;
    }

    @Override
    public com.github.alexthe666.citadel.server.item.CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }

    public int getDefense() {
        return this.iafMaterial.getDefenseForSlot(this.armorSlot);
    }

    // Textures: assets/iceandfire/equipment/dragonsteel_<type>.json (1.18 getArmorTexture is gone in 26.1).
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(com.github.alexthe666.iceandfire.client.render.IafArmorRenderProperties.armorModel((stack, inner) -> {
            if (iafMaterial == DRAGONSTEEL_FIRE_ARMOR_MATERIAL)
                return new com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelFireArmor(inner);
            if (iafMaterial == DRAGONSTEEL_ICE_ARMOR_MATERIAL)
                return new com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelIceArmor(inner);
            return new com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelLightningArmor(inner);
        }));
    }
}
