package com.github.alexthe666.iceandfire.entity.props;

import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDreadMob;
import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.github.alexthe666.iceandfire.item.ItemCarriedBlessing;
import com.github.alexthe666.iceandfire.item.ItemSilverArmor;
import com.github.alexthe666.iceandfire.item.OathType;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import com.github.alexthe666.iceandfire.world.IafDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class OathProperties {
    private static final String MISC_DATA = "MiscDataIaf";
    private static final String OATH = "Oath";
    private static final String FORSWORN_UNTIL = "ForswornUntil";
    private static final String FORSWORN_OATH = "ForswornOath";
    private static final String DEAD_ANSWERED = "DeadAnswered";
    private static final long SHAME_TICKS = 20L * 60L * 6L;
    private static final long RALLY_COOLDOWN = 20L * 12L;

    private OathProperties() {
    }

    public static OathType get(LivingEntity entity) {
        CompoundTag misc = misc(CitadelEntityData.getCitadelTag(entity));
        return misc == null ? null : OathType.byId(misc.getStringOr(OATH, ""));
    }

    public static void set(LivingEntity entity, OathType type) {
        CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(entity);
        CompoundTag misc = miscOrEmpty(root);
        misc.putString(OATH, type.name());
        root.put(MISC_DATA, misc);
        write(entity, root);
    }

    public static void clear(LivingEntity entity) {
        CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(entity);
        CompoundTag misc = miscOrEmpty(root);
        misc.remove(OATH);
        root.put(MISC_DATA, misc);
        write(entity, root);
    }

    public static void forswear(Player player, OathType broken) {
        CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(player);
        CompoundTag misc = miscOrEmpty(root);
        misc.remove(OATH);
        misc.putString(FORSWORN_OATH, broken.name());
        misc.putLong(FORSWORN_UNTIL, now(player) + SHAME_TICKS);
        root.put(MISC_DATA, misc);
        write(player, root);
    }

    public static boolean isForsworn(Player player) {
        CompoundTag misc = misc(CitadelEntityData.getCitadelTag(player));
        if (misc == null) {
            return false;
        }
        long until = misc.getLongOr(FORSWORN_UNTIL, 0L);
        if (until <= 0L) {
            return false;
        }
        if (now(player) >= until) {
            misc.remove(FORSWORN_UNTIL);
            misc.remove(FORSWORN_OATH);
            CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(player);
            root.put(MISC_DATA, misc);
            write(player, root);
            return false;
        }
        return true;
    }

    /** Carries the oath and the forsworn shame over to the respawned player; other misc data stays reset. */
    public static void copyAfterDeath(Player original, Player respawned) {
        CompoundTag from = misc(CitadelEntityData.getCitadelTag(original));
        if (from == null) {
            return;
        }
        CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(respawned);
        CompoundTag misc = miscOrEmpty(root);
        for (String key : new String[]{OATH, FORSWORN_OATH, FORSWORN_UNTIL}) {
            if (from.contains(key)) {
                misc.put(key, from.get(key).copy());
            }
        }
        root.put(MISC_DATA, misc);
    }

    public static boolean ignoresWaste(Player player) {
        return get(player) == OathType.WASTE;
    }

    public static boolean ignoresBarrow(Player player) {
        return get(player) == OathType.BARROW;
    }

    public static boolean ignoresTide(Player player) {
        return get(player) == OathType.TIDE;
    }

    public static void tick(Player player) {
        if (isForsworn(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, false, true));
        }
        OathType oath = get(player);
        if (oath == null) {
            return;
        }
        switch (oath) {
            case WASTE -> {
                if (player.onGround() && player.level().getBlockState(player.blockPosition().below()).is(BlockTags.SAND)) {
                    player.addEffect(new MobEffectInstance(MobEffects.SPEED, 50, 0, true, false));
                }
            }
            case TIDE -> {
                if (player.isInWater()) {
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 80, 0, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 80, 0, true, false));
                }
            }
            case BARROW -> {
            }
            case BLACK_FROST -> {
                if (IafDimensions.isDreadLands(player.level())) {
                    player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 80, 0, true, false));
                }
            }
        }
    }

    public static float outgoing(Player player, LivingEntity victim, float amount) {
        OathType oath = get(player);
        if (oath == OathType.WASTE && (victim instanceof EntityMyrmexBase || victim instanceof EntityDeathWorm)) {
            amount *= 1.35F;
        }
        if (oath == OathType.TIDE && (victim instanceof EntitySeaSerpent || victim instanceof EntitySiren)) {
            amount *= 1.35F;
        }
        if (oath == OathType.BARROW && isGraveFoe(victim)) {
            amount += 3.0F;
        }
        if (oath == OathType.BLACK_FROST && victim instanceof EntityDreadMob) {
            amount *= 1.4F;
        }
        if (player.getMainHandItem().is(com.github.alexthe666.iceandfire.item.IafItemRegistry.SHARD_KNIFE.get()) && isGraveFoe(victim)) {
            amount += 3.0F;
        }
        int silver = silverPieces(player);
        if (silver > 0 && isGraveFoe(victim)) {
            amount += silver;
        }
        return amount;
    }

    public static float incoming(Player player, DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker && isGraveFoe(attacker)) {
            int silver = silverPieces(player);
            if (silver > 0) {
                amount *= Math.max(0.55F, 1.0F - 0.08F * silver);
            }
        }
        if (get(player) == OathType.BLACK_FROST && IafDamageRegistry.DRAGON_FIRE_TYPE.equals(source.getMsgId())) {
            amount *= 0.75F;
        }
        OathType broken = forswornOath(player);
        if (broken != null && source.getEntity() instanceof LivingEntity attacker && matches(broken, attacker)) {
            amount *= 1.35F;
        }
        return amount;
    }

    public static void answer(Player player, LivingEntity foe) {
        if (get(player) != OathType.BARROW || isGraveFoe(foe) || player.level().isClientSide()) {
            return;
        }
        if (!(player.level() instanceof ServerLevel server)) {
            return;
        }
        long time = now(player);
        CompoundTag root = CitadelEntityData.getOrCreateCitadelTag(player);
        CompoundTag misc = miscOrEmpty(root);
        if (time < misc.getLongOr(DEAD_ANSWERED, 0L)) {
            return;
        }
        int called = 0;
        AABB near = player.getBoundingBox().inflate(14.0D);
        for (Mob dead : server.getEntitiesOfClass(Mob.class, near, mob -> mob.isAlive() && isGraveFoe(mob) && mob.getTarget() != player)) {
            if (dead instanceof com.github.alexthe666.iceandfire.entity.EntityDreadQueen) {
                continue;
            }
            dead.setTarget(foe);
            if (++called >= 4) {
                break;
            }
        }
        if (called == 0) {
            return;
        }
        misc.putLong(DEAD_ANSWERED, time + RALLY_COOLDOWN);
        root.put(MISC_DATA, misc);
        write(player, root);
        player.sendSystemMessage(Component.translatable("oath.message.dead"));
    }

    public static boolean isGraveFoe(LivingEntity entity) {
        return entity instanceof EntityGhost
            || entity instanceof EntityDreadMob
            || entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD);
    }

    private static boolean matches(OathType oath, LivingEntity entity) {
        return switch (oath) {
            case WASTE -> entity instanceof EntityMyrmexBase || entity instanceof EntityDeathWorm;
            case TIDE -> entity instanceof EntitySeaSerpent || entity instanceof EntitySiren;
            case BARROW -> isGraveFoe(entity);
            case BLACK_FROST -> entity instanceof EntityDreadMob;
        };
    }

    private static OathType forswornOath(Player player) {
        if (!isForsworn(player)) {
            return null;
        }
        CompoundTag misc = misc(CitadelEntityData.getCitadelTag(player));
        return misc == null ? null : OathType.byId(misc.getStringOr(FORSWORN_OATH, ""));
    }

    private static long now(Player player) {
        if (player.level() instanceof ServerLevel server && server.getServer() != null) {
            ServerLevel overworld = server.getServer().overworld();
            if (overworld != null) {
                return overworld.getGameTime();
            }
        }
        return player.level().getGameTime();
    }

    private static int silverPieces(LivingEntity entity) {
        int pieces = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (entity.getItemBySlot(slot).getItem() instanceof ItemSilverArmor) {
                pieces++;
            }
        }
        if (entity instanceof Player player && ItemCarriedBlessing.carries(player, com.github.alexthe666.iceandfire.item.IafItemRegistry.SILVER_BROOCH.get())) {
            pieces++;
        }
        return pieces;
    }

    private static CompoundTag misc(CompoundTag root) {
        if (root == null || !root.contains(MISC_DATA)) {
            return null;
        }
        return root.getCompoundOrEmpty(MISC_DATA);
    }

    private static CompoundTag miscOrEmpty(CompoundTag root) {
        CompoundTag misc = misc(root);
        return misc == null ? new CompoundTag() : misc;
    }

    private static void write(LivingEntity entity, CompoundTag root) {
        CitadelEntityData.setCitadelTag(entity, root);
        if (!entity.level().isClientSide()) {
            com.github.alexthe666.citadel.network.PropertiesNetwork.send(entity);
        }
    }
}
