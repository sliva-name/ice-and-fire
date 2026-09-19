package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.armor.ModelFireDragonScaleArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelIceDragonScaleArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelLightningDragonScaleArmor;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemScaleArmor extends Item implements IafArmorIdentity, IProtectAgainstDragonItem {

    public EnumDragonArmor armor_type;
    public EnumDragonEgg eggType;
    private final CustomArmorMaterial iafMaterial;
    private final EquipmentSlot armorSlot;

    public ItemScaleArmor(EnumDragonEgg eggType, EnumDragonArmor armorType, CustomArmorMaterial material, EquipmentSlot slot) {
        super(IafArmors.properties(material, slot));
        this.armor_type = armorType;
        this.eggType = eggType;
        this.iafMaterial = material;
        this.armorSlot = slot;
    }

    @Override
    public CustomArmorMaterial iafMaterial() {
        return iafMaterial;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(switch (this.armorSlot) {
            case HEAD -> "item.iceandfire.dragon_helmet";
            case CHEST -> "item.iceandfire.dragon_chestplate";
            case LEGS -> "item.iceandfire.dragon_leggings";
            case FEET -> "item.iceandfire.dragon_boots";
            default -> "item.iceandfire.dragon_helmet";
        });
    }

    // Textures: assets/iceandfire/equipment/armor_dragon_scales<N>.json (1.18 getArmorTexture is gone in 26.1).
    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(com.github.alexthe666.iceandfire.client.render.IafArmorRenderProperties.armorModel((stack, inner) -> {
            DragonType dragonType = this.armor_type.eggType.dragonType;
            if (DragonType.FIRE == dragonType)
                return new ModelFireDragonScaleArmor(inner);
            if (DragonType.ICE == dragonType)
                return new ModelIceDragonScaleArmor(inner);
            if (DragonType.LIGHTNING == dragonType)
                return new ModelLightningDragonScaleArmor(inner);
            return null;
        }));
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("dragon." + eggType.toString().toLowerCase()).withStyle(eggType.color));
        tooltip.accept(Component.translatable("item.dragonscales_armor.desc").withStyle(ChatFormatting.GRAY));
    }
}
