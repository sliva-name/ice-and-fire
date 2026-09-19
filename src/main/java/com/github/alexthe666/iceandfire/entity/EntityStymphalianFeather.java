package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.entity.util.IafDrops;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.packets.SpawnEntity;
import org.jetbrains.annotations.NotNull;

public class EntityStymphalianFeather extends AbstractArrow {

    public EntityStymphalianFeather(EntityType<? extends AbstractArrow> t, Level worldIn) {
        super(t, worldIn);
    }

    public EntityStymphalianFeather(EntityType<? extends AbstractArrow> t, Level worldIn, LivingEntity shooter) {
        this(t, worldIn, shooter, ItemStack.EMPTY);
    }

    public EntityStymphalianFeather(EntityType<? extends AbstractArrow> t, Level worldIn, LivingEntity shooter, ItemStack weapon) {
        super(t, shooter, worldIn, new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER.get()), IafArrows.firedFrom(weapon));
        this.setBaseDamage(IafConfig.stymphalianBirdFeatherAttackStength);
    }

    public EntityStymphalianFeather(SpawnEntity spawnEntity, Level world) {
        this(IafEntityRegistry.STYMPHALIAN_FEATHER.get(), world);
    }
    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        if (IafConfig.stymphalianBirdFeatherDropChance > 0) {
            if (!this.level().isClientSide() && this.random.nextInt(IafConfig.stymphalianBirdFeatherDropChance) == 0) {
                IafDrops.spawn(this, getPickupItem(), 0.1F);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > 100) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityHit) {
        Entity shootingEntity = this.getOwner();
        if (shootingEntity instanceof EntityStymphalianBird && entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
            return;
        } else {
            super.onHitEntity(entityHit);
            if (entityHit.getEntity() != null && entityHit.getEntity() instanceof EntityStymphalianBird) {
                LivingEntity LivingEntity = (LivingEntity) entityHit.getEntity();
                LivingEntity.setArrowCount(LivingEntity.getArrowCount() - 1);
                ItemStack itemstack1 = LivingEntity.isUsingItem() ? LivingEntity.getUseItem() : ItemStack.EMPTY;
                if (itemstack1.has(net.minecraft.core.component.DataComponents.BLOCKS_ATTACKS)) {
                    damageShield(LivingEntity, 1.0F);
                }
            }

        }
    }

    protected void damageShield(LivingEntity entity, float damage) {
        if (damage >= 3.0F && entity.getUseItem().has(net.minecraft.core.component.DataComponents.BLOCKS_ATTACKS)) {
            ItemStack copyBeforeUse = entity.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            InteractionHand Hand = entity.getUsedItemHand();
            entity.getUseItem().hurtAndBreak(i, entity, Hand);
            if (entity.getUseItem().isEmpty()) {
                if (entity instanceof Player) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((Player) entity, copyBeforeUse, Hand);
                }

                if (Hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
                    entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    entity.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                this.playSound(SoundEvents.SHIELD_BREAK.value(), 0.8F, 0.8F + this.level().getRandom().nextFloat() * 0.4F);
            }
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER.get());
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return getPickupItem();
    }
}
