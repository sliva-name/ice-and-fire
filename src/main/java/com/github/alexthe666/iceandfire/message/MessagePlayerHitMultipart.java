package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class MessagePlayerHitMultipart {
    public int creatureID;
    public int extraData;

    public MessagePlayerHitMultipart(int creatureID) {
        this.creatureID = creatureID;
        this.extraData = 0;
    }

    public MessagePlayerHitMultipart(int creatureID, int extraData) {
        this.creatureID = creatureID;
        this.extraData = extraData;
    }

    public MessagePlayerHitMultipart() {
    }

    public static MessagePlayerHitMultipart read(FriendlyByteBuf buf) {
        return new MessagePlayerHitMultipart(buf.readInt(), buf.readInt());
    }

    public static void write(MessagePlayerHitMultipart message, FriendlyByteBuf buf) {
        buf.writeInt(message.creatureID);
        buf.writeInt(message.extraData);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessagePlayerHitMultipart message, CustomPayloadEvent.Context context) {
            context.setPacketHandled(true);
            Player player = context.getSender();
            if(context.isClientSide()){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }
            if (player != null) {
                if (player.level() != null) {
                    Entity entity = player.level().getEntity(message.creatureID);
                    if (entity != null && entity instanceof LivingEntity) {
                        double dist = player.distanceTo(entity);
                        LivingEntity mob = (LivingEntity) entity;
                        if (dist < 100) {
                            player.attack(mob);
                            if (mob instanceof EntityHydra) {
                                ((EntityHydra) mob).triggerHeadFlags(message.extraData);
                            }
                        }
                    }
                }
            }
        }
    }
}
