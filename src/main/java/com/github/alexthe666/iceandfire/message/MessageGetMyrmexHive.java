package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.item.ItemMyrmexStaff;
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
            context.setPacketHandled(true);
            if (message.hive == null) {
                return;
            }
            MyrmexHive receivedHive = MyrmexHive.fromNBT(message.hive);
            if (context.isClientSide()) {
                CompoundTag tag = new CompoundTag();
                receivedHive.writeVillageDataToNBT(tag);
                receivedHive.readVillageDataFromNBT(tag);
                IceAndFire.PROXY.setReferencedHive(receivedHive);
                return;
            }
            // Client -> server: the staff GUIs edit their local copy and send it back. Only accept it for an existing
            // hive whose staff the sender is actually holding.
            Player player = context.getSender();
            if (player == null || !ItemMyrmexStaff.holdsStaffFor(player, receivedHive.hiveUUID)) {
                return;
            }
            MyrmexHive realHive = MyrmexWorldData.get(player.level()).getHiveFromUUID(receivedHive.hiveUUID);
            if (realHive != null) {
                realHive.readVillageDataFromNBT(receivedHive.toNBT());
            }
        }
    }
}
