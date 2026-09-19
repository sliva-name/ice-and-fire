package com.github.alexthe666.iceandfire.client.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

/** Extracted stone-statue inputs. Models are posed at submit time from these fields, not a live entity. */
public class StoneStatueRenderState extends EntityRenderState {
    public enum Kind {
        VANILLA, PLAYER, CITADEL, TROLL, HYDRA
    }

    public float statueScale = 1.0F;
    public float yRot;
    public int crackAmount;
    public Identifier crackTexture;
    public RenderType bodyRenderType;
    public Kind kind = Kind.VANILLA;
    public String trappedType = "minecraft:pig";
    public boolean trappedBaby;
    public boolean sitting;
    public float attackTime;
    public TrollRenderState.Variant trollVariant = TrollRenderState.Variant.FOREST;
    public HydraRenderState hydra;
    @SuppressWarnings("rawtypes")
    public EntityModel vanillaModel;
    public EntityRenderState vanillaState;
}
