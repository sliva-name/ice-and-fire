/*
 * Citadel subset for Ice and Fire, modified for Minecraft 26.1 / Forge 62.0.9.
 * Replaces LivingEntityMixin / PropertiesMessage synchronization by Alexthe666 / AlexModGuy,
 * https://github.com/AlexModGuy/Citadel, ref 8018e44d8b569913ca828f31aa6c86163319e7a5.
 * Citadel credits LLibrary by iLexiconn and Gegy1000, used with permission.
 * See citadel/NOTICE.md for provenance and unresolved license/distribution obligations.
 */
package com.github.alexthe666.citadel.network;

import com.github.alexthe666.citadel.client.ClientPropertiesHandler;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class PropertiesNetwork {
    private static SimpleChannel channel;

    private PropertiesNetwork() {
    }

    /** Registered by the Citadel mod constructor, on both physical sides. */
    public static synchronized void register() {
        if (channel != null) {
            return;
        }
        channel = ChannelBuilder.named("citadel:properties").networkProtocolVersion(1)
                .simpleChannel().play().clientbound()
                .addMain(PropertiesMessage.class, PropertiesMessage.CODEC, PropertiesNetwork::handle).build();
        PlayerEvent.StartTracking.BUS.addListener(PropertiesNetwork::startTracking);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(event -> sendSelf(event.getEntity()));
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(event -> sendSelf(event.getEntity()));
        PlayerEvent.PlayerChangedDimensionEvent.BUS.addListener(event -> sendSelf(event.getEntity()));
        PlayerEvent.Clone.BUS.addListener(event -> {
            // Death resets transient chains/frozen/scepter state. End-return cloning preserves it.
            if (!event.isWasDeath()) {
                CitadelEntityData.storeTag(event.getEntity().getPersistentData(),
                        CitadelEntityData.getCitadelTag(event.getOriginal()));
            }
        });
    }

    private static void handle(PropertiesMessage message, CustomPayloadEvent.Context context) {
        if (context.isClientSide() && FMLEnvironment.dist == Dist.CLIENT) {
            ClientPropertiesHandler.handle(message);
        }
    }

    private static SimpleChannel channel() {
        if (channel == null) {
            throw new IllegalStateException("Citadel properties network has not been registered by its mod bootstrap");
        }
        return channel;
    }

    public static void send(LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            channel().send(PropertiesMessage.snapshot(entity), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
        }
    }

    private static void startTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof LivingEntity entity) {
            channel().send(PropertiesMessage.snapshot(entity), PacketDistributor.PLAYER.with(player));
        }
    }

    private static void sendSelf(Player entity) {
        if (entity instanceof ServerPlayer player) {
            channel().send(PropertiesMessage.snapshot(player), PacketDistributor.PLAYER.with(player));
        }
    }
}
