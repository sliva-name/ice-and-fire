package com.github.alexthe666.iceandfire.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class IafDamageRegistry {
    public static final String GORGON_DMG_TYPE = "gorgon";
    public static final String DRAGON_FIRE_TYPE = "dragon_fire";
    public static final String DRAGON_ICE_TYPE = "dragon_ice";
    public static final String DRAGON_LIGHTNING_TYPE = "dragon_lightning";

    public static final ResourceKey<DamageType> GORGON = key(GORGON_DMG_TYPE);
    public static final ResourceKey<DamageType> DRAGON_FIRE = key(DRAGON_FIRE_TYPE);
    public static final ResourceKey<DamageType> DRAGON_ICE = key(DRAGON_ICE_TYPE);
    public static final ResourceKey<DamageType> DRAGON_LIGHTNING = key(DRAGON_LIGHTNING_TYPE);

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("iceandfire", path));
    }

    static class CustomEntityDamageSource extends DamageSource {
        public CustomEntityDamageSource(Holder<DamageType> type, @Nullable Entity damageSourceEntityIn) {
            super(type, damageSourceEntityIn);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    static class CustomIndirectEntityDamageSource extends DamageSource {
        public CustomIndirectEntityDamageSource(Holder<DamageType> type, Entity source, @Nullable Entity indirectEntityIn) {
            super(type, source, indirectEntityIn);
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
            LivingEntity livingentity = entityLivingBaseIn.getKillCredit();
            String s = "death.attack." + this.getMsgId();
            int index = entityLivingBaseIn.getRandom().nextInt(2);
            String s1 = s + "." + index;
            String s2 = s + ".attacker_" + index;
            return livingentity != null ? Component.translatable(s2, entityLivingBaseIn.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s1, entityLivingBaseIn.getDisplayName());
        }
    }

    private static Holder<DamageType> type(Entity entity, ResourceKey<DamageType> key) {
        Level level = entity.level();
        return level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
    }

    public static CustomEntityDamageSource causeGorgonDamage(@Nullable Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Gorgon damage requires a source entity");
        }
        return new CustomEntityDamageSource(type(entity, GORGON), entity);
    }

    public static CustomEntityDamageSource causeDragonFireDamage(@Nullable Entity entity) {
        return new CustomEntityDamageSource(type(entity, DRAGON_FIRE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonFireDamage(Entity source, @Nullable Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(type(source, DRAGON_FIRE), source, indirectEntityIn);
    }

    public static CustomEntityDamageSource causeDragonIceDamage(@Nullable Entity entity) {
        return new CustomEntityDamageSource(type(entity, DRAGON_ICE), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonIceDamage(Entity source, @Nullable Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(type(source, DRAGON_ICE), source, indirectEntityIn);
    }

    public static CustomEntityDamageSource causeDragonLightningDamage(@Nullable Entity entity) {
        return new CustomEntityDamageSource(type(entity, DRAGON_LIGHTNING), entity);
    }

    public static CustomIndirectEntityDamageSource causeIndirectDragonLightningDamage(Entity source, @Nullable Entity indirectEntityIn) {
        return new CustomIndirectEntityDamageSource(type(source, DRAGON_LIGHTNING), source, indirectEntityIn);
    }
}
