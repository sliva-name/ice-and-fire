/*
 * Adapted from Citadel 8018e44d8b569913ca828f31aa6c86163319e7a5 (LGPL).
 * Citadel by Alexthe666; LLibrary credits: iLexiconn and Gegy1000.
 * Modified for the Ice and Fire Forge 26.1 subset.
 */
package com.github.alexthe666.citadel.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/** The payload remains two fixed-width integers: entity ID, then array index. */
public record AnimationMessage(int entityId, int index) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AnimationMessage> CODEC =
            StreamCodec.of(AnimationMessage::encode, AnimationMessage::read);

    private static void encode(RegistryFriendlyByteBuf buffer, AnimationMessage message) {
        write(message, buffer);
    }

    public static AnimationMessage read(FriendlyByteBuf buffer) {
        return new AnimationMessage(buffer.readInt(), buffer.readInt());
    }

    public static void write(AnimationMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId());
        buffer.writeInt(message.index());
    }
}
