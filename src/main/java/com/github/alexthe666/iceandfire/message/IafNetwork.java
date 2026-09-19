package com.github.alexthe666.iceandfire.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.simple.SimpleFlow;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 26.1 play-channel registration. Packet bodies stay the 1.18 write/read order.
 */
public final class IafNetwork {
    private IafNetwork() {
    }

    public static SimpleChannel create() {
        SimpleFlow<RegistryFriendlyByteBuf, Object> play = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath("iceandfire", "main_channel"))
            .networkProtocolVersion(1)
            .simpleChannel()
            .play()
            .bidirectional();
        add(play, MessageDaytime.class, MessageDaytime::write, MessageDaytime::read, MessageDaytime.Handler::handle);
        add(play, MessageDeathWormHitbox.class, MessageDeathWormHitbox::write, MessageDeathWormHitbox::read, MessageDeathWormHitbox.Handler::handle);
        add(play, MessageDragonControl.class, MessageDragonControl::write, MessageDragonControl::read, MessageDragonControl.Handler::handle);
        add(play, MessageDragonSetBurnBlock.class, MessageDragonSetBurnBlock::write, MessageDragonSetBurnBlock::read, MessageDragonSetBurnBlock.Handler::handle);
        add(play, MessageDragonSyncFire.class, MessageDragonSyncFire::write, MessageDragonSyncFire::read, MessageDragonSyncFire.Handler::handle);
        add(play, MessageGetMyrmexHive.class, MessageGetMyrmexHive::write, MessageGetMyrmexHive::read, MessageGetMyrmexHive.Handler::handle);
        add(play, MessageMyrmexSettings.class, MessageMyrmexSettings::write, MessageMyrmexSettings::read, MessageMyrmexSettings.Handler::handle);
        add(play, MessageHippogryphArmor.class, MessageHippogryphArmor::write, MessageHippogryphArmor::read, MessageHippogryphArmor.Handler::handle);
        add(play, MessageMultipartInteract.class, MessageMultipartInteract::write, MessageMultipartInteract::read, MessageMultipartInteract.Handler::handle);
        add(play, MessagePlayerHitMultipart.class, MessagePlayerHitMultipart::write, MessagePlayerHitMultipart::read, MessagePlayerHitMultipart.Handler::handle);
        add(play, MessageSetMyrmexHiveNull.class, MessageSetMyrmexHiveNull::write, MessageSetMyrmexHiveNull::read, MessageSetMyrmexHiveNull.Handler::handle);
        add(play, MessageSirenSong.class, MessageSirenSong::write, MessageSirenSong::read, MessageSirenSong.Handler::handle);
        add(play, MessageSpawnParticleAt.class, MessageSpawnParticleAt::write, MessageSpawnParticleAt::read, MessageSpawnParticleAt.Handler::handle);
        add(play, MessageStartRidingMob.class, MessageStartRidingMob::write, MessageStartRidingMob::read, MessageStartRidingMob.Handler::handle);
        add(play, MessageUpdatePixieHouse.class, MessageUpdatePixieHouse::write, MessageUpdatePixieHouse::read, MessageUpdatePixieHouse.Handler::handle);
        add(play, MessageUpdatePixieHouseModel.class, MessageUpdatePixieHouseModel::write, MessageUpdatePixieHouseModel::read, MessageUpdatePixieHouseModel.Handler::handle);
        add(play, MessageUpdatePixieJar.class, MessageUpdatePixieJar::write, MessageUpdatePixieJar::read, MessageUpdatePixieJar.Handler::handle);
        add(play, MessageUpdatePodium.class, MessageUpdatePodium::write, MessageUpdatePodium::read, MessageUpdatePodium.Handler::handle);
        add(play, MessageUpdateDragonforge.class, MessageUpdateDragonforge::write, MessageUpdateDragonforge::read, MessageUpdateDragonforge.Handler::handle);
        add(play, MessageUpdateLectern.class, MessageUpdateLectern::write, MessageUpdateLectern::read, MessageUpdateLectern.Handler::handle);
        add(play, MessageSyncPath.class, (message, buf) -> message.write(buf), MessageSyncPath::read, MessageSyncPath::handle);
        add(play, MessageSyncPathReached.class, (message, buf) -> message.write(buf), MessageSyncPathReached::read, MessageSyncPathReached::handle);
        add(play, MessageSwingArm.class, MessageSwingArm::write, MessageSwingArm::read, MessageSwingArm.Handler::handle);
        return play.build();
    }

    private static <M> void add(
        SimpleFlow<RegistryFriendlyByteBuf, Object> play,
        Class<M> type,
        BiConsumer<M, FriendlyByteBuf> encoder,
        Function<FriendlyByteBuf, M> decoder,
        BiConsumer<M, CustomPayloadEvent.Context> handler
    ) {
        play.addMain(type, codec(encoder, decoder), handler);
    }

    private static <M> StreamCodec<RegistryFriendlyByteBuf, M> codec(
        BiConsumer<M, FriendlyByteBuf> encoder,
        Function<FriendlyByteBuf, M> decoder
    ) {
        return StreamCodec.of((buf, message) -> encoder.accept(message, buf), decoder::apply);
    }
}
