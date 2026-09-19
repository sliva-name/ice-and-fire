package com.github.alexthe666.iceandfire.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.stats.Stats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 1.18 used {@code ForgeSpawnEggItem(Supplier)} so eggs could register
 * before entity types existed. 26.1 vanilla eggs need {@code ENTITY_DATA}
 * at construction; this keeps the supplier lookup until use time.
 */
public class IafSpawnEggItem extends SpawnEggItem {
    private final RegistryObject<? extends EntityType<?>> type;

    public IafSpawnEggItem(RegistryObject<? extends EntityType<?>> type, Properties properties) {
        super(properties);
        this.type = type;
    }

    private @Nullable EntityType<?> entityType() {
        return type.isPresent() ? type.get() : null;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        BlockState blockState = level.getBlockState(pos);
        EntityType<?> entityType = entityType();
        if (entityType == null) {
            return InteractionResult.FAIL;
        }
        if (level.getBlockEntity(pos) instanceof Spawner spawner) {
            if (!serverLevel.isSpawnerBlockEnabled()) {
                if (context.getPlayer() instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(Component.translatable("advMode.notEnabled.spawner"));
                }
                return InteractionResult.FAIL;
            }
            spawner.setEntityId(entityType, level.getRandom());
            level.sendBlockUpdated(pos, blockState, blockState, 3);
            level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }
        BlockPos spawnPos = blockState.getCollisionShape(level, pos).isEmpty() ? pos : pos.relative(clickedFace);
        return spawnMob(context.getPlayer(), stack, level, spawnPos, true, !Objects.equals(pos, spawnPos) && clickedFace == Direction.UP);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = hitResult.getBlockPos();
        if (!(level.getBlockState(pos).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
        }
        if (level.mayInteract(player, pos) && player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            InteractionResult result = spawnMob(player, stack, level, pos, false, false);
            if (result == InteractionResult.SUCCESS) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            return result;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult spawnMob(@Nullable LivingEntity user, ItemStack stack, Level level, BlockPos spawnPos, boolean tryMoveDown, boolean movedUp) {
        EntityType<?> entityType = entityType();
        if (entityType == null) {
            return InteractionResult.FAIL;
        }
        if (!entityType.isAllowedInPeaceful() && level.getDifficulty() == Difficulty.PEACEFUL) {
            return InteractionResult.FAIL;
        }
        if (entityType.spawn((ServerLevel) level, stack, user, spawnPos, EntitySpawnReason.SPAWN_ITEM_USE, tryMoveDown, movedUp) != null) {
            stack.consume(1, user);
            level.gameEvent(user, GameEvent.ENTITY_PLACE, spawnPos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean shouldPrintOpWarning(@NotNull ItemStack stack, @Nullable Player player) {
        EntityType<?> entityType = entityType();
        return player != null && entityType != null
            && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
            && entityType.onlyOpCanSetNbt();
    }
}
