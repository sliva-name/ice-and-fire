package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.entity.EntityGhost;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BlockGraveyardSoil extends Block implements IBlockItemHoverText {

    public BlockGraveyardSoil() {
        super(
            IafBlockRegistry.id(Properties
                .of().mapColor(MapColor.DIRT)
                .sound(SoundType.GRAVEL)
                .strength(5, 1F)
                .randomTicks())
		);
    }

    @Override
    public void appendItemHoverText(@NotNull ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                    Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("block.iceandfire.graveyard_soil.desc").withStyle(ChatFormatting.GRAY));
    }


    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel worldIn, @NotNull BlockPos pos, @NotNull net.minecraft.util.RandomSource rand) {
        if (!worldIn.isClientSide()) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (!worldIn.isBrightOutside() && !worldIn.getBlockState(pos.above()).canOcclude() && rand.nextInt(9) == 0 && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
                int checkRange = 32;
                int k = worldIn.getEntitiesOfClass(EntityGhost.class, (new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).inflate(checkRange)).size();
                if (k < 10) {
                    EntityGhost ghost = IafEntityRegistry.GHOST.get().create(worldIn, EntitySpawnReason.SPAWNER);
                    if (ghost == null) {
                        return;
                    }
                    ghost.snapTo(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        ThreadLocalRandom.current().nextFloat() * 360F, 0);
                    if (!worldIn.isClientSide()) {
                        ghost.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(pos), EntitySpawnReason.SPAWNER, null);
                        worldIn.addFreshEntity(ghost);
                    }
                    ghost.setAnimation(EntityGhost.ANIMATION_SCARE);
                    ghost.setHomeTo(pos, 16);
                }
            }
        }
    }
}
