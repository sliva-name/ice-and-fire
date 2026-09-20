package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDeathWormGauntlet;
import com.github.alexthe666.iceandfire.entity.props.MiscProperties;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class RenderDeathWormGauntlet implements SpecialModelRenderer<RenderDeathWormGauntlet.State> {
    private static final Identifier RED = texture("red");
    private static final Identifier WHITE = texture("white");
    private static final Identifier YELLOW = texture("yellow");
    private final ModelDeathWormGauntlet geometry = new ModelDeathWormGauntlet();
    private final EntityModel<EntityRenderState> model = geometry.asEntityModel();
    private final ItemDisplayContext displayContext;

    public RenderDeathWormGauntlet(ItemDisplayContext displayContext) {
        this.displayContext = displayContext;
    }

    public record State(Identifier texture, float lungeTicks) { }

    private static Identifier texture(String color) {
        return Identifier.fromNamespaceAndPath("iceandfire", "textures/models/deathworm/deathworm_" + color + ".png");
    }

    @Override
    public State extractArgument(ItemStack stack) {
        Identifier texture = stack.is(IafItemRegistry.DEATHWORM_GAUNTLET_RED.get()) ? RED
            : stack.is(IafItemRegistry.DEATHWORM_GAUNTLET_WHITE.get()) ? WHITE : YELLOW;
        Minecraft client = Minecraft.getInstance();
        int holderId = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntOr("HolderID", -1);
        float lungeTicks = 0;
        if (client.level != null && holderId != -1 && client.level.getEntity(holderId) instanceof LivingEntity holder) {
            lungeTicks = MiscProperties.getLungeTicks(holder) + client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        }
        return new State(texture, lungeTicks);
    }

    private static void applyPose(ModelDeathWormGauntlet geometry, float lungeTicks) {
        geometry.resetToDefaultPose();
        geometry.progressRotation(geometry.TopJaw, lungeTicks, (float) Math.toRadians(-30), 0, 0, 20);
        geometry.progressRotation(geometry.BottomJaw, lungeTicks, (float) Math.toRadians(30), 0, 0, 20);
        geometry.progressPosition(geometry.JawExtender, lungeTicks, 0, 0, -4, 20);
        geometry.progressPosition(geometry.JawExtender2, lungeTicks, 0, 0, -10, 20);
        geometry.progressPosition(geometry.JawExtender3, lungeTicks, 0, 0, -10, 20);
        geometry.progressPosition(geometry.JawExtender4, lungeTicks, 0, 0, -10, 20);
    }

    @Override
    public void submit(@Nullable State state, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        if (state == null) {
            return;
        }
        poses.pushPose();
        // 1.18 ISTER origin; ItemTransform.apply already supplied the (-0.5, -0.5, -0.5) offset.
        poses.translate(0.5F, 0.5F, 0.5F);
        // Each queued item owns its posed native tree; another stack cannot overwrite its jaw pose.
        ModelDeathWormGauntlet posedGeometry = new ModelDeathWormGauntlet();
        applyPose(posedGeometry, state.lungeTicks());
        collector.submitModelPart(posedGeometry.asEntityModel().root(), poses, RenderTypes.entityCutoutCull(state.texture()),
            light, overlay, null, false, hasFoil, -1, null, outlineColor);
        poses.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poses = new PoseStack();
        poses.translate(0.5F, 0.5F, 0.5F);
        // Rest pose only: an open jaw inflates the AABB and shrinks the idle icon in the slot.
        model.root().getExtentsForGui(poses, output);
    }

    public record Unbaked(ItemDisplayContext displayContext) implements SpecialModelRenderer.Unbaked<State> {
        public static final MapCodec<Unbaked> MAP_CODEC = ItemDisplayContext.CODEC.optionalFieldOf("display_context", ItemDisplayContext.NONE)
            .xmap(Unbaked::new, Unbaked::displayContext);

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderDeathWormGauntlet bake(BakingContext context) { return new RenderDeathWormGauntlet(displayContext); }
    }
}
