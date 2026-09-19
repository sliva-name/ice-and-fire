package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;

/** Extracted pose and item inputs; never retains a pixie, tile, or live ItemStack. */
public class PixieRenderState extends LivingEntityRenderState {
    public enum Mode { ENTITY, JAR, HOUSE }

    private static final Identifier[] TEXTURES = new Identifier[6];

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath("iceandfire", "textures/models/pixie/pixie_" + i + ".png");
        }
    }

    public Mode mode = Mode.ENTITY;
    public int color;
    public boolean sitting;
    public boolean orderedToSit;
    // The holding pose follows stack presence even if its resolved model has no visible layers.
    public boolean holdingItem;
    public final ItemStackRenderState heldItem = new ItemStackRenderState();

    public static Identifier textureFor(int color) {
        return TEXTURES[color >= 0 && color < TEXTURES.length ? color : 0];
    }

    /** Fully replaces the model inputs when a block renderer reuses its contained-pixie state. */
    public void setContainedPose(Mode mode, int color, float age, boolean sitting) {
        this.mode = mode;
        this.color = color;
        this.ageInTicks = age;
        this.sitting = sitting;
        this.orderedToSit = false;
        this.holdingItem = false;
        this.heldItem.clear();
        this.walkAnimationPos = 0;
        this.walkAnimationSpeed = 0;
        this.yRot = 0;
        this.xRot = 0;
    }
}
