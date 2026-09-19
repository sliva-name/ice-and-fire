package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ItemCyclopsEye extends Item {

    public ItemCyclopsEye() {
        super(IafItemRegistry.defaultBuilder().durability(500));
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull net.minecraft.server.level.ServerLevel world, @NotNull Entity entity, @NotNull net.minecraft.world.entity.EquipmentSlot slot) {
        if (!IafItemData.has(stack)) {
            IafItemData.write(stack, new CompoundTag());
        } else {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (living.getMainHandItem() == stack || living.getOffhandItem() == stack) {
                    double range = 15;
                    boolean inflictedDamage = false;
                    for (Mob LivingEntity : world.getEntitiesOfClass(Mob.class, new AABB(living.getX() - range, living.getY() - range, living.getZ() - range, living.getX() + range, living.getY() + range, living.getZ() + range))) {
                        if (!LivingEntity.is(living) && !LivingEntity.isAlliedTo(living) && (LivingEntity.getTarget() == living || LivingEntity.getLastHurtByMob() == living || LivingEntity instanceof Enemy)) {
                            LivingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 1));
                            inflictedDamage = true;
                        }
                    }
                    if (inflictedDamage) {
                        IafItemData.update(stack, tag -> tag.putInt("HurtingTicks", tag.getIntOr("HurtingTicks", 0) + 1));
                    }
                }
                if (IafItemData.copy(stack).getIntOr("HurtingTicks", 0) > 120) {
                    InteractionHand hand = living.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                    stack.hurtAndBreak(1, living, hand);
                    IafItemData.update(stack, tag -> tag.putInt("HurtingTicks", 0));
                }
            }

        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        tooltip.accept(Component.translatable("item.iceandfire.legendary_weapon.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.cyclops_eye.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("item.iceandfire.cyclops_eye.desc_1").withStyle(ChatFormatting.GRAY));
    }
}
