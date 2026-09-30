package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;


public class MessageHippogryphArmor {

    public int dragonId;
    public int slot_index;
    public int armor_type;

    public MessageHippogryphArmor(int dragonId, int slot_index, int armor_type) {
        this.dragonId = dragonId;
        this.slot_index = slot_index;
        this.armor_type = armor_type;
    }

    public MessageHippogryphArmor() {
    }

    public static MessageHippogryphArmor read(FriendlyByteBuf buf) {
        return new MessageHippogryphArmor(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void write(MessageHippogryphArmor message, FriendlyByteBuf buf) {
        buf.writeInt(message.dragonId);
        buf.writeInt(message.slot_index);
        buf.writeInt(message.armor_type);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageHippogryphArmor message, CustomPayloadEvent.Context context) {
            context.setPacketHandled(true);
            Player player = context.getSender();
            if (player == null) {
                return;
            }
            // The client's gear values are not trusted. The server owns the inventory, so it only re-derives the
            // saddle / chest / armor state from it, and only for the owner standing near the mount.
            Entity entity = player.level().getEntity(message.dragonId);
            if (entity == null || player.distanceToSqr(entity) > 64 * 64
                || !(entity instanceof TamableAnimal tamable) || !tamable.isOwnedBy(player)) {
                return;
            }
            if (entity instanceof EntityHippogryph hippo) {
                hippo.refreshInventory();
            } else if (entity instanceof EntityHippocampus hippo) {
                hippo.containerChanged(null);
            }
        }
    }
}
