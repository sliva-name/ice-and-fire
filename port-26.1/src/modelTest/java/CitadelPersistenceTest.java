import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.citadel.server.item.CustomToolMaterial;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Plain-main regression probe: real 26.1 NBT/codec/material APIs, no Ice and Fire entity dependencies. */
public final class CitadelPersistenceTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        CompoundTag firstEntity = new CompoundTag();
        CompoundTag secondEntity = new CompoundTag();
        firstEntity.putString("other_mod", "preserved");
        CompoundTag live = CitadelEntityData.getOrCreateTag(firstEntity);
        check(live == CitadelEntityData.getOrCreateTag(firstEntity), "get-or-create is attached and stable");
        live.putInt("TicksUntilUnfrozen", 40);
        check(CitadelEntityData.getOrCreateTag(secondEntity).isEmpty(), "entity isolation");
        CompoundTag replacement = new CompoundTag();
        CompoundTag nested = new CompoundTag();
        nested.putInt("TicksInLove", 12);
        replacement.put("MiscDataIaf", nested);
        CitadelEntityData.storeTag(firstEntity, replacement);
        nested.putInt("TicksInLove", 99);
        check(CitadelEntityData.getOrCreateTag(firstEntity).getCompoundOrEmpty("MiscDataIaf")
                .getIntOr("TicksInLove", 0) == 12, "store owns a deep copy");
        check(!CitadelEntityData.getOrCreateTag(firstEntity).contains("TicksUntilUnfrozen"), "replace rather than merge");
        check(firstEntity.getStringOr("other_mod", "").equals("preserved"), "other mod data preserved");

        // Forge Entity.store serializes this exact enclosing compound under ForgeData.
        CompoundTag entitySave = new CompoundTag();
        entitySave.put("ForgeData", firstEntity);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NbtIo.write(entitySave, new DataOutputStream(bytes));
        CompoundTag loaded = NbtIo.read(new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        check(loaded.getCompoundOrEmpty("ForgeData").equals(firstEntity), "binary save/load round trip");
        CompoundTag malformed = new CompoundTag();
        malformed.putString(CitadelEntityData.DATA_KEY, "wrong type");
        check(CitadelEntityData.getOrCreateTag(malformed).isEmpty(), "malformed storage repaired");
        check(malformed.getCompound(CitadelEntityData.DATA_KEY).isPresent(), "repair remains attached");

        UUID uuid = UUID.randomUUID();
        PropertiesMessage message = new PropertiesMessage(Identifier.parse("minecraft:overworld"), 17, uuid, replacement);
        replacement.putInt("late_mutation", 1);
        check(!message.tag().contains("late_mutation"), "packet captures snapshot");
        message.tag().putInt("accessor_mutation", 1);
        check(!message.tag().contains("accessor_mutation"), "packet accessor protects snapshot");
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            PropertiesMessage.write(message, buffer);
            check(PropertiesMessage.read(buffer).equals(message), "packet round trip includes dimension and UUID");
        } finally {
            buffer.release();
        }

        int[] defense = {2, 5, 7, 3};
        CustomArmorMaterial material = new CustomArmorMaterial("forest troll", 20, defense, 10,
                SoundEvent.createVariableRangeEvent(Identifier.parse("test:equip")), 1, 0);
        defense[0] = 99;
        check(material.getDefenseForSlot(EquipmentSlot.FEET) == 2, "defense array copied");
        check(material.getDurabilityForSlot(EquipmentSlot.HEAD) == 20, "upstream absolute durability");
        check(material.getRepairIngredient().isEmpty(), "unset repair ingredient is absent");
        var repair = TagKey.create(Registries.ITEM, Identifier.parse("iceandfire:repairs_troll_forest_armor"));
        var asset = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.parse("iceandfire:troll_forest"));
        var nativeMaterial = material.toArmorMaterial(repair, asset);
        check(nativeMaterial.defense().get(ArmorType.HELMET) == 3, "helmet defense order");
        check(nativeMaterial.defense().get(ArmorType.CHESTPLATE) == 7, "chestplate defense order");
        check(nativeMaterial.defense().get(ArmorType.LEGGINGS) == 5, "leggings defense order");
        check(ArmorType.BOOTS.getDurability(nativeMaterial.durability()) == 260, "native multiplier semantics");
        check(nativeMaterial.repairIngredient().equals(repair) && nativeMaterial.assetId().equals(asset), "explicit repair and asset keys");
        check(nativeMaterial.enchantmentValue() == 10 && nativeMaterial.toughness() == 1, "material values preserved");
        // Upstream's constructor takes damage before speed; the native record reverses them.
        CustomToolMaterial tool = new CustomToolMaterial("silver", 2, 460, 1.0F, 11.0F, 18);
        var incorrect = TagKey.create(Registries.BLOCK, Identifier.parse("minecraft:incorrect_for_iron_tool"));
        var toolRepair = TagKey.create(Registries.ITEM, Identifier.parse("iceandfire:tool_materials/silver"));
        var nativeTool = tool.toToolMaterial(incorrect, toolRepair);
        check(tool.getAttackDamageBonus() == 1.0F && tool.getSpeed() == 11.0F, "legacy tool argument order");
        check(nativeTool.attackDamageBonus() == 1.0F && nativeTool.speed() == 11.0F, "native tool argument order");
        check(nativeTool.durability() == 460 && nativeTool.enchantmentValue() == 18, "tool durability and enchantability");
        check(nativeTool.incorrectBlocksForDrops().equals(incorrect), "explicit harvest replacement tag");
        check(nativeTool.repairItems().equals(toolRepair), "native repair tag");
        check(tool.getRepairIngredient().isEmpty(), "unset tool repair ingredient is absent");
        System.out.println("CitadelPersistenceTest: " + checks + " checks passed");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
