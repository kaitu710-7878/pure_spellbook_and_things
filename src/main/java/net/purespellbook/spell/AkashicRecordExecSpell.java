package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.purespellbook.akashic.AkashicCastContext;
import net.purespellbook.akashic.AkashicRecordHelper;

import java.util.List;
import java.util.Optional;

/**
 * Akashic Record: Execution 1..4 — simultaneously invokes all stored scrolls on the given page.
 * Mana cost (250 + sum), cast time (sum), and cooldown (sum) are computed dynamically from
 * the stored scroll NBT data. Max level is permanently locked to 1.
 */
public class AkashicRecordExecSpell extends AbstractPureSpell {
    public static final int BASE_MANA = 250;
    private final int pageIndex;

    public AkashicRecordExecSpell(int pageNumber) {
        super("akashic_record_exec_" + pageNumber, 0.0D, BASE_MANA, 0);
        this.pageIndex = pageNumber - 1;
    }

    public int getPageIndex() {
        return pageIndex;
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
    public int getManaCost(int spellLevel) {
        Player player = AkashicCastContext.getPlayer();
        if (player == null) {
            player = AkashicRecordHelper.resolveClientPlayerFallback();
        }
        return AkashicRecordHelper.getCombinedManaCost(player, pageIndex, BASE_MANA);
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, LivingEntity entity) {
        return AkashicRecordHelper.getCombinedCastTime(entity, pageIndex);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        List<SpellData> stored = AkashicRecordHelper.getPageSpells(caster, pageIndex);
        int castTicks = AkashicRecordHelper.getCombinedCastTime(caster, pageIndex);
        return List.of(
                Component.translatable("ui.pure_spellbook_and_things.akashic_exec_page", pageIndex + 1, stored.size()),
                Component.translatable("ui.pure_spellbook_and_things.akashic_exec_cast_time",
                        Utils.timeFromTicks(castTicks, 1))
        );
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ENCHANTMENT_TABLE_USE);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BEACON_ACTIVATE);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            List<SpellData> storedSpells = AkashicRecordHelper.getPageSpells(entity, pageIndex);
            for (SpellData data : storedSpells) {
                AbstractSpell storedSpell = data.getSpell();
                int storedLevel = data.getLevel();
                if (storedSpell instanceof AkashicRecordExecSpell || storedSpell instanceof AkashicRecordAccessSpell) {
                    continue;
                }
                playerMagicData.resetAdditionalCastData();
                if (storedSpell.checkPreCastConditions(level, storedLevel, entity, playerMagicData)) {
                    storedSpell.onCast(level, storedLevel, entity, castSource, playerMagicData);
                }
            }
            playerMagicData.resetAdditionalCastData();
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }
}
