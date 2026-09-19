package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;

public class RenderDragonEgg extends LivingEntityRenderer<EntityDragonEgg, EggRenderState, EntityModel<EggRenderState>> {

    public static final Identifier EGG_RED = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/firedragon/egg_red.png");
    public static final Identifier EGG_GREEN = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/firedragon/egg_green.png");
    public static final Identifier EGG_BRONZE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/firedragon/egg_bronze.png");
    public static final Identifier EGG_GREY = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/firedragon/egg_gray.png");
    public static final Identifier EGG_BLUE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/icedragon/egg_blue.png");
    public static final Identifier EGG_WHITE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/icedragon/egg_white.png");
    public static final Identifier EGG_SAPPHIRE = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/icedragon/egg_sapphire.png");
    public static final Identifier EGG_SILVER = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/icedragon/egg_silver.png");
    public static final Identifier EGG_ELECTRIC = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/lightningdragon/egg_electric.png");
    public static final Identifier EGG_AMYTHEST = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/lightningdragon/egg_amythest.png");
    public static final Identifier EGG_BLACK = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/lightningdragon/egg_black.png");
    public static final Identifier EGG_COPPER = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/lightningdragon/egg_copper.png");

    public RenderDragonEgg(EntityRendererProvider.Context context) {
        super(context, new ModelDragonEgg().asEntityModel(), 0.3F);
    }

    @Override
    public EggRenderState createRenderState() {
        return new EggRenderState();
    }

    @Override
    public void extractRenderState(EntityDragonEgg entity, EggRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        EnumDragonEgg type = entity.getEggType();
        state.texture = getEggTexture(type);
        state.inverted = false;
        boolean incubating = type.dragonType == DragonType.FIRE
            ? entity.level().getBlockState(entity.blockPosition()).is(BlockTags.FIRE)
            : type.dragonType == DragonType.LIGHTNING && entity.level().isRainingAt(entity.blockPosition());
        state.wobbleAmount = incubating ? 0.3F : 0;
    }

    @Override
    protected boolean shouldShowName(EntityDragonEgg entity, double distanceToCameraSq) {
        return entity.shouldShowName() && entity.hasCustomName();
    }

    @Override
    public Identifier getTextureLocation(EggRenderState state) {
        return state.texture;
    }

    public static Identifier getEggTexture(EnumDragonEgg type) {
        return switch (type) {
            case RED -> EGG_RED;
            case GREEN -> EGG_GREEN;
            case BRONZE -> EGG_BRONZE;
            case GRAY -> EGG_GREY;
            case BLUE -> EGG_BLUE;
            case WHITE -> EGG_WHITE;
            case SAPPHIRE -> EGG_SAPPHIRE;
            case SILVER -> EGG_SILVER;
            case ELECTRIC -> EGG_ELECTRIC;
            case AMYTHEST -> EGG_AMYTHEST;
            case COPPER -> EGG_COPPER;
            case BLACK -> EGG_BLACK;
        };
    }
}
