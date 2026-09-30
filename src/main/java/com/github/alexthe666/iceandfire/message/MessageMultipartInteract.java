package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class MessageMultipartInteract {

    public int creatureID;
    public float dmg;

    public MessageMultipartInteract(int creatureID, float dmg) {
        this.creatureID = creatureID;
        this.dmg = dmg;
    }

    public MessageMultipartInteract() {
    }

    public static MessageMultipartInteract read(FriendlyByteBuf buf) {
        return new MessageMultipartInteract(buf.readInt(), buf.readFloat());
    }

    public static void write(MessageMultipartInteract message, FriendlyByteBuf buf) {
        buf.writeInt(message.creatureID);
        buf.writeFloat(message.dmg);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageMultipartInteract message, CustomPayloadEvent.Context context) {
            // Dead by design: EntityMutlipartPart#getParent is null on the client, so the client never sends this.
            // The server already handles the vanilla interact/attack packets aimed at the part entity itself, and
            // trusting a client supplied target id/damage here would allow unlimited-reach hits and damage spoofing.
            context.setPacketHandled(true);
        }
    }
}
