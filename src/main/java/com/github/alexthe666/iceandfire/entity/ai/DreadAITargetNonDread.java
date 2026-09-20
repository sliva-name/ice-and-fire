package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDreadMob;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DreadAITargetNonDread extends NearestAttackableTargetGoal<LivingEntity> {

    public DreadAITargetNonDread(Mob entityIn, Class<LivingEntity> classTarget, boolean checkSight,
                                 TargetingConditions.Selector targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, (entity, level) -> {
            if (entityIn instanceof EntityDreadMob dread) {
                return dread.canDreadPursue(entity);
            }
            return isValidDreadPrey(entity) && targetSelector.test(entity, level);
        });
    }

    static boolean isValidDreadPrey(@Nullable LivingEntity target) {
        if (target == null || target instanceof IDreadMob || !DragonUtils.isAlive(target) || !DragonUtils.canHostilesTarget(target)) {
            return false;
        }
        return target instanceof Player;
    }

    @Override
    protected boolean canAttack(@Nullable LivingEntity target, @NotNull TargetingConditions targetPredicate) {
        if (this.mob instanceof EntityDreadMob dread) {
            return dread.canDreadPursue(target) && super.canAttack(target, targetPredicate);
        }
        return super.canAttack(target, targetPredicate) && isValidDreadPrey(target);
    }

}
