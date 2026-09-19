package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelGorgonHead;
import com.github.alexthe666.iceandfire.client.model.ModelGorgonHeadActive;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class RenderGorgonHead implements SpecialModelRenderer<Boolean> {
    private static final Identifier ACTIVE_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/head_active.png");
    private static final Identifier INACTIVE_TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/gorgon/head_inactive.png");
    private final EntityModel<EntityRenderState> activeModel = new ModelGorgonHeadActive().asEntityModel();
    private final EntityModel<EntityRenderState> inactiveModel = new ModelGorgonHead().asEntityModel();

    @Override
    public Boolean extractArgument(ItemStack stack) {
        return stack.is(IafItemRegistry.GORGON_HEAD.get())
            && stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBooleanOr("Active", false);
    }

    @Override
    public void submit(@Nullable Boolean argument, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        boolean active = Boolean.TRUE.equals(argument);
        poses.pushPose();
        // Special models no longer receive ItemRenderer's legacy (-0.5, -0.5, -0.5) offset.
        poses.translate(0, active ? 1.0F : 0.75F, 0);
        collector.submitModel(active ? activeModel : inactiveModel, new EntityRenderState(), poses,
            RenderTypes.entityCutout(active ? ACTIVE_TEXTURE : INACTIVE_TEXTURE, false),
            light, overlay, outlineColor, null);
        poses.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poses = new PoseStack();
        poses.translate(0, 1.0F, 0);
        activeModel.root().getExtentsForGui(poses, output);
        poses.translate(0, -0.25F, 0);
        inactiveModel.root().getExtentsForGui(poses, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Boolean> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderGorgonHead bake(BakingContext context) { return new RenderGorgonHead(); }
    }
}
