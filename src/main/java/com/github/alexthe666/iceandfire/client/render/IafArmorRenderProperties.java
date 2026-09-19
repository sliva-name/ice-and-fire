package com.github.alexthe666.iceandfire.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

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

    /** {@code factory(stack, inner)} builds the model; {@code inner} is the 1.18 legs/head layer flag. */
    public static IClientItemExtensions armorModel(BiFunction<ItemStack, Boolean, HumanoidModel<?>> factory) {
        return new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntityRenderState state, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                boolean inner = slot == EquipmentSlot.LEGS || slot == EquipmentSlot.HEAD;
                HumanoidModel<?> model = factory.apply(stack, inner);
                if (model == null) {
                    return original;
                }
                setPartVisibility(model, slot);
                return model;
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
