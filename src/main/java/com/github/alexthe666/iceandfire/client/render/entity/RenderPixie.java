package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelPixie;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerPixieGlow;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerPixieItem;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderPixie extends MobRenderer<EntityPixie, PixieRenderState, EntityModel<PixieRenderState>> {
    public static final Identifier TEXTURE_0 = PixieRenderState.textureFor(0);
    public static final Identifier TEXTURE_1 = PixieRenderState.textureFor(1);
    public static final Identifier TEXTURE_2 = PixieRenderState.textureFor(2);
    public static final Identifier TEXTURE_3 = PixieRenderState.textureFor(3);
    public static final Identifier TEXTURE_4 = PixieRenderState.textureFor(4);
    public static final Identifier TEXTURE_5 = PixieRenderState.textureFor(5);

    private final ModelPixie pixieModel;

    public RenderPixie(EntityRendererProvider.Context context) {
        this(context, new ModelPixie());
    }

    private RenderPixie(EntityRendererProvider.Context context, ModelPixie model) {
        super(context, model.asEntityModel(), 0.2F);
        this.pixieModel = model;
        this.addLayer(new LayerPixieItem(this));
        this.addLayer(new LayerPixieGlow(this));
    }

    public ModelPixie getPixieModel() {
        return pixieModel;
    }

    @Override
    public PixieRenderState createRenderState() {
        return new PixieRenderState();
    }

    @Override
    public void extractRenderState(EntityPixie entity, PixieRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.mode = PixieRenderState.Mode.ENTITY;
        state.color = entity.getColor();
        state.sitting = entity.isPixieSitting();
        state.orderedToSit = entity.isOrderedToSit();
        ItemStack item = entity.getItemInHand(InteractionHand.MAIN_HAND);
        state.holdingItem = !item.isEmpty();
        // Retain the old FIXED item's ownerless model resolution and seed zero.
        this.itemModelResolver.updateForTopItem(state.heldItem, item, ItemDisplayContext.FIXED, entity.level(), null, 0);
    }

    @Override
    public void scale(PixieRenderState state, PoseStack stack) {
        stack.scale(0.55F, 0.55F, 0.55F);
        if (state.orderedToSit) {
            stack.translate(0F, 0.5F, 0F);
        }
    }

    @Override
    public Identifier getTextureLocation(PixieRenderState state) {
        return PixieRenderState.textureFor(state.color);
    }
}
