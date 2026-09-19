package com.github.alexthe666.iceandfire.item;

import net.minecraft.core.UUIDUtil;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.world.DragonPosWorldData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ItemSummoningCrystal extends Item {


    public ItemSummoningCrystal() {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
    }

    public static boolean hasDragon(ItemStack stack) {
        if (stack.getItem() instanceof ItemSummoningCrystal && IafItemData.has(stack)) {
            for (String tagInfo : IafItemData.copy(stack).keySet()) {
                if (tagInfo.contains("Dragon")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        IafItemData.write(itemStack, new CompoundTag());
    }

    public ItemStack onItemUseFinish(Level worldIn, LivingEntity LivingEntity) {
        return new ItemStack(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {

        boolean flag = false;
        String desc = "entity.iceandfire.fire_dragon";
        if (stack.getItem() == IafItemRegistry.SUMMONING_CRYSTAL_ICE.get()) {
            desc = "entity.iceandfire.ice_dragon";
        }
        if (stack.getItem() == IafItemRegistry.SUMMONING_CRYSTAL_LIGHTNING.get()) {
            desc = "entity.iceandfire.lightning_dragon";
        }
        if (IafItemData.has(stack)) {
            CompoundTag stored = IafItemData.copy(stack);
            for (String tagInfo : stored.keySet()) {
                if (tagInfo.contains("Dragon")) {
                    CompoundTag dragonTag = stored.getCompoundOrEmpty(tagInfo);
                    String dragonName = Component.translatable(desc).getString();
                    if (!dragonTag.getStringOr("CustomName", "").isEmpty()) {
                        dragonName = dragonTag.getStringOr("CustomName", "");
                    }
                    tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.bound", dragonName).withStyle(ChatFormatting.GRAY));
                    flag = true;
                }
            }
        }
        if (!flag) {
            tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.desc_0").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.desc_1").withStyle(ChatFormatting.GRAY));

        }

    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        boolean flag = false;
        BlockPos offsetPos = context.getClickedPos().relative(context.getClickedFace());
        float yaw = context.getPlayer().getYRot();
        boolean displayError = false;
        if (stack.getItem() == this && hasDragon(stack)) {
            if (IafItemData.has(stack)) {
                CompoundTag stored = IafItemData.copy(stack);
                for (String tagInfo : stored.keySet()) {
                    if (tagInfo.contains("Dragon")) {
                        CompoundTag dragonTag = stored.getCompoundOrEmpty(tagInfo);
                        UUID id = dragonTag.read("DragonUUID", UUIDUtil.CODEC).orElseThrow();
                        if (id != null) {
                            if (!context.getLevel().isClientSide()) {
                                try {
                                    Entity entity = context.getLevel().getServer().getLevel(context.getPlayer().level().dimension()).getEntity(id);
                                    if (entity != null) {
                                        flag = true;
                                        summonEntity(entity, context.getLevel(), offsetPos, yaw);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    displayError = true;
                                }
                                // ForgeChunkManager.Ticket ticket = null;
                                DragonPosWorldData data = DragonPosWorldData.get(context.getLevel());
                                BlockPos dragonChunkPos = null;
                                if (data != null) {
                                    dragonChunkPos = data.getDragonPos(id);
                                }
                                if (IafConfig.chunkLoadSummonCrystal) {
                                    try {
                                        boolean flag2 = false;
                                        if (!flag) {//server side but couldn't find dragon
                                            if (data != null) {
                                                // Original 1.18 guarded this with isClientSide inside an already-server block.
                                                if (context.getLevel().isClientSide() && context.getLevel() instanceof ServerLevel serverWorld && dragonChunkPos != null) {
                                                    serverWorld.setChunkForced(dragonChunkPos.getX() >> 4, dragonChunkPos.getZ() >> 4, true);
                                                }
                                                /*ticket = ForgeChunkManager.requestPlayerTicket(IceAndFire.INSTANCE, player.getName(), worldIn, ForgeChunkManager.Type.NORMAL);
                                                if (ticket != null) {
                                                    if (dragonChunkPos != null) {
                                                        ForgeChunkManager.forceChunk(ticket, new ChunkPos(dragonChunkPos));
                                                    } else {
                                                        displayError = true;
                                                    }
                                                    lastChunkTicket = ticket;
                                                    flag2 = true;
                                                }*/
                                            }
                                        }
                                        if (flag2) {
                                            try {
                                                Entity entity = context.getLevel().getServer().getLevel(context.getPlayer().level().dimension()).getEntity(id);
                                                if (entity != null) {
                                                    flag = true;
                                                    summonEntity(entity, context.getLevel(), offsetPos, yaw);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                       /* if (flag && lastChunkTicket != null && dragonChunkPos != null) {
                                            ForgeChunkManager.releaseTicket(lastChunkTicket);
                                            lastChunkTicket = null;
                                        }*/
                                    } catch (Exception e) {
                                        IceAndFire.LOGGER.warn("Could not load chunk when summoning dragon");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (flag) {
                context.getPlayer().playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
                context.getPlayer().playSound(SoundEvents.GLASS_BREAK, 1, 1);
                context.getPlayer().swing(context.getHand());
                context.getPlayer().sendOverlayMessage(Component.translatable("message.iceandfire.dragonTeleport"));
                IafItemData.write(stack, new CompoundTag());
            } else if (displayError) {
                context.getPlayer().sendOverlayMessage(Component.translatable("message.iceandfire.noDragonTeleport"));

            }
        }
        return InteractionResult.PASS;
    }

    public void summonEntity(Entity entity, Level worldIn, BlockPos offsetPos, float yaw) {
        entity.snapTo(offsetPos.getX() + 0.5D, offsetPos.getY() + 0.5D, offsetPos.getZ() + 0.5D, yaw, 0);
        if (entity instanceof EntityDragonBase) {
            ((EntityDragonBase) entity).setCrystalBound(false);
        }
        if (IafConfig.chunkLoadSummonCrystal) {
            DragonPosWorldData data = DragonPosWorldData.get(worldIn);
            if (data != null) {
                data.removeDragon(entity.getUUID());
            }
        }
    }


}
