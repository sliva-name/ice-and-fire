package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelTideTrident;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class RenderTideTridentItem implements SpecialModelRenderer<ItemStackRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/misc/tide_trident.png");
    private final EntityModel<EntityRenderState> model = new ModelTideTrident().asEntityModel();
    private final ItemDisplayContext displayContext;

    // The 26.1 special-renderer callbacks do not receive the display context.
    // Select a separately baked instance for each context in the client item definition.
    public RenderTideTridentItem(ItemDisplayContext displayContext) {
        this.displayContext = displayContext;
    }

    private boolean inventoryModel() {
        return displayContext == ItemDisplayContext.GUI || displayContext == ItemDisplayContext.FIXED
            || displayContext == ItemDisplayContext.NONE || displayContext == ItemDisplayContext.GROUND;
    }

    @Override
    public @Nullable ItemStackRenderState extractArgument(ItemStack stack) {
        if (!inventoryModel()) {
            return null;
        }
        ItemStack inventory = new ItemStack(IafItemRegistry.TIDE_TRIDENT_INVENTORY.get());
        var enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null && !enchantments.isEmpty()) {
            inventory.set(DataComponents.ENCHANTMENTS, enchantments);
        }
        ItemStackRenderState state = new ItemStackRenderState();
        Minecraft client = Minecraft.getInstance();
        client.getItemModelResolver().updateForTopItem(state, inventory, displayContext, client.level, null, 0);
        return state;
    }

    @Override
    public void submit(@Nullable ItemStackRenderState inventory, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        if (inventoryModel()) {
            if (inventory != null) {
                inventory.submit(poses, collector, displayContext == ItemDisplayContext.GROUND ? light : 240, overlay, outlineColor);
            }
            return;
        }
        poses.pushPose();
        applyHandTransform(poses);
        collector.submitModelPart(model.root(), poses, RenderTypes.entityCutout(TEXTURE),
            light, overlay, null, false, hasFoil, -1, null, outlineColor);
        poses.popPose();
    }

    private void applyHandTransform(PoseStack poses) {
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
        if (inventoryModel()) {
            ItemStackRenderState inventory = extractArgument(new ItemStack(IafItemRegistry.TIDE_TRIDENT_INVENTORY.get()));
            if (inventory != null) {
                inventory.visitExtents(output);
            }
        } else {
            PoseStack poses = new PoseStack();
            applyHandTransform(poses);
            model.root().getExtentsForGui(poses, output);
        }
    }

    public record Unbaked(ItemDisplayContext displayContext) implements SpecialModelRenderer.Unbaked<ItemStackRenderState> {
        public static final MapCodec<Unbaked> MAP_CODEC = ItemDisplayContext.CODEC.fieldOf("display_context")
            .xmap(Unbaked::new, Unbaked::displayContext);

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderTideTridentItem bake(BakingContext context) { return new RenderTideTridentItem(displayContext); }
    }
}
