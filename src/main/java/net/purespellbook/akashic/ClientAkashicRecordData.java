package net.purespellbook.akashic;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Client-side mirror of the local player's Akashic Record slots for spell bar,
 * mana cost, cast time, and tooltip calculations.
 */
public final class ClientAkashicRecordData {
    private static final NonNullList<ItemStack> SLOTS =
            NonNullList.withSize(AkashicRecordSavedData.TOTAL_SLOTS, ItemStack.EMPTY);

    private ClientAkashicRecordData() {}

    public static void setAll(List<ItemStack> stacks) {
        for (int i = 0; i < AkashicRecordSavedData.TOTAL_SLOTS; i++) {
            SLOTS.set(i, i < stacks.size() ? stacks.get(i).copy() : ItemStack.EMPTY);
        }
    }

    public static NonNullList<ItemStack> getAll() {
        return SLOTS;
    }

    public static void clear() {
        for (int i = 0; i < SLOTS.size(); i++) {
            SLOTS.set(i, ItemStack.EMPTY);
        }
    }
}
