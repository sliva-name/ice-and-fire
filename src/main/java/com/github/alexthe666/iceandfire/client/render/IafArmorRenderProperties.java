package com.github.alexthe666.iceandfire.client.render;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.client.model.armor.ArmorModelBase;
import com.github.alexthe666.iceandfire.client.model.armor.ModelCopperArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelDeathWormArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelFireArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelIceArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelDragonsteelLightningArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelFireDragonScaleArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelIceDragonScaleArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelLightningDragonScaleArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelSeaSerpentArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelSilverArmor;
import com.github.alexthe666.iceandfire.client.model.armor.ModelTrollArmor;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.item.IafArmorIdentity;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemCopperArmor;
import com.github.alexthe666.iceandfire.item.ItemDeathwormArmor;
import com.github.alexthe666.iceandfire.item.ItemDragonsteelArmor;
import com.github.alexthe666.iceandfire.item.ItemScaleArmor;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemSilverArmor;
import com.github.alexthe666.iceandfire.item.ItemTrollArmor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 26.1 replacement for the 1.18 {@code IItemRenderProperties#getArmorModel} anonymous classes.
 * <p>
 * Vanilla now bakes one stripped model per armor slot, so a replacement full-body model has to
 * hide the parts the slot does not cover itself (1.18 {@code HumanoidArmorLayer#setPartVisibility}).
 * The equipment texture still comes from {@code assets/iceandfire/equipment/<material>.json}.
 */
public final class IafArmorRenderProperties {
    private IafArmorRenderProperties() {
    }

    public static void register(Item item, Consumer<IClientItemExtensions> consumer) {
        if (item instanceof ItemTrollArmor) {
            consumer.accept(armorModel((stack, inner) -> new ModelTrollArmor(inner)));
        } else if (item instanceof ItemCopperArmor) {
            consumer.accept(armorModel((stack, inner) -> new ModelCopperArmor(inner)));
        } else if (item instanceof ItemSilverArmor) {
            consumer.accept(armorModel((stack, inner) -> new ModelSilverArmor(inner)));
        } else if (item instanceof ItemDeathwormArmor) {
            consumer.accept(armorModel((stack, inner) -> new ModelDeathWormArmor(ModelDeathWormArmor.getBakedModel(inner))));
        } else if (item instanceof ItemSeaSerpentArmor) {
            consumer.accept(armorModel((stack, inner) -> new ModelSeaSerpentArmor(inner)));
        } else if (item instanceof ItemScaleArmor scale) {
            consumer.accept(armorModel((stack, inner) -> {
                DragonType dragonType = scale.armor_type.eggType.dragonType;
                if (DragonType.FIRE == dragonType) {
                    return new ModelFireDragonScaleArmor(inner);
                }
                if (DragonType.ICE == dragonType) {
                    return new ModelIceDragonScaleArmor(inner);
                }
                if (DragonType.LIGHTNING == dragonType) {
                    return new ModelLightningDragonScaleArmor(inner);
                }
                return null;
            }));
        } else if (item instanceof ItemDragonsteelArmor) {
            CustomArmorMaterial material = ((IafArmorIdentity) item).iafMaterial();
            consumer.accept(armorModel((stack, inner) -> {
                if (material == IafItemRegistry.DRAGONSTEEL_FIRE_ARMOR_MATERIAL) {
                    return new ModelDragonsteelFireArmor(inner);
                }
                if (material == IafItemRegistry.DRAGONSTEEL_ICE_ARMOR_MATERIAL) {
                    return new ModelDragonsteelIceArmor(inner);
                }
                return new ModelDragonsteelLightningArmor(inner);
            }));
        }
    }

    /** {@code factory(stack, inner)} builds the model; {@code inner} is the 1.18 legs/head layer flag. */
    public static IClientItemExtensions armorModel(BiFunction<ItemStack, Boolean, HumanoidModel<?>> factory) {
        return new IClientItemExtensions() {
            private final HumanoidModel<?>[] bySlot = new HumanoidModel<?>[4];

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntityRenderState state, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                int index = slotIndex(slot);
                if (index < 0) {
                    return original;
                }
                if (bySlot[index] == null) {
                    boolean inner = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.HEAD;
                    HumanoidModel<?> model = factory.apply(stack, inner);
                    if (model == null) {
                        return original;
                    }
                    bySlot[index] = model;
                }
                HumanoidModel<?> model = bySlot[index];
                if (model instanceof ArmorModelBase armor) {
                    armor.setVisibleSlot(slot);
                } else {
                    setPartVisibility(model, slot);
                }
                return model;
            }

            @Override
            public @NotNull Model getGenericArmorModel(HumanoidRenderState state, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                // Do not copyFrom the stripped vanilla slot mesh. That overwrites pivots
                // on a full custom tree and is what made player armor sit crooked.
                HumanoidModel<?> model = getHumanoidArmorModel(state, stack, slot, original);
                if (model instanceof ArmorModelBase armor) {
                    armor.setVisibleSlot(slot);
                }
                return model;
            }

            private static int slotIndex(EquipmentSlot slot) {
                return switch (slot) {
                    case HEAD -> 0;
                    case CHEST -> 1;
                    case LEGS -> 2;
                    case FEET -> 3;
                    default -> -1;
                };
            }
        };
    }

    public static void setPartVisibility(HumanoidModel<?> model, EquipmentSlot slot) {
        model.head.visible = false;
        model.hat.visible = false;
        model.body.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> {
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
            }
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            default -> {
            }
        }
    }
}
