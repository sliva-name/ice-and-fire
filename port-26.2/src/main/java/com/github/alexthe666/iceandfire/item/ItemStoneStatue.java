package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.util.IafEntityNbt;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ItemStoneStatue extends Item {

    public ItemStoneStatue() {
        super(IafItemRegistry.defaultBuilder().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, @NotNull TooltipFlag flagIn) {
        if (IafItemData.has(stack)) {
            CompoundTag stored = IafItemData.copy(stack);
            boolean isPlayer = stored.getBooleanOr("IAFStoneStatuePlayerEntity", false);
            String id = stored.getStringOr("IAFStoneStatueEntityID", "");
            if (BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).orElse(null) != null) {
                EntityType type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).orElse(null);
                Component untranslated = isPlayer ? Component.translatable("entity.player.name") : Component.translatable(type.getDescriptionId());
                tooltip.accept(untranslated.copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, @NotNull Player player) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IAFStoneStatuePlayerEntity", true);
        IafItemData.write(itemStack, tag);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return InteractionResult.FAIL;
        } else {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            if (IafItemData.has(stack)) {
                CompoundTag stored = IafItemData.copy(stack);
                String id = stored.getStringOr("IAFStoneStatueEntityID", "");
                CompoundTag statueNBT = stored.getCompoundOrEmpty("IAFStoneStatueNBT");
                EntityStoneStatue statue = new EntityStoneStatue(IafEntityRegistry.STONE_STATUE.get(),
                    context.getLevel());
                statue.readAdditionalSaveData(IafEntityNbt.input(context.getLevel(), statueNBT));
                statue.setTrappedEntityTypeString(id);
                double d1 = context.getPlayer().getX() - (context.getClickedPos().getX() + 0.5);
                double d2 = context.getPlayer().getZ() - (context.getClickedPos().getZ() + 0.5);
                float yaw = (float) (Mth.atan2(d2, d1) * (180F / (float) Math.PI)) - 90;
                statue.yRotO = yaw;
                statue.setYRot(yaw);
                statue.yHeadRot = yaw;
                statue.yBodyRot = yaw;
                statue.yBodyRotO = yaw;
                statue.snapTo(context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 1, context.getClickedPos().getZ() + 0.5, yaw, 0);
                if (!context.getLevel().isClientSide()) {
                    context.getLevel().addFreshEntity(statue);
                    statue.readAdditionalSaveData(IafEntityNbt.input(context.getLevel(), stored));
                }
                statue.setCrackAmount(0);

                if (!context.getPlayer().isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
