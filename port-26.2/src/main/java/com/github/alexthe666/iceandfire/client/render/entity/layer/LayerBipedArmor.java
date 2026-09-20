package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.model.ModelBipedBase;
import com.github.alexthe666.iceandfire.client.render.entity.DreadHumanoidRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class LayerBipedArmor<M extends EntityModel<DreadHumanoidRenderState>> extends RenderLayer<DreadHumanoidRenderState, M> {
    private final SlotMesh legs;
    private final SlotMesh chest;
    private final SlotMesh feet;
    private final SlotMesh head;
    private final Identifier defaultLegArmor;
    private final Identifier defaultArmor;

    public LayerBipedArmor(RenderLayerParent<DreadHumanoidRenderState, M> renderer,
                           ModelBipedBase body, ModelBipedBase modelLeggings,
                           ModelBipedBase modelChest, ModelBipedBase modelFeet, ModelBipedBase modelHead,
                           Identifier defaultArmor, Identifier defaultLegArmor) {
        super(renderer);
        this.legs = new SlotMesh(modelLeggings, EquipmentSlot.LEGS);
        this.chest = new SlotMesh(modelChest, EquipmentSlot.CHEST);
        this.feet = new SlotMesh(modelFeet, EquipmentSlot.FEET);
        this.head = new SlotMesh(modelHead, EquipmentSlot.HEAD);
        this.defaultLegArmor = defaultLegArmor;
        this.defaultArmor = defaultArmor;
    }

    @Override
    public void submit(PoseStack poses, SubmitNodeCollector collector, int light,
                       DreadHumanoidRenderState state, float yRot, float xRot) {
        this.renderEquipment(poses, collector, state, EquipmentSlot.CHEST, light, this.chest);
        this.renderEquipment(poses, collector, state, EquipmentSlot.LEGS, light, this.legs);
        this.renderEquipment(poses, collector, state, EquipmentSlot.FEET, light, this.feet);
        this.renderEquipment(poses, collector, state, EquipmentSlot.HEAD, light, this.head);
    }

    private void renderEquipment(PoseStack poses, SubmitNodeCollector collector, DreadHumanoidRenderState state,
                                 EquipmentSlot slot, int light, SlotMesh mesh) {
        ItemStack itemstack = itemForSlot(state, slot);
        if (itemstack.isEmpty() || !HumanoidArmorLayer.shouldRender(itemstack, slot)) {
            return;
        }
        Identifier texture = this.getArmorResource(state, itemstack, slot, null);
        var type = RenderTypes.armorCutoutNoCull(texture);
        collector.order(1).submitModel(mesh.adapter, state, poses, type, light, OverlayTexture.NO_OVERLAY,
            state.outlineColor, null);
        if (itemstack.hasFoil()) {
            collector.order(2).submitModel(mesh.adapter, state, poses,
                RenderTypes.armorEntityGlint(),
                light, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }

    private static ItemStack itemForSlot(DreadHumanoidRenderState state, EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> state.headEquipment;
            case CHEST -> state.chestEquipment;
            case LEGS -> state.legsEquipment;
            case FEET -> state.feetEquipment;
            default -> ItemStack.EMPTY;
        };
    }

    protected void setModelSlotVisible(ModelBipedBase modelIn, EquipmentSlot slotIn) {
        modelIn.setVisible(false);
        switch (slotIn) {
            case HEAD:
                modelIn.head.invisible = false;
                modelIn.headware.invisible = false;
                break;
            case CHEST:
                modelIn.body.invisible = false;
                modelIn.armRight.invisible = false;
                modelIn.armLeft.invisible = false;
                break;
            case LEGS:
                modelIn.body.invisible = false;
                modelIn.legRight.invisible = false;
                modelIn.legLeft.invisible = false;
                break;
            case FEET:
                modelIn.legRight.invisible = false;
                modelIn.legLeft.invisible = false;
            default:
                break;
        }
    }

    public Identifier getArmorResource(DreadHumanoidRenderState state, ItemStack stack, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return defaultLegArmor;
        }
        return defaultArmor;
    }

    /**
     * One Citadel tree plus native adapter per slot. Deferred {@code submitModel}
     * re-reads the live invisible flags, so chest/feet/head cannot share a model.
     * Pose comes from {@code setupAnim(state)} at draw, not from the shared body.
     */
    private final class SlotMesh {
        private final EntityModel<DreadHumanoidRenderState> adapter;

        private SlotMesh(ModelBipedBase model, EquipmentSlot slot) {
            EntityModel<DreadHumanoidRenderState> baked = model.asEntityModel();
            this.adapter = new EntityModel<>(baked.root()) {
                @Override
                public void setupAnim(DreadHumanoidRenderState state) {
                    setModelSlotVisible(model, slot);
                    baked.setupAnim(state);
                }
            };
        }
    }
}
