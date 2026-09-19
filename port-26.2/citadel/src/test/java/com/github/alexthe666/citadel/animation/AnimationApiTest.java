package com.github.alexthe666.citadel.animation;

import com.github.alexthe666.citadel.network.AnimationMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * Plain-main checks against the actual Citadel, Forge and Minecraft APIs, with
 * explicit assertions (no -ea or JUnit required). Run :citadel:animationApiTest.
 * Null event entities deliberately avoid bootstrapping a world: these are event
 * bus tests, not AnimationHandler advancement or client packet-application tests.
 */
public final class AnimationApiTest {
    private AnimationApiTest() {
    }

    public static void main(String[] args) {
        testAnimationTokens();
        // Repeat to expose leaked listeners on the static Forge buses.
        for (int pass = 0; pass < 2; pass++) {
            testStartEvents();
            testTickEvents();
        }
        testPacketCodec();
        System.out.println("PASS: animation identity/duration/legacy IDs/sentinel; Start replacement, "
                + "cancellation and listener removal; Tick delivery/order/removal (buses tested twice); "
                + "28 packet vectors through CODEC and read/write, fixed-int wire layout, "
                + "sentinel/invalid-index preservation, all 0..7-byte truncations and packet framing");
        System.out.println("LIMITS: no live Entity/Level or Minecraft client; handler advancement, "
                + "cancellation-versus-synchronization, client invalid-index rejection and tracking delivery "
                + "require an in-game integration test. The codec preserves indices; it does not validate them.");
    }

    @SuppressWarnings("deprecation")
    private static void testAnimationTokens() {
        Animation first = Animation.create(4);
        Animation second = Animation.create(4);
        check(first.getDuration() == 4 && second.getDuration() == 4, "Duration in ticks");
        check(first != second && !first.equals(second), "Equal durations must not merge animation identities");
        check(first.equals(first), "Animation identity is reflexive");
        check(new HashSet<>(List.of(first, second, first)).size() == 2, "Identity-based collection membership");
        check(first.getID() == 0, "Default legacy ID");

        Animation legacy = Animation.create(73, 9);
        Animation sameLegacyId = Animation.create(73, 9);
        check(legacy.getID() == 73 && legacy.getDuration() == 9, "Legacy factory ID/duration argument order");
        check(!legacy.equals(sameLegacyId), "Legacy IDs do not define animation equality");
        check(first.getDuration() == 4, "Creating another token must not change existing duration");
        check(Animation.create(Integer.MAX_VALUE).getDuration() == Integer.MAX_VALUE, "Large duration preserved");

        Animation sentinel = IAnimatedEntity.NO_ANIMATION;
        Animation zeroDuration = Animation.create(0);
        check(sentinel == IAnimatedEntity.NO_ANIMATION, "Shared sentinel identity");
        check(sentinel.getDuration() == 0, "Sentinel duration");
        check(zeroDuration.getDuration() == 0 && zeroDuration != sentinel && !zeroDuration.equals(sentinel),
                "A zero-duration animation is not the NO_ANIMATION sentinel");
    }

    @SuppressWarnings("rawtypes") // Forge exposes one erased bus for all animated entity types.
    private static void testStartEvents() {
        Animation original = Animation.create(4);
        Animation replacement = Animation.create(9);
        var start = new AnimationEvent.Start<>(null, original);
        check(start.getEntity() == null && start.getAnimation() == original, "Start constructor payload");
        check(!AnimationEvent.Start.BUS.post(start), "Start is not cancelled without listeners");

        int[] calls = {0};
        var listener = AnimationEvent.Start.BUS.addListener((Predicate<AnimationEvent.Start>) event -> {
            check(event.getAnimation() == original, "Start listener sees original token");
            check(event.getEntity() == null, "Start listener sees supplied entity");
            calls[0]++;
            event.setAnimation(replacement);
            return true;
        });
        try {
            check(AnimationEvent.Start.BUS.post(start), "True predicate cancels Start");
            check(calls[0] == 1, "Cancelling listener delivered exactly once");
            check(start.getAnimation() == replacement, "Cancelled event retains listener replacement");
            var fresh = new AnimationEvent.Start<>(null, original);
            check(fresh.getAnimation() == original, "Event replacement does not mutate another event");
        } finally {
            AnimationEvent.Start.BUS.removeListener(listener);
        }
        check(!AnimationEvent.Start.BUS.post(new AnimationEvent.Start<>(null, original)),
                "Removed listener no longer cancels Start");
        check(calls[0] == 1, "Removed Start listener is not invoked");

        var replacingListener = AnimationEvent.Start.BUS.addListener((Predicate<AnimationEvent.Start>) event -> {
            event.setAnimation(replacement);
            return false;
        });
        try {
            var uncancelled = new AnimationEvent.Start<>(null, original);
            check(!AnimationEvent.Start.BUS.post(uncancelled), "Replacement alone does not cancel Start");
            check(uncancelled.getAnimation() == replacement, "Uncancelled replacement is exposed to caller");
        } finally {
            AnimationEvent.Start.BUS.removeListener(replacingListener);
        }
        var afterRemoval = new AnimationEvent.Start<>(null, original);
        check(!AnimationEvent.Start.BUS.post(afterRemoval) && afterRemoval.getAnimation() == original,
                "Replacing listener removed");
    }

