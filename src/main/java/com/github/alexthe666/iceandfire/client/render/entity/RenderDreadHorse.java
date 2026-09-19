package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerGenericGlowing;
import com.github.alexthe666.iceandfire.entity.EntityDreadHorse;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class RenderDreadHorse extends MobRenderer<EntityDreadHorse, EquineRenderState, HorseModel> {
    public static final Identifier TEXTURE = Identifier.parse("iceandfire:textures/models/dread/dread_knight_horse.png");
    public static final Identifier TEXTURE_EYES = Identifier.parse("iceandfire:textures/models/dread/dread_knight_horse_eyes.png");

    public RenderDreadHorse(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), 0.75F);
        this.addLayer(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public EquineRenderState createRenderState() {
        return new EquineRenderState();
    }

    @Override
    public void extractRenderState(EntityDreadHorse entity, EquineRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.saddle = entity.getItemBySlot(EquipmentSlot.SADDLE).copy();
        state.bodyArmorItem = entity.getBodyArmorItem().copy();
        state.isRidden = entity.isVehicle();
        state.eatAnimation = entity.getEatAnim(partialTick);
        state.standAnimation = entity.getStandAnim(partialTick);
        state.feedingAnimation = entity.getMouthAnim(partialTick);
        state.animateTail = entity.tailCounter > 0;
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull EquineRenderState state) {
        return TEXTURE;
    }
}
