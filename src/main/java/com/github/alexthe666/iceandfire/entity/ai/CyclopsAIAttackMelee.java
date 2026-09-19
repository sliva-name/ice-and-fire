package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class CyclopsAIAttackMelee extends MeleeAttackGoal {

    public CyclopsAIAttackMelee(EntityCyclops creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity entity) {
        if (isCyclopsBlinded() && this.mob.distanceToSqr(entity) >= 36D) {
            this.stop();
            return;
        }
        super.checkAndPerformAttack(entity);
    }

    private boolean isCyclopsBlinded() {
        return this.mob instanceof EntityCyclops && ((EntityCyclops) this.mob).isBlinded();
    }
}
