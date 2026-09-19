package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDragonSkull extends Item {
    private final int dragonType;

    public ItemDragonSkull(int dragonType) {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
        this.dragonType = dragonType;
    }

    static String getName(int type) {
        return "dragon_skull_%s".formatted(getType(type));
    }

    private static String getType(int type) {
        if (type == 2) {
            return "lightning";
        } else if (type == 1) {
            return "ice";
        } else {
            return "fire";
        }
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        IafItemData.write(itemStack, new CompoundTag());
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel worldIn, @NotNull Entity entityIn, @NotNull net.minecraft.world.entity.EquipmentSlot slot) {
        if (!IafItemData.has(stack)) {
            IafItemData.update(stack, tag -> {
                tag.putInt("Stage", 4);
                tag.putInt("DragonAge", 75);
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        String iceorfire = "dragon." + getType(dragonType);
        tooltip.accept(Component.translatable(iceorfire).withStyle(ChatFormatting.GRAY));
        if (IafItemData.has(stack)) {
            tooltip.accept(Component.translatable("dragon.stage").withStyle(ChatFormatting.GRAY).append(Component.literal(" " + IafItemData.copy(stack).getIntOr("Stage", 0))));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        /*
         * EntityDragonEgg egg = new EntityDragonEgg(worldIn);
         * egg.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() +
         * 0.5); if(!worldIn.isRemote){ worldIn.spawnEntityInWorld(egg); }
         */
        if (IafItemData.has(stack)) {
            CompoundTag tag = IafItemData.copy(stack);
            EntityDragonSkull skull = new EntityDragonSkull(IafEntityRegistry.DRAGON_SKULL.get(), context.getLevel());
            skull.setDragonType(dragonType);
            skull.setStage(tag.getIntOr("Stage", 0));
            skull.setDragonAge(tag.getIntOr("DragonAge", 0));
            BlockPos offset = context.getClickedPos().relative(context.getClickedFace(), 1);
            skull.snapTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0.0F, 0.0F);
            float yaw = context.getPlayer().getYRot();
            if (context.getClickedFace() != Direction.UP) {
                yaw = context.getPlayer().getDirection().toYRot();
            }
            skull.setYRot(yaw);
            if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)) {
                skull.setCustomName(stack.getHoverName());
            }
            if (!context.getLevel().isClientSide()) {
                context.getLevel().addFreshEntity(skull);
            }
            if (!context.getPlayer().isCreative()) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;

    }
}
