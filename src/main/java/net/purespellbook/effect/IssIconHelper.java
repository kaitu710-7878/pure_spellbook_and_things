package net.purespellbook.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

/**
 * Provides client-side MobEffect icon rendering that references Iron's Spells 'n Spellbooks'
 * built-in effect textures directly without duplicating texture assets.
 */
public final class IssIconHelper {
    private IssIconHelper() {
    }

    public static void initIssIcon(Consumer<IClientMobEffectExtensions> consumer, String issEffectIconName) {
        final ResourceLocation icon = new ResourceLocation(
                IronsSpellbooks.MODID, "textures/mob_effect/" + issEffectIconName + ".png");
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen,
                                               GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                guiGraphics.blit(icon, x + 6, y + 7, 0, 0, 18, 18, 18, 18);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics,
                                         int x, int y, float z, float alpha) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                guiGraphics.blit(icon, x + 3, y + 3, 0, 0, 18, 18, 18, 18);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                return true;
            }
        });
    }
}
