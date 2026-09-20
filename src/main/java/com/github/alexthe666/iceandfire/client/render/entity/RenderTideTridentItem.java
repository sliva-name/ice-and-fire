package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelTideTrident;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class RenderTideTridentItem implements SpecialModelRenderer<Void> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/tide_trident.png");
    private final EntityModel<EntityRenderState> model = new ModelTideTrident().asEntityModel();
    private final ItemDisplayContext displayContext;

    // The 26.1 special-renderer callbacks do not receive the display context.
    // Select a separately baked instance for each context in the client item definition.
    public RenderTideTridentItem(ItemDisplayContext displayContext) {
        this.displayContext = displayContext;
    }

    @Override
    public @Nullable Void extractArgument(ItemStack stack) {
        return null;
    }

    @Override
    public void submit(@Nullable Void argument, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        poses.pushPose();
        applyHandTransform(poses);
        collector.submitModelPart(model.root(), poses, RenderTypes.entityCutout(TEXTURE),
            light, overlay, null, false, hasFoil, -1, null, outlineColor);
        poses.popPose();
    }

    private void applyHandTransform(PoseStack poses) {
        // 26.x ItemTransform.apply already appends (-0.5, -0.5, -0.5), same as 1.18 ItemRenderer.
        // Keep the original ISTER origin on top of that.
        poses.translate(0.5F, 0.5F, 0.5F);
        poses.translate(0, 0.2F, -0.15F);
        if (displayContext.firstPerson()) {
            poses.translate(displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.2F, -0.2F);
        } else {
            poses.translate(0, 0.6F, 0);
        }
        poses.mulPose(Axis.XP.rotationDegrees(160));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poses = new PoseStack();
        applyHandTransform(poses);
        model.root().getExtentsForGui(poses, output);
    }

    public record Unbaked(ItemDisplayContext displayContext) implements SpecialModelRenderer.Unbaked<Void> {
        public static final MapCodec<Unbaked> MAP_CODEC = ItemDisplayContext.CODEC.fieldOf("display_context")
            .xmap(Unbaked::new, Unbaked::displayContext);

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderTideTridentItem bake(BakingContext context) { return new RenderTideTridentItem(displayContext); }
    }
}