    private static void testTickEvents() {
        Animation animation = Animation.create(3);
        List<Integer> ticks = new ArrayList<>();
        var listener = AnimationEvent.Tick.BUS.addListener(event -> {
            check(event.getEntity() == null && event.getAnimation() == animation, "Tick listener payload");
            ticks.add(event.getTick());
        });
        try {
            for (int tick = 1; tick <= animation.getDuration(); tick++) {
                var event = new AnimationEvent.Tick<>(null, animation, tick);
                check(event.getTick() == tick, "Tick constructor preserves supplied tick");
                check(!AnimationEvent.Tick.BUS.post(event), "Tick event is not cancellable");
            }
            check(ticks.equals(List.of(1, 2, 3)), "Tick bus delivers each supplied tick in order");
        } finally {
            AnimationEvent.Tick.BUS.removeListener(listener);
        }
        check(!AnimationEvent.Tick.BUS.post(new AnimationEvent.Tick<>(null, animation, 4)), "Tick post after removal");
        check(ticks.equals(List.of(1, 2, 3)), "Removed Tick listener is not invoked");
    }

    private static void testPacketCodec() {
        int[] entityIds = {0, 0x01020304, Integer.MIN_VALUE, Integer.MAX_VALUE};
        // -1 is the sentinel; -2/MIN_VALUE and oversized positive indices are
        // intentionally transported unchanged. Bounds belong to the client handler.
        int[] indices = {-1, 0, 1, 5, -2, Integer.MIN_VALUE, Integer.MAX_VALUE};
        FriendlyByteBuf plain = new FriendlyByteBuf(Unpooled.buffer());
        RegistryFriendlyByteBuf registry = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        try {
            for (int entityId : entityIds) {
                for (int index : indices) {
                    AnimationMessage message = new AnimationMessage(entityId, index);
                    check(message.entityId() == entityId && message.index() == index, "Packet accessors");
                    plain.clear();
                    AnimationMessage.write(message, plain);
                    checkWire(plain, message);
                    check(AnimationMessage.read(plain).equals(message), "read/write round trip: " + message);
                    check(!plain.isReadable(), "read consumes exactly one packet");

                    registry.clear();
                    AnimationMessage.CODEC.encode(registry, message);
                    checkWire(registry, message);
                    check(AnimationMessage.CODEC.decode(registry).equals(message), "CODEC round trip: " + message);
                    check(!registry.isReadable(), "CODEC consumes exactly one packet");
                    registry.readerIndex(0);
                    check(AnimationMessage.read(registry).equals(message), "CODEC encode interoperates with read");
                    registry.clear();
                    AnimationMessage.write(message, registry);
                    check(AnimationMessage.CODEC.decode(registry).equals(message), "write interoperates with CODEC decode");
                }
            }
            for (int length = 0; length < 8; length++) {
                plain.clear();
                plain.writeZero(length);
                expectTruncated(() -> AnimationMessage.read(plain), "read length " + length);
                registry.clear();
                registry.writeZero(length);
                expectTruncated(() -> AnimationMessage.CODEC.decode(registry), "CODEC length " + length);
            }
            AnimationMessage first = new AnimationMessage(42, -1);
            AnimationMessage second = new AnimationMessage(43, Integer.MAX_VALUE);
            registry.clear();
            AnimationMessage.CODEC.encode(registry, first);
            AnimationMessage.CODEC.encode(registry, second);
            registry.writeByte(0x5A);
            check(AnimationMessage.CODEC.decode(registry).equals(first), "First framed packet");
            check(registry.readableBytes() == 9, "First decode leaves following packet untouched");
            check(AnimationMessage.CODEC.decode(registry).equals(second), "Second framed packet");
            check(registry.readableBytes() == 1 && registry.readUnsignedByte() == 0x5A, "Trailing byte preserved");
        } finally {
            plain.release();
            registry.release();
        }
    }

    private static void checkWire(FriendlyByteBuf buffer, AnimationMessage message) {
        check(buffer.readableBytes() == 8, "Packet is exactly two fixed-width ints: " + message);
        for (int byteIndex = 0; byteIndex < 4; byteIndex++) {
            int shift = 24 - byteIndex * 8;
            check(buffer.getUnsignedByte(byteIndex) == ((message.entityId() >>> shift) & 0xFF),
                    "Entity ID big-endian byte " + byteIndex);
            check(buffer.getUnsignedByte(4 + byteIndex) == ((message.index() >>> shift) & 0xFF),
                    "Animation index big-endian byte " + byteIndex);
        }
    }

    private static void expectTruncated(Runnable decode, String context) {
        try {
            decode.run();
        } catch (IndexOutOfBoundsException expected) {
            return;
        }
        throw new AssertionError("Truncated packet accepted: " + context);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
