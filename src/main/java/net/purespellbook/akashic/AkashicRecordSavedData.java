package net.purespellbook.akashic;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side ender-chest-style persistent storage for each player's Akashic Record.
 * Stores 4 pages of 6x3 (18) slots = 72 slots per player UUID in Overworld SavedData.
 */
public class AkashicRecordSavedData extends SavedData {
    public static final int PAGE_COUNT = 4;
    public static final int SLOTS_PER_PAGE = 18;
    public static final int TOTAL_SLOTS = PAGE_COUNT * SLOTS_PER_PAGE;
    private static final String DATA_NAME = "pure_spellbook_akashic_record";

    private final Map<UUID, NonNullList<ItemStack>> playerRecords = new HashMap<>();

    public static AkashicRecordSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                AkashicRecordSavedData::load,
                AkashicRecordSavedData::new,
                DATA_NAME
        );
    }

    public NonNullList<ItemStack> getRecord(UUID playerId) {
        return playerRecords.computeIfAbsent(playerId, id -> NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY));
    }

    public static AkashicRecordSavedData load(CompoundTag tag) {
        AkashicRecordSavedData data = new AkashicRecordSavedData();
        ListTag playersTag = tag.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playersTag.size(); i++) {
            CompoundTag entry = playersTag.getCompound(i);
            if (!entry.hasUUID("UUID")) {
                continue;
            }
            UUID uuid = entry.getUUID("UUID");
            NonNullList<ItemStack> stacks = NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY);
            ListTag itemsTag = entry.getList("Items", Tag.TAG_COMPOUND);
            for (int j = 0; j < itemsTag.size(); j++) {
                CompoundTag itemTag = itemsTag.getCompound(j);
                int slot = itemTag.getInt("Slot");
                if (slot >= 0 && slot < TOTAL_SLOTS) {
                    stacks.set(slot, ItemStack.of(itemTag));
                }
            }
            data.playerRecords.put(uuid, stacks);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag playersTag = new ListTag();
        for (Map.Entry<UUID, NonNullList<ItemStack>> entry : playerRecords.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            ListTag itemsTag = new ListTag();
            NonNullList<ItemStack> stacks = entry.getValue();
            for (int slot = 0; slot < stacks.size(); slot++) {
                ItemStack stack = stacks.get(slot);
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", slot);
                    stack.save(itemTag);
                    itemsTag.add(itemTag);
                }
            }
            playerTag.put("Items", itemsTag);
            playersTag.add(playerTag);
        }
        tag.put("Players", playersTag);
        return tag;
    }
}
