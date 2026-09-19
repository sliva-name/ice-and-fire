package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.render.tile.RenderDeathWormGauntlet;
import com.github.alexthe666.iceandfire.entity.props.MiscProperties;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDeathwormGauntlet extends Item {

    private boolean deathwormReceded = true;
    private boolean deathwormLaunched = false;
    private int specialDamage = 0;

    public ItemDeathwormGauntlet() {
        super(IafItemRegistry.defaultBuilder().durability(500));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 1;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        playerIn.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onUseTick(Level world, LivingEntity player, ItemStack stack, int count) {
        if (IafItemData.has(stack)) {
            if (deathwormReceded || deathwormLaunched) {
                return;
            } else {
                if (player instanceof Player) {
                    if (IafItemData.copy(stack).getIntOr("HolderID", 0) != player.getId()) {
                        IafItemData.update(stack, tag -> tag.putInt("HolderID", player.getId()));
                    }
                    if (((Player) player).getCooldowns().getCooldownPercent(stack, 0.0F) == 0) {
                        ((Player) player).getCooldowns().addCooldown(stack, 10);
                        player.playSound(IafSoundRegistry.DEATHWORM_ATTACK, 1F, 1F);
                        deathwormReceded = false;
                        deathwormLaunched = true;
                    }
                }
            }
        }
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity LivingEntity, int timeLeft) {
        if (specialDamage > 0) {
            stack.hurtAndBreak(specialDamage, LivingEntity, LivingEntity.getUsedItemHand());
            specialDamage = 0;
        }
        if (IafItemData.copy(stack).getIntOr("HolderID", 0) != -1) {
            IafItemData.update(stack, tag -> tag.putInt("HolderID", -1));
        }
        return true;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel world, @NotNull Entity entity, @NotNull net.minecraft.world.entity.EquipmentSlot slot) {
        boolean hitMob = false;
        if (!IafItemData.has(stack)) {
            IafItemData.write(stack, new CompoundTag());
        } else {
            if (!(entity instanceof LivingEntity))
                return;
            int tempLungeTicks = MiscProperties.getLungeTicks((LivingEntity) entity);
            if (deathwormReceded) {
                if (tempLungeTicks > 0) {
                    tempLungeTicks = tempLungeTicks - 4;
                }
                if (tempLungeTicks <= 0) {
                    tempLungeTicks = 0;
                    deathwormReceded = false;
                    deathwormLaunched = false;
                }
            } else if (deathwormLaunched) {
                tempLungeTicks = 4 + tempLungeTicks;
                if (tempLungeTicks > 20 && !deathwormReceded) {
                    deathwormReceded = true;
                }
            }

            if (MiscProperties.getLungeTicks((LivingEntity) entity) == 20) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    Vec3 Vector3d = player.getViewVector(1.0F).normalize();
                    double range = 5;
                    for (LivingEntity livingEntity : world.getEntitiesOfClass(LivingEntity.class, new AABB(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range, player.getY() + range, player.getZ() + range))) {
                        //Let's not pull/hit ourselves
                        if (livingEntity == entity)
                            continue;
                        Vec3 Vector3d1 = new Vec3(livingEntity.getX() - player.getX(), livingEntity.getY() - player.getY(), livingEntity.getZ() - player.getZ());
                        double d0 = Vector3d1.length();
                        Vector3d1 = Vector3d1.normalize();
                        double d1 = Vector3d.dot(Vector3d1);
                        boolean canSee = d1 > 1.0D - 0.5D / d0 && player.hasLineOfSight(livingEntity);
                        if (canSee) {
                            specialDamage++;
                            livingEntity.hurt(entity.damageSources().playerAttack((Player) entity), 3F);
                            livingEntity.knockback(0.5F, livingEntity.getX() - player.getX(), livingEntity.getZ() - player.getZ(), livingEntity.damageSources().generic(), 0.0F);
                        }
                    }
                }
            }
            MiscProperties.setLungeTicks((LivingEntity) entity, tempLungeTicks);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.deathworm_gauntlet.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.deathworm_gauntlet.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
