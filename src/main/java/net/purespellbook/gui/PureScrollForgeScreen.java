package net.purespellbook.gui;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.purespellbook.network.ModNetwork;
import net.purespellbook.network.SelectPureSpellPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Screen for the Pure Scroll Forge reusing Iron's Spells 'n Spellbooks' Scroll Forge UI texture.
 * Displays all enabled spells regardless of allowCrafting() and requires only matching-rarity ink.
 */
public class PureScrollForgeScreen extends AbstractContainerScreen<PureScrollForgeMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/scroll_forge.png");

    private static final int SPELL_LIST_X = 89;
    private static final int SPELL_LIST_Y = 15;
    private static final int SPELL_CARD_WIDTH = 108;
    private static final int SPELL_CARD_HEIGHT = 18;
    private static final int VISIBLE_ROWS = 3;

    private static final int SCROLL_BAR_X = 199;
    private static final int SCROLL_BAR_Y = 15;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 56;

    private final List<SpellEntry> availableSpells = new ArrayList<>();
    private ItemStack lastInk = ItemStack.EMPTY;
    private AbstractSpell selectedSpell = SpellRegistry.none();
    private int scrollOffset = 0;
    private boolean isScrollbarHeld = false;

    private record SpellEntry(AbstractSpell spell, boolean craftable, SpellRarity targetRarity) {}

    public PureScrollForgeScreen(PureScrollForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 218;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        generateSpellList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        ItemStack currentInk = this.menu.getInkSlot().getItem();
        if (!ItemStack.matches(currentInk, lastInk)) {
            lastInk = currentInk.copy();
            generateSpellList();
        }

        // Scrollbar handle
        int maxScroll = Math.max(0, availableSpells.size() - VISIBLE_ROWS);
        float scrollProgress = maxScroll > 0 ? (float) scrollOffset / maxScroll : 0.0F;
        int handleY = this.topPos + SCROLL_BAR_Y + (int) (scrollProgress * (SCROLL_BAR_HEIGHT - 15));
        guiGraphics.blit(TEXTURE, this.leftPos + SCROLL_BAR_X, handleY, 218 + (maxScroll > 0 ? 0 : 12), 0, 12, 15);

        renderSpellList(guiGraphics, mouseX, mouseY);
    }

    private void generateSpellList() {
        availableSpells.clear();
        Item inkItem = this.menu.getInkSlot().getItem().getItem();
        SpellRarity inkRarity = inkItem instanceof InkItem ink ? ink.getRarity() : null;

        List<AbstractSpell> spells = SpellRegistry.getEnabledSpells().stream()
                .filter(s -> !s.equals(SpellRegistry.none()))
                .sorted(Comparator.comparing((AbstractSpell s) -> s.getSchoolType().getId().toString())
                        .thenComparing(AbstractSpell::getMinRarity)
                        .thenComparing(AbstractSpell::getSpellId))
                .toList();

        for (AbstractSpell spell : spells) {
            boolean craftable = inkRarity != null && spell.getMinLevelForRarity(inkRarity) > 0;
            SpellRarity displayRarity = inkRarity != null ? inkRarity : SpellRarity.values()[Math.min(spell.getMinRarity(), SpellRarity.values().length - 1)];
            availableSpells.add(new SpellEntry(spell, craftable, displayRarity));
        }

        int maxScroll = Math.max(0, availableSpells.size() - VISIBLE_ROWS);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
    }

    private void renderSpellList(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int startX = this.leftPos + SPELL_LIST_X;
        int startY = this.topPos + SPELL_LIST_Y;

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx >= availableSpells.size()) {
                break;
            }
            SpellEntry entry = availableSpells.get(idx);
            AbstractSpell spell = entry.spell();
            int cardY = startY + i * SPELL_CARD_HEIGHT;

            boolean hovered = mouseX >= startX && mouseX < startX + SPELL_CARD_WIDTH
                    && mouseY >= cardY && mouseY < cardY + SPELL_CARD_HEIGHT;
            boolean selected = spell.equals(this.selectedSpell);

            int bgColor;
            if (!entry.craftable()) {
                bgColor = 0xAA262626;
            } else if (selected) {
                bgColor = 0xAA3B5B7A;
            } else if (hovered) {
                bgColor = 0xAA4A4A5E;
            } else {
                bgColor = 0xAA30303E;
            }
            guiGraphics.fill(startX, cardY, startX + SPELL_CARD_WIDTH, cardY + SPELL_CARD_HEIGHT - 1, bgColor);

            // Spell icon (16x16)
            ResourceLocation icon = spell.getSpellIconResource();
            guiGraphics.blit(icon, startX + 1, cardY + 1, 0, 0, 16, 16, 16, 16);

            // Spell name
            Component name = spell.getDisplayName(null);
            int textColor = entry.craftable() ? (selected ? 0xFFFFAA : 0xFFFFFF) : 0x777777;
            String trimmed = this.font.plainSubstrByWidth(name.getString(), SPELL_CARD_WIDTH - 22);
            guiGraphics.drawString(this.font, trimmed, startX + 20, cardY + 5, textColor, true);

            if (hovered) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(name.copy().withStyle(spell.getSchoolType().getDisplayName().getStyle()));
                tooltip.add(spell.getSchoolType().getDisplayName());
                if (!entry.craftable()) {
                    tooltip.add(Component.translatable("ui.pure_spellbook_and_things.requires_ink_rarity")
                            .withStyle(ChatFormatting.RED));
                }
                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, availableSpells.size() - VISIBLE_ROWS);
        scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(delta), 0, maxScroll);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = this.leftPos + SPELL_LIST_X;
        int startY = this.topPos + SPELL_LIST_Y;

        for (int i = 0; i < VISIBLE_ROWS; i++) {
            int idx = scrollOffset + i;
            if (idx >= availableSpells.size()) {
                break;
            }
            int cardY = startY + i * SPELL_CARD_HEIGHT;
            if (mouseX >= startX && mouseX < startX + SPELL_CARD_WIDTH
                    && mouseY >= cardY && mouseY < cardY + SPELL_CARD_HEIGHT) {
                SpellEntry entry = availableSpells.get(idx);
                if (entry.craftable()) {
                    this.selectedSpell = entry.spell();
                    ModNetwork.sendToServer(new SelectPureSpellPacket(this.menu.blockEntity.getBlockPos(), this.selectedSpell.getSpellId()));
                    return true;
                }
            }
        }

        int barX = this.leftPos + SCROLL_BAR_X;
        int barY = this.topPos + SCROLL_BAR_Y;
        if (mouseX >= barX && mouseX < barX + SCROLL_BAR_WIDTH
                && mouseY >= barY && mouseY < barY + SCROLL_BAR_HEIGHT) {
            isScrollbarHeld = true;
            updateScrollFromMouse(mouseY);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isScrollbarHeld = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrollbarHeld) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void updateScrollFromMouse(double mouseY) {
        int maxScroll = Math.max(0, availableSpells.size() - VISIBLE_ROWS);
        if (maxScroll <= 0) {
            scrollOffset = 0;
            return;
        }
        double rel = (mouseY - (this.topPos + SCROLL_BAR_Y)) / (double) (SCROLL_BAR_HEIGHT - 15);
        scrollOffset = Mth.clamp((int) Math.round(rel * maxScroll), 0, maxScroll);
    }
}
