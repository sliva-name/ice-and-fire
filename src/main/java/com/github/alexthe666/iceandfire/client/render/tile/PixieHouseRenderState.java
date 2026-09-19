package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.render.entity.PixieRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class PixieHouseRenderState extends BlockEntityRenderState {
    private static final Identifier[] TEXTURES = new Identifier[6];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/pixie/house/pixie_house_" + i + ".png");
        }
    }

    public final EntityRenderState house = new EntityRenderState();
    public final PixieRenderState pixie = new PixieRenderState();
    public boolean visible;
    public boolean hasPixie;
    public int houseType;
    public float rotation;

    public static Identifier textureFor(int houseType) {
        return TEXTURES[houseType >= 0 && houseType < TEXTURES.length ? houseType : 0];
    }

    public static float rotationFor(Direction facing) {
        return switch (facing) {
            case NORTH -> 180;
            case EAST -> -90;
            case WEST -> 90;
            default -> 0;
        };
    }
}
