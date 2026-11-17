package net.purespellbook.gui;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.purespellbook.akashic.AkashicRecordHelper;
import net.purespellbook.akashic.AkashicRecordSavedData;
import net.purespellbook.akashic.ClientAkashicRecordData;
import net.purespellbook.network.ModNetwork;
import net.purespellbook.network.SyncAkashicRecordPacket;
import net.purespellbook.registry.ModMenuTypes;

/**
 * Ender-chest-style container menu for the Akashic Record.
 * Displays 6x3 (18) scroll slots per page across 4 pages (total 72 slots).
 */
public class AkashicRecordMenu extends AbstractContainerMenu {
    public static final int COLS = 6;
    public static final int ROWS = 3;
    public static final int SLOT_START_X = 35;
    public static final int SLOT_START_Y = 18;

    private final Container recordContainer;
    private final Player player;
    private int currentPage = 0;

    public AkashicRecordMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, createClientContainer(), null);
    }

    public AkashicRecordMenu(int containerId, Inventory playerInv, ServerPlayer serverPlayer) {
        this(containerId, playerInv, createServerContainer(serverPlayer), serverPlayer);
    }

    private AkashicRecordMenu(int containerId, Inventory playerInv, Container recordContainer, ServerPlayer serverPlayer) {
        super(ModMenuTypes.AKASHIC_RECORD_MENU.get(), containerId);
        this.recordContainer = recordContainer;
        this.player = playerInv.player;

        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return currentPage;
            }

            @Override
            public void set(int value) {
                currentPage = Math.max(0, Math.min(AkashicRecordSavedData.PAGE_COUNT - 1, value));
            }
        });

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int localIdx = row * COLS + col;
                this.addSlot(new PagedScrollSlot(recordContainer, localIdx,
                        SLOT_START_X + col * 18, SLOT_START_Y + row * 18));
            }
        }

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }

        if (serverPlayer != null && serverPlayer.getServer() != null) {
            NonNullList<ItemStack> slots = AkashicRecordSavedData.get(serverPlayer.getServer()).getRecord(serverPlayer.getUUID());
            ModNetwork.sendToPlayer(serverPlayer, new SyncAkashicRecordPacket(slots));
        }
    }

    private static Container createClientContainer() {
        SimpleContainer container = new SimpleContainer(AkashicRecordSavedData.TOTAL_SLOTS);
        NonNullList<ItemStack> cached = ClientAkashicRecordData.getAll();
        for (int i = 0; i < AkashicRecordSavedData.TOTAL_SLOTS; i++) {
            container.setItem(i, cached.get(i).copy());
        }
        return container;
    }

    private static Container createServerContainer(ServerPlayer player) {
        AkashicRecordSavedData data = AkashicRecordSavedData.get(player.server);
        NonNullList<ItemStack> backing = data.getRecord(player.getUUID());
        return new Container() {
            @Override
            public int getContainerSize() {
                return AkashicRecordSavedData.TOTAL_SLOTS;
            }

            @Override
            public boolean isEmpty() {
                for (ItemStack s : backing) {
                    if (!s.isEmpty()) return false;
                }
                return true;
            }

            @Override
            public ItemStack getItem(int slot) {
                return slot >= 0 && slot < backing.size() ? backing.get(slot) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                if (slot < 0 || slot >= backing.size() || backing.get(slot).isEmpty()) {
                    return ItemStack.EMPTY;
                }
                ItemStack split = backing.get(slot).split(amount);
                if (!split.isEmpty()) {
                    setChanged();
                }
                return split;
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                if (slot < 0 || slot >= backing.size()) {
                    return ItemStack.EMPTY;
                }
                ItemStack prev = backing.get(slot);
                backing.set(slot, ItemStack.EMPTY);
                return prev;
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if (slot >= 0 && slot < backing.size()) {
                    backing.set(slot, stack);
                    setChanged();
                }
            }

            @Override
            public void setChanged() {
                data.setDirty();
            }

            @Override
            public boolean stillValid(Player p) {
                return true;
            }

            @Override
            public void clearContent() {
                for (int i = 0; i < backing.size(); i++) {
                    backing.set(i, ItemStack.EMPTY);
                }
                setChanged();
            }
        };
    }

    public int getPage() {
        return currentPage;
    }

    public void setPage(int page) {
        this.currentPage = Math.max(0, Math.min(AkashicRecordSavedData.PAGE_COUNT - 1, page));
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            AkashicRecordSavedData data = AkashicRecordSavedData.get(serverPlayer.getServer());
            data.setDirty();
            ModNetwork.sendToPlayer(serverPlayer, new SyncAkashicRecordPacket(data.getRecord(serverPlayer.getUUID())));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        int recordSlots = AkashicRecordSavedData.SLOTS_PER_PAGE;
        if (index < recordSlots) {
            if (!this.moveItemStackTo(sourceStack, recordSlots, recordSlots + 36, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!AkashicRecordHelper.isValidScroll(sourceStack, this.player)
                    || !this.moveItemStackTo(sourceStack, 0, recordSlots, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        return copy;
    }

    private class PagedScrollSlot extends Slot {
        private final int localIndex;

        PagedScrollSlot(Container container, int localIndex, int x, int y) {
            super(container, localIndex, x, y);
            this.localIndex = localIndex;
        }

        private int actualIndex() {
            return currentPage * AkashicRecordSavedData.SLOTS_PER_PAGE + localIndex;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AkashicRecordHelper.isValidScroll(stack, player);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public ItemStack getItem() {
            return this.container.getItem(actualIndex());
        }

        @Override
        public void set(ItemStack stack) {
            this.container.setItem(actualIndex(), stack);
            this.setChanged();
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            set(stack);
        }

        @Override
        public ItemStack remove(int amount) {
            return this.container.removeItem(actualIndex(), amount);
        }

        @Override
        public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
            int diff = newStack.getCount() - oldStack.getCount();
            if (diff > 0) {
                this.onQuickCraft(newStack, diff);
            }
        }
    }
}
