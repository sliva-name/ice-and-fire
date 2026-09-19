/*
 * Ice and Fire's Forge 26.1 Citadel animation transport (LGPL).
 * Preserves the animation protocol of Citadel
 * 8018e44d8b569913ca828f31aa6c86163319e7a5 by Alexthe666.
 * LLibrary credits: iLexiconn and Gegy1000.
 */
package com.github.alexthe666.citadel.network;

import com.github.alexthe666.citadel.client.animation.ClientAnimationHandler;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class AnimationNetwork {
    private static SimpleChannel channel;

    private AnimationNetwork() {
    }

    /** Called by the standalone Citadel mod constructor on both physical sides. */
    public static synchronized void register() {
        if (channel != null) {
            return;
        }
        channel = ChannelBuilder.named("citadel:animation")
                .networkProtocolVersion(1)
                .simpleChannel()
                .play().clientbound()
                .addMain(AnimationMessage.class, AnimationMessage.CODEC, AnimationNetwork::handle)
                .build();
    }

    private static void handle(AnimationMessage message, CustomPayloadEvent.Context context) {
        // addMain enqueues onto the logical client's main thread. This physical
        // side guard keeps the client-only class unloaded on dedicated servers.
        if (context.isClientSide() && FMLEnvironment.dist == Dist.CLIENT) {
            ClientAnimationHandler.handle(message);
        }
    }

    public static void send(Entity entity, AnimationMessage message) {
        if (entity.level().isClientSide()) {
            return;
        }
        if (channel == null) {
            throw new IllegalStateException("Citadel animation network has not been registered by its mod bootstrap");
        }
        // Deliberate improvement over upstream's global broadcast: only players
        // tracking this entity (and the entity itself, if a player) can apply it.
        channel.send(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }
}
