package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * 26.1 {@link NearestAttackableTargetGoal} takes {@link TargetingConditions.Selector}
 * ({@code test(LivingEntity, ServerLevel)}). Predicates stay the 1.18 living-only checks.
 */
public final class IafSelectors {
    private IafSelectors() {
    }

    @Nullable
    public static TargetingConditions.Selector of(@Nullable Predicate<LivingEntity> predicate) {
        return predicate == null ? null : (living, serverLevel) -> predicate.test(living);
    }

    @Nullable
    public static TargetingConditions.Selector ofGuava(@Nullable com.google.common.base.Predicate<? super LivingEntity> predicate) {
        return predicate == null ? null : (living, serverLevel) -> predicate.apply(living);
    }
}
