package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IafOwners;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

/**
 * Each biome is two sides. Desert hives hunt death worms. Jungle hives hunt amphitheres.
 * A tame creature whose owner the hive already trusts is left alone.
 */
public final class BiomeFactions {
    private BiomeFactions() {
    }

    public static boolean huntsDeathWorm(EntityMyrmexBase myrmex, LivingEntity entity) {
        if (myrmex.isJungle() || !(entity instanceof EntityDeathWorm worm) || !DragonUtils.isAlive(worm)) {
            return false;
        }
        return !sparesTame(myrmex, worm.isTame(), IafOwners.getUUID(worm));
    }

    public static boolean huntsAmphithere(EntityMyrmexBase myrmex, LivingEntity entity) {
        if (!myrmex.isJungle() || !(entity instanceof EntityAmphithere bird) || !DragonUtils.isAlive(bird)) {
            return false;
        }
        return !sparesTame(myrmex, bird.isTame(), IafOwners.getUUID(bird));
    }

    public static boolean amphithereHuntsMyrmex(EntityAmphithere bird, LivingEntity entity) {
        if (!(entity instanceof EntityMyrmexBase myrmex) || !myrmex.isJungle() || !DragonUtils.isAlive(myrmex)) {
            return false;
        }
        return !sparesTame(myrmex, bird.isTame(), IafOwners.getUUID(bird));
    }

    private static boolean sparesTame(EntityMyrmexBase myrmex, boolean tame, UUID owner) {
        return tame && owner != null && myrmex.getHive() != null
            && !myrmex.getHive().isPlayerReputationLowEnoughToFight(owner);
    }
}
