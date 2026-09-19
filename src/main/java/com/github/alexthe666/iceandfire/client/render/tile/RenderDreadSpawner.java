package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RenderDreadSpawner<T extends TileEntityDreadSpawner> implements BlockEntityRenderer<T, SpawnerRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public RenderDreadSpawner(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public SpawnerRenderState createRenderState() {
        return new SpawnerRenderState();
    }

    @Override
    public void extractRenderState(T spawnerTile, SpawnerRenderState state, float partialTicks, Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(spawnerTile, state, partialTicks, cameraPosition, breakProgress);
        state.displayEntity = null;
        state.spin = 0;
        state.scale = 0.53125F;
        if (spawnerTile.getLevel() == null) {
            return;
        }
        BaseSpawner spawner = spawnerTile.getSpawner();
        Entity displayEntity = spawner.getOrCreateDisplayEntity(spawnerTile.getLevel(), spawnerTile.getBlockPos());
        if (displayEntity != null) {
            state.displayEntity = entityRenderer.extractEntity(displayEntity, partialTicks);
            state.displayEntity.lightCoords = state.lightCoords;
            state.spin = (float) Mth.lerp((double) partialTicks, spawner.getOSpin(), spawner.getSpin()) * 10.0F;
            float maxLength = Math.max(displayEntity.getBbWidth(), displayEntity.getBbHeight());
            if (maxLength > 1.0F) {
                state.scale /= maxLength;
            }
        }
    }

    @Override
    public void submit(SpawnerRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.displayEntity != null) {
            SpawnerRenderer.submitEntityInSpawner(poses, collector, state.displayEntity, entityRenderer, state.spin, state.scale, camera);
        }
    }
}
