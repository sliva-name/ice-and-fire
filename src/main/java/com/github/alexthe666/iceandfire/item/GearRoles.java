package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Mid-game sets and mounts keep a job after a dragon exists.
 * Full sets are four pieces. Mixed deathworm colors still count as one set.
 * Scale colors count together when they share a dragon element.
 */
public final class GearRoles {
    private static final EquipmentSlot[] ARMOR = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private GearRoles() {
    }

    public static void tick(Player player) {
        int worms = count(player, ItemDeathwormArmor.class);
        if (worms >= 2 && player.onGround() && player.level().getBlockState(player.blockPosition().below()).is(BlockTags.SAND)) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 50, 0, true, false));
        }
        if (worms >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, 50, 0, true, false));
        }
        if (count(player, ItemTrollArmor.class) >= 4 && !player.level().isBrightOutside()) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 50, 0, true, false));
        }
        if (scaleCount(player, DragonType.FIRE) >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 50, 0, true, false));
        }
        if (scaleCount(player, DragonType.ICE) >= 4) {
            player.removeEffect(MobEffects.SLOWNESS);
        }
        if (scaleCount(player, DragonType.LIGHTNING) >= 4 && player.level().isRaining()) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 50, 1, true, false));
        }
        if (myrmexCount(player, true) >= 4 || myrmexCount(player, false) >= 4) {
            player.removeEffect(MobEffects.POISON);
        }
        mount(player);
    }

    public static float scaleBreath(LivingEntity wearer, String damageType, float amount) {
        DragonType type = breathType(damageType);
        if (type != null && scaleCount(wearer, type) >= 4) {
            return amount * 0.65F;
        }
        return amount;
    }

    public static boolean hiveTruce(Player player, EntityMyrmexBase myrmex) {
        return myrmexCount(player, myrmex.isJungle()) >= 4;
    }

    private static void mount(Player player) {
        if (player.getVehicle() instanceof EntityHippogryph) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, true, false));
        } else if (player.getVehicle() instanceof EntityAmphithere) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 40, 1, true, false));
        } else if (player.getVehicle() instanceof EntityDeathWorm) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 240, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.HASTE, 50, 0, true, false));
        } else if (player.getVehicle() instanceof EntityHippocampus && player.isInWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, true, false));
        }
    }

    private static DragonType breathType(String damageType) {
        if (IafDamageRegistry.DRAGON_FIRE_TYPE.equals(damageType)) {
            return DragonType.FIRE;
        }
        if (IafDamageRegistry.DRAGON_ICE_TYPE.equals(damageType)) {
            return DragonType.ICE;
        }
        if (IafDamageRegistry.DRAGON_LIGHTNING_TYPE.equals(damageType)) {
            return DragonType.LIGHTNING;
        }
        return null;
    }

    private static int count(LivingEntity entity, Class<?> type) {
        int pieces = 0;
        for (EquipmentSlot slot : ARMOR) {
            if (type.isInstance(entity.getItemBySlot(slot).getItem())) {
                pieces++;
            }
        }
        return pieces;
    }

    private static int scaleCount(LivingEntity entity, DragonType type) {
        int pieces = 0;
        for (EquipmentSlot slot : ARMOR) {
            if (entity.getItemBySlot(slot).getItem() instanceof ItemScaleArmor armor && armor.eggType.dragonType == type) {
                pieces++;
            }
        }
        return pieces;
    }

    private static int myrmexCount(LivingEntity entity, boolean jungle) {
        CustomArmorMaterial material = jungle
            ? IafItemRegistry.MYRMEX_JUNGLE_ARMOR_MATERIAL
            : IafItemRegistry.MYRMEX_DESERT_ARMOR_MATERIAL;
        int pieces = 0;
        for (EquipmentSlot slot : ARMOR) {
            if (entity.getItemBySlot(slot).getItem() instanceof ItemModArmor armor && armor.iafMaterial() == material) {
                pieces++;
            }
        }
        return pieces;
    }

    public static boolean fullJungleMyrmex(LivingEntity entity) {
        return myrmexCount(entity, true) >= 4;
    }
}
