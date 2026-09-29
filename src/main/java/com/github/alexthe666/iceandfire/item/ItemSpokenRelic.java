package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.props.OathProperties;
import com.github.alexthe666.iceandfire.world.IafDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemSpokenRelic extends Item {
    public enum Kind {
        MOON_PHIAL(false, 600),
        MARCH_HORN(false, 500),
        SEA_SHELL(false, 500),
        GRAVE_CANDLE(true, 40),
        HIVE_SEAL(true, 20),
        WATCHFIRE(true, 40),
        BIRD_RATTLE(false, 400);

        final boolean consumes;
        final int cooldown;

        Kind(boolean consumes, int cooldown) {
            this.consumes = consumes;
            this.cooldown = cooldown;
        }
    }

    private final Kind kind;

    public ItemSpokenRelic(Kind kind) {
        super(kind.consumes ? IafItemRegistry.defaultBuilder().stacksTo(16) : IafItemRegistry.unstackable());
        this.kind = kind;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel server) || !act(server, player)) {
            return InteractionResult.FAIL;
        }
        if (kind.consumes) {
            stack.shrink(1);
        }
        if (kind.cooldown > 0) {
            player.getCooldowns().addCooldown(stack, kind.cooldown);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean act(ServerLevel server, Player player) {
        return switch (kind) {
            case MOON_PHIAL -> phial(server, player);
            case MARCH_HORN -> horn(server, player);
            case SEA_SHELL -> shell(server, player);
            case GRAVE_CANDLE -> candle(server, player);
            case HIVE_SEAL -> seal(server, player);
            case WATCHFIRE -> watchfire(server, player);
            case BIRD_RATTLE -> rattle(server, player);
        };
    }

    private boolean phial(ServerLevel server, Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, false, true));
        glowDead(server, player, 10.0D, 200);
        server.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, 1.4F);
        return true;
    }

    private boolean horn(ServerLevel server, Player player) {
        for (Player ally : server.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(12.0D), LivingEntity::isAlive)) {
            ally.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 0, false, true));
            ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, 0, false, true));
        }
        if (OathProperties.ignoresWaste(player)) {
            for (EntityDeathWorm worm : server.getEntitiesOfClass(EntityDeathWorm.class, player.getBoundingBox().inflate(18.0D), EntityDeathWorm::isAlive)) {
                if (!worm.isTame()) {
                    worm.setTarget(null);
                }
            }
        }
        server.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.2F, 0.7F);
        return true;
    }

    private boolean shell(ServerLevel server, Player player) {
        if (!player.isInWater() && !server.isRaining()) {
            player.sendSystemMessage(Component.translatable("item.iceandfire.sea_shell.dry"));
            return false;
        }
        for (Player ally : server.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(8.0D), LivingEntity::isAlive)) {
            if (ally == player || ally.isInWater()) {
                ally.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, false, true));
                ally.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 400, 0, false, true));
            }
        }
        server.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 0.6F);
        return true;
    }

    private boolean candle(ServerLevel server, Player player) {
        glowDead(server, player, 12.0D, 160);
        Mob foe = null;
        double best = Double.MAX_VALUE;
        for (Mob mob : server.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(12.0D), LivingEntity::isAlive)) {
            if (OathProperties.isGraveFoe(mob) || mob.getTarget() != player) {
                continue;
            }
            double dist = mob.distanceToSqr(player);
            if (dist < best) {
                best = dist;
                foe = mob;
            }
        }
        if (foe != null && OathProperties.get(player) == OathType.BARROW) {
            OathProperties.answer(player, foe);
        }
        server.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.6F, 1.2F);
        return true;
    }

    private boolean seal(ServerLevel server, Player player) {
        com.github.alexthe666.iceandfire.world.MyrmexWorldData data = com.github.alexthe666.iceandfire.world.MyrmexWorldData.get(server);
        if (data == null) {
            player.sendSystemMessage(Component.translatable("item.iceandfire.hive_seal.none"));
            return false;
        }
        com.github.alexthe666.iceandfire.entity.util.MyrmexHive hive = data.getNearestHive(player.blockPosition(), 48);
        if (hive == null) {
            player.sendSystemMessage(Component.translatable("item.iceandfire.hive_seal.none"));
            return false;
        }
        int gift = 10;
        if (GearRoles.fullJungleMyrmex(player)) {
            gift += 6;
        }
        hive.modifyPlayerReputation(player.getUUID(), gift);
        server.playSound(null, player.blockPosition(), SoundEvents.BEEHIVE_SHEAR, SoundSource.PLAYERS, 0.7F, 1.1F);
        return true;
    }

    private boolean watchfire(ServerLevel server, Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 360, 0, false, true));
        if (IafDimensions.isDreadLands(server)) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 0, false, true));
        }
        for (LivingEntity dead : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0D), OathProperties::isGraveFoe)) {
            dead.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 0, false, true));
            dead.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0, false, true));
        }
        server.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.9F, 0.7F);
        return true;
    }

    private boolean rattle(ServerLevel server, Player player) {
        for (EntityStymphalianBird bird : server.getEntitiesOfClass(EntityStymphalianBird.class, player.getBoundingBox().inflate(16.0D), LivingEntity::isAlive)) {
            if (bird.getTarget() == player) {
                bird.setTarget(null);
            }
        }
        server.playSound(null, player.blockPosition(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 0.5F, 1.8F);
        return true;
    }

    private static void glowDead(ServerLevel server, Player player, double range, int time) {
        for (LivingEntity dead : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range), OathProperties::isGraveFoe)) {
            dead.addEffect(new MobEffectInstance(MobEffects.GLOWING, time, 0, false, false));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull TooltipDisplay display,
                                @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.accept(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
