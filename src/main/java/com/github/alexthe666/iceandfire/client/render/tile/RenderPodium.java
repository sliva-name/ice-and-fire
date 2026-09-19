package com.github.alexthe666.iceandfire.client.render.tile;


import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.EggRenderState;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderPodium<T extends TileEntityPodium> implements BlockEntityRenderer<T, PodiumRenderState> {
    private final EntityModel<EggRenderState> eggModel = new ModelDragonEgg().asEntityModel();
    private final ItemModelResolver itemModelResolver;

    public RenderPodium(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    protected static RenderType getEggTexture(EnumDragonEgg type) {
        return RenderTypes.entityCutout(RenderDragonEgg.getEggTexture(type));
    }

    @Override
    public PodiumRenderState createRenderState() {
        return new PodiumRenderState();
    }

    @Override
    public void extractRenderState(T podium, PodiumRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(podium, state, partialTicks, cameraPosition, breakProgress);
        // A reused state may change between egg, ordinary item and empty slot.
        state.eggRenderType = null;
        state.item.clear();
        state.egg.inverted = true;
        state.egg.wobbleAmount = 0;
        state.egg.ageInTicks = 0;
        state.itemBob = 0;
        state.itemRotation = 0;
        ItemStack stack = podium.getItem(0).copy();
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof ItemDragonEgg egg) {
            state.eggRenderType = getEggTexture(egg.type);
        } else if (stack.getItem() instanceof ItemMyrmexEgg) {
            boolean jungle = stack.getItem() == IafItemRegistry.MYRMEX_JUNGLE_EGG.get();
            state.eggRenderType = RenderTypes.entityCutout(jungle ? RenderMyrmexEgg.EGG_JUNGLE : RenderMyrmexEgg.EGG_DESERT);
        } else {

            float age = Mth.lerp(partialTicks, (float) podium.prevTicksExisted, (float) podium.ticksExisted);
            state.itemBob = Mth.sin(age / 10.0F) * 0.1F + 0.1F;
            state.itemRotation = age / 20.0F;
            itemModelResolver.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED, podium.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(PodiumRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.eggRenderType != null) {
            poses.pushPose();
            poses.translate(0.5F, 0.475F, 0.5F);
            collector.submitModel(eggModel, state.egg, poses, state.eggRenderType, state.lightCoords,
                OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
            poses.popPose();
        } else if (!state.item.isEmpty()) {
            poses.pushPose();
            poses.translate(0.5F, 1.55F + state.itemBob, 0.5F);
            poses.mulPose(Axis.YP.rotation(state.itemRotation));
            poses.translate(0, 0.2F, 0);
            poses.scale(0.65F, 0.65F, 0.65F);
            state.item.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poses.popPose();
        }
    }
}
