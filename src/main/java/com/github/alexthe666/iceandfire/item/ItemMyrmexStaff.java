package com.github.alexthe666.iceandfire.item;

import net.minecraft.core.UUIDUtil;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.message.MessageGetMyrmexHive;
import com.github.alexthe666.iceandfire.message.MessageSetMyrmexHiveNull;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemMyrmexStaff extends Item {

    public ItemMyrmexStaff(boolean jungle) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        IafItemData.write(itemStack, new CompoundTag());
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel world, @NotNull Entity entity, @NotNull net.minecraft.world.entity.EquipmentSlot slot) {
        if (!IafItemData.has(stack) || IafItemData.copy(stack).read("HiveUUID", UUIDUtil.CODEC).isEmpty()) {
            IafItemData.update(stack, tag -> tag.store("HiveUUID", UUIDUtil.CODEC, new UUID(0, 0)));
        }
    }

    /** True when {@code player} holds a staff bound to the hive {@code hiveId}; hive edits from clients require this. */
    public static boolean holdsStaffFor(Player player, UUID hiveId) {
        if (hiveId == null) {
            return false;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem() instanceof ItemMyrmexStaff && IafItemData.has(held)
                && IafItemData.copy(held).read("HiveUUID", UUIDUtil.CODEC).filter(hiveId::equals).isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        if (playerIn.isShiftKeyDown()) {
            return super.use(worldIn, playerIn, hand);
        }
        if (IafItemData.has(itemStackIn) && IafItemData.copy(itemStackIn).read("HiveUUID", UUIDUtil.CODEC).isPresent()) {
            UUID id = IafItemData.copy(itemStackIn).read("HiveUUID", UUIDUtil.CODEC).orElseThrow();
            if (!worldIn.isClientSide()) {
                MyrmexHive hive = MyrmexWorldData.get(worldIn).getHiveFromUUID(id);
                if (hive != null) {
                    IceAndFire.sendMSGToPlayer(new MessageGetMyrmexHive(hive.toNBT()), (net.minecraft.server.level.ServerPlayer) playerIn);
                } else {
                    IceAndFire.sendMSGToPlayer(new MessageSetMyrmexHiveNull(), (net.minecraft.server.level.ServerPlayer) playerIn);
                }
            } else if (id != null && !id.equals(new UUID(0, 0))) {
                IceAndFire.PROXY.openMyrmexStaffGui(itemStackIn);
            }
        }
        playerIn.swing(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getPlayer().isShiftKeyDown()) {
            return super.useOn(context);
        } else {
            CompoundTag tag = IafItemData.has(context.getPlayer().getItemInHand(context.getHand()))
                ? IafItemData.copy(context.getPlayer().getItemInHand(context.getHand()))
                : null;
            if (tag != null && tag.read("HiveUUID", UUIDUtil.CODEC).isPresent()) {
                UUID id = tag.read("HiveUUID", UUIDUtil.CODEC).orElseThrow();
                if (!context.getLevel().isClientSide()) {
                    MyrmexHive hive = MyrmexWorldData.get(context.getLevel()).getHiveFromUUID(id);
                    if (hive != null) {
                        IceAndFire.sendMSGToPlayer(new MessageGetMyrmexHive(hive.toNBT()), (net.minecraft.server.level.ServerPlayer) context.getPlayer());
                    } else {
                        IceAndFire.sendMSGToPlayer(new MessageSetMyrmexHiveNull(), (net.minecraft.server.level.ServerPlayer) context.getPlayer());
                    }
                } else if (id != null && !id.equals(new UUID(0, 0))) {
                    IceAndFire.PROXY.openMyrmexAddRoomGui(context.getPlayer().getItemInHand(context.getHand()), context.getClickedPos(), context.getPlayer().getDirection());
                }
            }
            context.getPlayer().swing(context.getHand());
            return InteractionResult.SUCCESS;
        }
    }
}
