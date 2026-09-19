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
        // Each queued item owns its posed native tree; another stack cannot overwrite its jaw pose.
        ModelDeathWormGauntlet posedGeometry = new ModelDeathWormGauntlet();
        applyPose(posedGeometry, state.lungeTicks());
        collector.submitModelPart(posedGeometry.asEntityModel().root(), poses, RenderTypes.entityCutoutCull(state.texture()),
            light, overlay, null, false, false, -1, null, outlineColor);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        model.root().getExtentsForGui(new PoseStack(), output);
        // Include the fully extended jaw in GUI bounds without retaining a live holder.
        applyPose(geometry, 20);
        geometry.asEntityModel().root().getExtentsForGui(new PoseStack(), output);
        geometry.resetToDefaultPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<State> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderDeathWormGauntlet bake(BakingContext context) { return new RenderDeathWormGauntlet(); }
    }
}
