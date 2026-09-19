package com.github.alexthe666.iceandfire.item;


import net.minecraft.core.UUIDUtil;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.util.IafEntityNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDragonHorn extends Item {

    public ItemDragonHorn() {
        super((IafItemRegistry.defaultBuilder()).stacksTo(1));
    }

    public static int getDragonType(ItemStack stack) {
        if (IafItemData.has(stack)) {
            String id = IafItemData.copy(stack).getStringOr("DragonHornEntityID", "");
            if (EntityType.byString(id).isPresent()) {
                EntityType entityType = EntityType.byString(id).get();
                if (entityType == IafEntityRegistry.FIRE_DRAGON.get())
                    return 1;

                if (entityType == IafEntityRegistry.ICE_DRAGON.get())
                    return 2;

                if (entityType == IafEntityRegistry.LIGHTNING_DRAGON.get())
                    return 3;
            }
        }

        return 0;
    }


    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        IafItemData.write(itemStack, new CompoundTag());
    }


    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player playerIn, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        ItemStack trueStack = playerIn.getItemInHand(hand);
        CompoundTag existing = IafItemData.has(trueStack) ? IafItemData.copy(trueStack) : null;
        if (!playerIn.level().isClientSide() && hand == InteractionHand.MAIN_HAND && target instanceof EntityDragonBase && ((EntityDragonBase) target).isOwnedBy(playerIn) && (existing == null || existing.getCompoundOrEmpty("EntityTag").isEmpty())) {
            CompoundTag newTag = new CompoundTag();

            newTag.put("EntityTag", IafEntityNbt.save(target));

            newTag.putString("DragonHornEntityID", BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString());
            IafItemData.write(trueStack, newTag);

            playerIn.swing(hand);
            playerIn.level().playSound(playerIn, playerIn.blockPosition(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 3.0F, 0.75F);
            target.remove(Entity.RemovalReason.DISCARDED);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP)
            return InteractionResult.FAIL;
        ItemStack stack = context.getItemInHand();
        if (IafItemData.has(stack) && !IafItemData.copy(stack).getStringOr("DragonHornEntityID", "").isEmpty()) {
            Level world = context.getLevel();
            CompoundTag stored = IafItemData.copy(stack);
            String id = stored.getStringOr("DragonHornEntityID", "");
            EntityType type = EntityType.byString(id).orElse(null);
            if (type != null) {
                Entity entity = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
                if (entity instanceof EntityDragonBase) {
                    IafEntityNbt.load(entity, stored.getCompoundOrEmpty("EntityTag"));
                }
                //Still needed to allow for intercompatibility
                if (stored.contains("EntityUUID"))
                    entity.setUUID(stored.read("EntityUUID", UUIDUtil.CODEC).orElseThrow());

                entity.snapTo(context.getClickedPos().getX() + 0.5D, (context.getClickedPos().getY() + 1), context.getClickedPos().getZ() + 0.5D, 180 + (context.getHorizontalDirection()).toYRot(), 0.0F);
                if (world.addFreshEntity(entity)) {
                    stored.remove("DragonHornEntityID");
                    stored.remove("EntityTag");
                    stored.remove("EntityUUID");
                    IafItemData.write(stack, stored);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (IafItemData.has(stack)) {
            CompoundTag stored = IafItemData.copy(stack);
            CompoundTag entityTag = stored.getCompoundOrEmpty("EntityTag");
            if (!entityTag.isEmpty()) {
                String id = stored.getStringOr("DragonHornEntityID", "");
                if (EntityType.byString(id).isPresent()) {
                    EntityType type = EntityType.byString(id).get();
                    tooltip.accept((Component.translatable(type.getDescriptionId())).withStyle(getTextColorForEntityType(type)));
                    String name = (Component.translatable("dragon.unnamed")).getString();
                    if (!entityTag.getStringOr("CustomName", "").isEmpty()) {
                        name = entityTag.getStringOr("CustomName", "");
                    }

                    tooltip.accept((Component.literal(name)).withStyle(ChatFormatting.GRAY));
                    String gender = (Component.translatable("dragon.gender")).getString() + " " + (Component.translatable(entityTag.getBooleanOr("Gender", false) ? "dragon.gender.male" : "dragon.gender.female")).getString();
                    tooltip.accept((Component.literal(gender)).withStyle(ChatFormatting.GRAY));
                    int stagenumber = entityTag.getIntOr("AgeTicks", 0) / 24000;
                    int stage1 = 0;
                    if (stagenumber >= 100) {
                        stage1 = 5;
                    } else if (stagenumber >= 75) {
                        stage1 = 4;
                    } else if (stagenumber >= 50) {
                        stage1 = 3;
                    } else if (stagenumber >= 25) {
                        stage1 = 2;
                    } else {
                        stage1 = 1;
                    }
                    String stage = (Component.translatable("dragon.stage")).getString() + " " + stage1 + " " + (Component.translatable("dragon.days.front")).getString() + stagenumber + " " + (Component.translatable("dragon.days.back")).getString();
                    tooltip.accept((Component.literal(stage)).withStyle(ChatFormatting.GRAY));
                }
            }

        }
    }

    private ChatFormatting getTextColorForEntityType(EntityType type) {
        if (type == IafEntityRegistry.FIRE_DRAGON.get())
            return ChatFormatting.DARK_RED;

        if (type == IafEntityRegistry.ICE_DRAGON.get())
            return ChatFormatting.BLUE;

        if (type == IafEntityRegistry.LIGHTNING_DRAGON.get())
            return ChatFormatting.DARK_PURPLE;

        return ChatFormatting.GRAY;
    }
}
