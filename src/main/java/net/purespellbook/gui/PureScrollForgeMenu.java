package net.purespellbook.gui;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.purespellbook.block.PureScrollForgeBlockEntity;
import net.purespellbook.registry.ModBlocks;
import net.purespellbook.registry.ModMenuTypes;

/**
 * Menu for the Pure Scroll Forge.
 * Only requires Ink in slot 0; paper (slot 1) and focus (slot 2) are visual-only dummy slots.
 * Ignores AbstractSpell.allowCrafting(), allowing treasure-only and Pure spells to be forged.
 */
public class PureScrollForgeMenu extends AbstractContainerMenu {
    public final PureScrollForgeBlockEntity blockEntity;
    private final Level level;
    private final Slot inkSlot;
    private final Slot blankScrollSlot;
    private final Slot focusSlot;
    private final Slot resultSlot;
    private AbstractSpell spellRecipeSelection = SpellRegistry.none();

    private static final int VANILLA_SLOT_COUNT = 36;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;

    public PureScrollForgeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public PureScrollForgeMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.PURE_SCROLL_FORGE_MENU.get(), containerId);
        checkContainerSize(inv, 4);
        this.blockEntity = (PureScrollForgeBlockEntity) entity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        ItemStackHandler handler = this.blockEntity.getItemHandler();

        this.inkSlot = new SlotItemHandler(handler, 0, 12, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof InkItem;
            }
        };

        // Visual-only slots (paper & focus)
        this.blankScrollSlot = new SlotItemHandler(handler, 1, 35, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return false;
            }
        };

        this.focusSlot = new SlotItemHandler(handler, 2, 58, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return false;
            }
        };

        this.resultSlot = new SlotItemHandler(handler, 3, 35, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                inkSlot.remove(1);
                setupResultSlot(spellRecipeSelection);
                super.onTake(player, stack);
            }
        };

        addSlot(inkSlot);
        addSlot(blankScrollSlot);
        addSlot(focusSlot);
        addSlot(resultSlot);
    }

    public void onSlotsChanged(int slot) {
        if (slot != 3) {
            setupResultSlot(this.spellRecipeSelection);
        }
    }

    /**
     * Computes the forged scroll level for a given spell and ink rarity.
     * Any ink at or above the spell's minimum rarity can craft it; single-rarity
     * or max-level-1 spells (e.g. Pocket Dimension, Pure spells) remain craftable
     * at level 1 even when higher-rarity ink such as Legendary Ink is used.
     */
    public static int getCraftableSpellLevel(AbstractSpell spell, SpellRarity inkRarity) {
        if (spell == null || inkRarity == null || spell.equals(SpellRegistry.none())) {
            return 0;
        }
        if (inkRarity.getValue() < spell.getMinRarity()) {
            return 0;
        }
        int maxLevel = Math.max(1, spell.getMaxLevel());
        if (maxLevel == 1 || inkRarity.getValue() == spell.getMinRarity()) {
            return 1;
        }
        int cappedRarityVal = Math.min(inkRarity.getValue(), spell.getMaxRarity());
        SpellRarity[] rarities = SpellRarity.values();
        SpellRarity cappedRarity = rarities[Mth.clamp(cappedRarityVal, 0, rarities.length - 1)];
        int level = spell.getMinLevelForRarity(cappedRarity);
        return Mth.clamp(level, 1, maxLevel);
    }

    private void setupResultSlot(AbstractSpell spell) {
        ItemStack inkStack = this.inkSlot.getItem();
        ItemStack result = ItemStack.EMPTY;

        if (!inkStack.isEmpty() && !spell.equals(SpellRegistry.none()) && inkStack.getItem() instanceof InkItem ink) {
            int level = getCraftableSpellLevel(spell, ink.getRarity());
            if (level > 0) {
                result = new ItemStack(ItemRegistry.SCROLL.get());
                result.setCount(1);
                ISpellContainer.createScrollContainer(spell, level, result);
            }
        }

        if (!ItemStack.matches(result, this.resultSlot.getItem())) {
            if (result.isEmpty()) {
                this.spellRecipeSelection = SpellRegistry.none();
            }
            this.resultSlot.set(result);
        }
    }

    public void setRecipeSpell(AbstractSpell spell) {
        this.spellRecipeSelection = spell;
        setupResultSlot(spell);
    }

    public AbstractSpell getRecipeSpell() {
        return this.spellRecipeSelection;
    }

    public Slot getInkSlot() {
        return inkSlot;
    }

    public Slot getResultSlot() {
        return resultSlot;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // Only move into inkSlot (index 36)
            if (!moveItemStackTo(sourceStack, 36, 37, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 40) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlot.container && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(net.minecraft.world.inventory.ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.PURE_SCROLL_FORGE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18 + 21, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18 + 21, 142));
        }
    }
}
