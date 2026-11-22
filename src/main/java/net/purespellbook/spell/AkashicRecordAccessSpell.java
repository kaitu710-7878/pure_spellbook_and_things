package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.purespellbook.gui.AkashicRecordMenu;

import java.util.List;
import java.util.Optional;

/**
 * Akashic Record: Access — 400 mana, 5s cast, no cooldown.
 * Opens the player's ender-chest-style 4-page (6x3) Akashic Record scroll storage.
 * Max level is permanently locked to 1.
 */
public class AkashicRecordAccessSpell extends AbstractPureSpell {
    public AkashicRecordAccessSpell() {
        super("akashic_record_access", 0.0D, 400, 100);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.pure_spellbook_and_things.akashic_access_desc"));
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ENCHANTMENT_TABLE_USE);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BOOK_PAGE_TURN);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
            if (castSource == CastSource.SCROLL) {
                // Opening a container menu interrupts/cancels the active cast before MagicManager consumes the scroll.
                Scroll.attemptRemoveScrollAfterCast(serverPlayer);
            }
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("ui.pure_spellbook_and_things.akashic_record_title");
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
                    return new AkashicRecordMenu(containerId, playerInv, serverPlayer);
                }
            });
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }
}
