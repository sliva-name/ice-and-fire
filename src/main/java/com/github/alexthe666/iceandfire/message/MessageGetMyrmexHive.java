package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class MessageGetMyrmexHive {

    public CompoundTag hive;

    public MessageGetMyrmexHive(CompoundTag hive) {
        this.hive = hive;
    }

    public MessageGetMyrmexHive() {
    }

    public static MessageGetMyrmexHive read(FriendlyByteBuf buf) {
        return new MessageGetMyrmexHive(buf.readNbt());
    }

    public static void write(MessageGetMyrmexHive message, FriendlyByteBuf buf) {
        buf.writeNbt(message.hive);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageGetMyrmexHive message, CustomPayloadEvent.Context context) {
            Player player = context.getSender();
            MyrmexHive serverHive = MyrmexHive.fromNBT(message.hive);
            CompoundTag tag = new CompoundTag();
            serverHive.writeVillageDataToNBT(tag);
            serverHive.readVillageDataFromNBT(tag);
            IceAndFire.PROXY.setReferencedHive(serverHive);
            context.setPacketHandled(true);
            if(context.isClientSide()){
                player = IceAndFire.PROXY.getClientSidePlayer();
            }else {
                if (MyrmexWorldData.get(player.level()) != null) {
                    MyrmexHive realHive = MyrmexWorldData.get(player.level()).getHiveFromUUID(serverHive.hiveUUID);
                    realHive.readVillageDataFromNBT(serverHive.toNBT());
                }
            }

        }
    }
}