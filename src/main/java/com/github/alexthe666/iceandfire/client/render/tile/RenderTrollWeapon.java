package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class RenderTrollWeapon implements SpecialModelRenderer<Identifier> {
    private final EntityModel<EntityRenderState> model = new ModelTrollWeapon().asEntityModel();

    @Override
    public Identifier extractArgument(ItemStack stack) {
        EnumTroll.Weapon weapon = stack.getItem() instanceof ItemTrollWeapon item ? item.weapon : EnumTroll.Weapon.AXE;
        return textureFor(weapon);
    }

    private static Identifier textureFor(EnumTroll.Weapon weapon) {
        return Identifier.fromNamespaceAndPath("iceandfire",
            "textures/models/troll/weapon/weapon_" + weapon.name().toLowerCase(Locale.ROOT) + ".png");
    }

    @Override
    public void submit(@Nullable Identifier texture, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean hasFoil, int outlineColor) {
        poses.pushPose();
        poses.translate(0, -1.25F, 0);
        collector.submitModel(model, new EntityRenderState(), poses,
            RenderTypes.entityCutoutCull(texture == null ? textureFor(EnumTroll.Weapon.AXE) : texture),
            light, overlay, outlineColor, null);
        poses.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        PoseStack poses = new PoseStack();
        poses.translate(0, -1.25F, 0);
        model.root().getExtentsForGui(poses, output);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Identifier> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<Unbaked> type() { return MAP_CODEC; }

        @Override
        public RenderTrollWeapon bake(BakingContext context) { return new RenderTrollWeapon(); }
    }
}
