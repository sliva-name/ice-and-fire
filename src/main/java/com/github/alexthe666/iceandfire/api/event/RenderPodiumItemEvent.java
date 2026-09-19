package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.client.render.tile.RenderPodium;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;

/**
 * Fired during extraction for ordinary podium items, not the custom egg models.
 * Listeners on {@link #BUS} return true to suppress the default item submission.
 * Coordinates are relative to the extraction camera. The live podium is available
 * only for this synchronous callback; do not retain it for deferred rendering.
 * The item getter returns a defensive snapshot, not the podium's inventory stack.
 */
public final class RenderPodiumItemEvent extends MutableEvent implements Cancellable {
    public static final CancellableEventBus<RenderPodiumItemEvent> BUS = CancellableEventBus.create(RenderPodiumItemEvent.class);

    private final float partialTicks;
    private final double x, y, z;
    private final ItemStack itemStack;
    private final RenderPodium<?> render;
    private final TileEntityPodium podium;

    public RenderPodiumItemEvent(RenderPodium<?> renderPodium, TileEntityPodium podium, float partialTicks, double x,
                                 double y, double z) {
        this.render = renderPodium;
        this.podium = podium;
        this.itemStack = podium.getItem(0).copy();
        this.partialTicks = partialTicks;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderPodium<?> getRender() {
        return render;
    }

    public ItemStack getItemStack() {
        return itemStack.copy();
    }

    public TileEntityPodium getPodium() {
        return podium;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
