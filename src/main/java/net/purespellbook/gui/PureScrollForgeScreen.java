package net.purespellbook.gui;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeScreen;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
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
 * Screen for the Pure Scroll Forge matching Iron's Spells 'n Spellbooks' Scroll Forge UI
 * and hover tooltips while allowing any enabled spell to be crafted with ink only.
 */
public class PureScrollForgeScreen extends AbstractContainerScreen<PureScrollForgeMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/scroll_forge.png");

    private static final int SPELL_LIST_X = 89;
    private static final int SPELL_LIST_Y = 15;
    private static final int SPELL_CARD_WIDTH = 108;
    private static final int SPELL_CARD_HEIGHT = 19;
    private static final int VISIBLE_ROWS = 3;
    private static final int MAX_TEXT_WIDTH = 88;

    private static final int SCROLL_BAR_X = 199;
    private static final int SCROLL_BAR_Y = 15;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 56;

    private final List<SpellEntry> availableSpells = new ArrayList<>();
    private ItemStack lastInk = ItemStack.EMPTY;
    private AbstractSpell selectedSpell = SpellRegistry.none();
    private int scrollOffset = 0;
    private boolean isScrollbarHeld = false;

    private record SpellEntry(AbstractSpell spell, boolean craftable) {}

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
        renderHoveredSpellTooltip(guiGraphics, mouseX, mouseY);
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

        renderSpellList(guiGraphics);
    }

    private void generateSpellList() {
        availableSpells.clear();
        Item inkItem = this.menu.getInkSlot().getItem().getItem();
        SpellRarity inkRarity = inkItem instanceof InkItem ink ? ink.getRarity() : null;

        List<AbstractSpell> spells = SpellRegistry.getEnabledSpells().stream()
                .filter(s -> !s.equals(SpellRegistry.none()))
                .toList();

        for (AbstractSpell spell : spells) {
            boolean craftable = PureScrollForgeMenu.getCraftableSpellLevel(spell, inkRarity) > 0;
            availableSpells.add(new SpellEntry(spell, craftable));
        }

        availableSpells.sort(Comparator
                .comparing((SpellEntry e) -> !e.craftable())
                .thenComparing(e -> e.spell().getSchoolType().getId().toString())
                .thenComparingInt(e -> e.spell().getMinRarity())
                .thenComparing(e -> e.spell().getSpellId()));

        if (!this.selectedSpell.equals(SpellRegistry.none())
                && PureScrollForgeMenu.getCraftableSpellLevel(this.selectedSpell, inkRarity) <= 0) {
            this.selectedSpell = SpellRegistry.none();
        }

        int maxScroll = Math.max(0, availableSpells.size() - VISIBLE_ROWS);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
    }

    private void renderSpellList(GuiGraphics guiGraphics) {
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
            boolean selected = spell.equals(this.selectedSpell);

            // Card background from vanilla Scroll Forge texture (u=0, v=166/185/204, 108x19)
            int vOffset;
            if (!entry.craftable()) {
                vOffset = 185;
            } else if (selected) {
                vOffset = 204;
            } else {
                vOffset = 166;
            }
            guiGraphics.blit(TEXTURE, startX, cardY, 0, vOffset, SPELL_CARD_WIDTH, SPELL_CARD_HEIGHT);

            // Spell icon on the right side (16x16)
            ResourceLocation icon = entry.craftable()
                    ? spell.getSpellIconResource()
                    : SpellRegistry.none().getSpellIconResource();
            guiGraphics.blit(icon, startX + SPELL_CARD_WIDTH - 18, cardY + 1, 0, 0, 16, 16, 16, 16);

            // Spell name on the left side (runic font when ink is missing/insufficient)
            MutableComponent displayName = spell.getDisplayName(this.minecraft != null ? this.minecraft.player : null);
            Component styledName = entry.craftable()
                    ? displayName
                    : displayName.copy().withStyle(Style.EMPTY.withFont(ScrollForgeScreen.RUNIC_FONT));
            FormattedText trimmed = trimText(this.font, styledName, MAX_TEXT_WIDTH);
            guiGraphics.drawWordWrap(this.font, trimmed, startX + 2, cardY + 3, MAX_TEXT_WIDTH, 0xFFFFFF);
        }
    }

    private void renderHoveredSpellTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
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
                List<FormattedCharSequence> tooltip;
                if (!entry.craftable()) {
                    tooltip = List.of(FormattedCharSequence.forward(
                            Component.translatable("ui.irons_spellbooks.ink_rarity_error").getString(),
                            Style.EMPTY));
                } else {
                    tooltip = TooltipsUtils.createSpellDescriptionTooltip(entry.spell(), this.font);
                }
                guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
                return;
            }
        }
    }

    private static FormattedText trimText(Font font, Component text, int maxWidth) {
        List<FormattedText> lines = font.getSplitter().splitLines(text, maxWidth, text.getStyle());
        if (lines.isEmpty()) {
            return FormattedText.EMPTY;
        }
        FormattedText first = lines.get(0);
        if (first.getString().length() < text.getString().length()) {
            return FormattedText.composite(first, FormattedText.of("..."));
        }
        return first;
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
