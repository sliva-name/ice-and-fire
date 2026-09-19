package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityGhostChest;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 26.1 {@link ChestRenderState.ChestMaterialType} is a closed enum, so ghost chests
 * cannot ride the vanilla material table. Submit the vanilla chest models with the
 * 1.18 textures instead of {@link net.minecraft.client.renderer.Sheets#chooseSprite}.
 */
public class RenderGhostChest implements BlockEntityRenderer<TileEntityGhostChest, ChestRenderState> {
    private static final Identifier SINGLE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/ghost/ghost_chest.png");
    private static final Identifier LEFT = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/ghost/ghost_chest_left.png");
    private static final Identifier RIGHT = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/ghost/ghost_chest_right.png");

    private final MultiblockChestResources<ChestModel> models;

    public RenderGhostChest(BlockEntityRendererProvider.Context context) {
        this.models = ChestRenderer.LAYERS.map(layer -> new ChestModel(context.bakeLayer(layer)));
    }

    @Override
    public ChestRenderState createRenderState() {
        return new ChestRenderState();
    }

    @Override
    public void extractRenderState(TileEntityGhostChest blockEntity, ChestRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        boolean hasLevel = blockEntity.getLevel() != null;
        BlockState blockState = hasLevel
            ? blockEntity.getBlockState()
            : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
        state.type = blockState.hasProperty(ChestBlock.TYPE) ? blockState.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
        state.facing = blockState.getValue(ChestBlock.FACING);
        DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combineResult;
        if (hasLevel && blockState.getBlock() instanceof ChestBlock chestBlock) {
            combineResult = chestBlock.combine(blockState, blockEntity.getLevel(), blockEntity.getBlockPos(), true);
        } else {
            combineResult = DoubleBlockCombiner.Combiner::acceptNone;
        }
        state.open = combineResult.apply(ChestBlock.opennessCombiner(blockEntity)).get(partialTicks);
        if (state.type != ChestType.SINGLE) {
            state.lightCoords = combineResult.apply(new BrightnessCombiner<>()).applyAsInt(state.lightCoords);
        }
    }

    @Override
    public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                       CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(ChestRenderer.modelTransformation(state.facing));
        float open = 1.0F - state.open;
        open = 1.0F - open * open * open;
        submitNodeCollector.submitModel(this.models.select(state.type), open, poseStack,
            RenderTypes.entityCutoutCull(textureFor(state.type)),
            state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    private static Identifier textureFor(ChestType type) {
        return switch (type) {
            case LEFT -> LEFT;
            case RIGHT -> RIGHT;
            default -> SINGLE;
        };
    }
}
