package net.purespellbook.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.purespellbook.akashic.AkashicRecordSavedData;
import net.purespellbook.network.ModNetwork;
import net.purespellbook.network.SwitchAkashicPagePacket;

/**
 * Vanilla-styled 4-page 6x3 container screen for the Akashic Record.
 */
public class AkashicRecordScreen extends AbstractContainerScreen<AkashicRecordMenu> {
    private static final ResourceLocation VANILLA_CHEST_TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private final Button[] pageButtons = new Button[AkashicRecordSavedData.PAGE_COUNT];

    public AkashicRecordScreen(AkashicRecordMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();
        for (int i = 0; i < AkashicRecordSavedData.PAGE_COUNT; i++) {
            final int pageIdx = i;
            pageButtons[i] = this.addRenderableWidget(
                    Button.builder(Component.literal(String.valueOf(i + 1)), btn -> {
                        this.menu.setPage(pageIdx);
                        ModNetwork.sendToServer(new SwitchAkashicPagePacket(pageIdx));
                    })
                    .bounds(this.leftPos - 24, this.topPos + 14 + i * 22, 20, 20)
                    .build()
            );
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int current = this.menu.getPage();
        for (int i = 0; i < pageButtons.length; i++) {
            if (pageButtons[i] != null) {
                pageButtons[i].active = (i != current);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Top border & header from vanilla generic_54
        guiGraphics.blit(VANILLA_CHEST_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, 17);
        // Side borders for 3 rows (54px)
        guiGraphics.blit(VANILLA_CHEST_TEXTURE, this.leftPos, this.topPos + 17, 0, 17, 7, 54);
        guiGraphics.blit(VANILLA_CHEST_TEXTURE, this.leftPos + 169, this.topPos + 17, 169, 17, 7, 54);
        // Vanilla chest panel background (#C6C6C6)
        guiGraphics.fill(this.leftPos + 7, this.topPos + 17, this.leftPos + 169, this.topPos + 71, 0xFFC6C6C6);

        // 6x3 vanilla slot frames
        for (int row = 0; row < AkashicRecordMenu.ROWS; row++) {
            for (int col = 0; col < AkashicRecordMenu.COLS; col++) {
                int sx = this.leftPos + AkashicRecordMenu.SLOT_START_X - 1 + col * 18;
                int sy = this.topPos + AkashicRecordMenu.SLOT_START_Y - 1 + row * 18;
                guiGraphics.blit(VANILLA_CHEST_TEXTURE, sx, sy, 7, 17, 18, 18);
            }
        }

        // Player inventory & hotbar from vanilla generic_54
        guiGraphics.blit(VANILLA_CHEST_TEXTURE, this.leftPos, this.topPos + 70, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component pageTitle = Component.translatable("ui.pure_spellbook_and_things.akashic_record_page_title",
                this.title, this.menu.getPage() + 1);
        guiGraphics.drawString(this.font, pageTitle, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
