package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.render.entity.EggRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jspecify.annotations.Nullable;

public class PodiumRenderState extends BlockEntityRenderState {
    public final EggRenderState egg = new EggRenderState();
    public @Nullable RenderType eggRenderType;
    public final ItemStackRenderState item = new ItemStackRenderState();
    public float itemBob;
    public float itemRotation;
}
