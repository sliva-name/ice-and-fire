package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.client.model.ModelPixieHouse;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPixieHouse;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.EndPortalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class IceAndFireTEISR implements SpecialModelRenderer<IceAndFireTEISR.State> {
    private static final Identifier CHEST_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/ghost/ghost_chest.png");
    private final ChestModel chest;
    private final EntityModel<EntityRenderState> house = new ModelPixieHouse().asEntityModel();
    private final RenderDreadPortal<TileEntityDreadPortal> portal = new RenderDreadPortal<>(null);

    public IceAndFireTEISR(BakingContext context) {
        chest = new ChestModel(context.entityModelSet().bakeLayer(ChestRenderer.LAYERS.select(ChestType.SINGLE)));
    }

    public enum Kind { CHEST, PORTAL, HOUSE }

    public record State(Kind kind, PixieHouseRenderState house, EndPortalRenderState portal) { }

    @Override
    public @Nullable State extractArgument(ItemStack stack) {
        if (stack.is(IafBlockRegistry.GHOST_CHEST.get().asItem())) {
            return new State(Kind.CHEST, new PixieHouseRenderState(), new EndPortalRenderState());
        }
        if (stack.getItem() instanceof BlockItem item) {
            if (item.getBlock() == IafBlockRegistry.DREAD_PORTAL.get()) {
                EndPortalRenderState state = new EndPortalRenderState();
                // The level-less dread portal shows all six faces, just like its tile renderer.
                for (Direction direction : Direction.values()) {
                    state.facesToShow.add(direction);
                }
                return new State(Kind.PORTAL, new PixieHouseRenderState(), state);
            }
            if (item.getBlock() instanceof BlockPixieHouse) {
                PixieHouseRenderState state = new PixieHouseRenderState();
                state.visible = true;
                state.houseType = TileEntityPixieHouse.getHouseTypeFromBlock(item.getBlock());
                // Item houses have no occupant and no world-facing rotation.
                return new State(Kind.HOUSE, state, new EndPortalRenderState());
            }
        }
        return null;
    }

    @Override
    public void submit(@Nullable State state, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        if (state == null) {
            return;
        }
        poses.pushPose();
        // Restore the origin used by the former item-renderer/BEWLR pipeline.
        poses.translate(-0.5F, -0.5F, -0.5F);
        switch (state.kind()) {
            case CHEST -> collector.submitModel(chest, 0.0F, poses, RenderTypes.entityCutoutCull(CHEST_TEXTURE),
                light, overlay, outlineColor, null);
            case PORTAL -> portal.submit(state.portal(), poses, collector, new CameraRenderState());
            case HOUSE -> {
                poses.translate(0.5F, 1.501F, 0.5F);
                poses.mulPose(Axis.XP.rotationDegrees(180));
                // Unlike the world renderer, items must keep the caller's overlay coordinates.
                collector.submitModel(house, state.house().house, poses,
                    RenderTypes.entityCutout(PixieHouseRenderState.textureFor(state.house().houseType), false),
                    light, overlay, outlineColor, null);
            }
        }
        poses.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poses = new PoseStack();
        poses.translate(-0.5F, -0.5F, -0.5F);
        chest.setupAnim(0.0F);
        chest.root().getExtentsForGui(poses, output);
        // Include the portal cube; extents cover every argument handled by this renderer.
        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = 0; z <= 1; z++) {
                    output.accept(new Vector3f(x - 0.5F, y - 0.5F, z - 0.5F));
                }
            }
        }
        poses.translate(0.5F, 1.501F, 0.5F);
        poses.mulPose(Axis.XP.rotationDegrees(180));
        house.root().getExtentsForGui(poses, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<State> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public IceAndFireTEISR bake(BakingContext context) { return new IceAndFireTEISR(context); }
    }
}
