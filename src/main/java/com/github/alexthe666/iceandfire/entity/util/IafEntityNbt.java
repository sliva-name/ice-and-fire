package com.github.alexthe666.iceandfire.entity.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

/**
 * 1.18 {@code Entity.save(CompoundTag)} / {@code load(CompoundTag)} on 26.1
 * {@link ValueInput} / {@link net.minecraft.world.level.storage.ValueOutput}.
 */
public final class IafEntityNbt {
    private IafEntityNbt() {
    }

    public static ValueInput input(Level level, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag);
    }

    public static CompoundTag save(Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.level().registryAccess());
        entity.save(output);
        return output.buildResult();
    }

    public static CompoundTag saveWithoutId(Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.level().registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
    }

    public static void load(Entity entity, CompoundTag tag) {
        entity.load(input(entity.level(), tag));
    }
}
