package com.github.alexthe666.iceandfire.world.gen.mixin;

import com.github.alexthe666.iceandfire.client.render.entity.DragonRiderRenderHooks;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class DragonRiderStateMixin implements DragonRiderRenderHooks.StateAccess {
    @Unique
    private boolean iceandfire$standaloneDragonRider;

    @Override
    public boolean iceandfire$isStandaloneDragonRider() {
        return iceandfire$standaloneDragonRider;
    }

    @Override
    public void iceandfire$setStandaloneDragonRider(boolean value) {
        iceandfire$standaloneDragonRider = value;
    }
}
