package net.purespellbook.spell;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastData;
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
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Akashic Record: Execution 1..4 — simultaneously invokes all stored scrolls on the given page.
 * Mana cost (250 + sum), cast time (sum), and cooldown (sum) are computed dynamically from
 * the stored scroll NBT data. Max level is permanently locked to 1.
 * Snapshots target/pre-cast data at cast start so targeting spells remain reliable at cast finish.
 */
public class AkashicRecordExecSpell extends AbstractPureSpell {
    private static final Logger LOGGER = LogUtils.getLogger();
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
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        List<SpellData> storedSpells = AkashicRecordHelper.getPageSpells(entity, pageIndex);
        List<ICastData> snapshots = new ArrayList<>(storedSpells.size());
        for (SpellData data : storedSpells) {
            AbstractSpell storedSpell = data.getSpell();
            int storedLevel = data.getLevel();
            if (isBlockedSpell(storedSpell, storedLevel, entity)) {
                snapshots.add(null);
                continue;
            }
            playerMagicData.resetAdditionalCastData();
            ICastData captured = null;
            try {
                if (storedSpell.checkPreCastConditions(level, storedLevel, entity, playerMagicData)) {
                    captured = playerMagicData.getAdditionalCastData();
                }
            } catch (Throwable t) {
                LOGGER.warn("Akashic Record pre-cast check failed for spell {}: {}", storedSpell.getSpellId(), t.getMessage());
            }
            snapshots.add(captured);
        }
        playerMagicData.setAdditionalCastData(new AkashicPreCastData(snapshots));
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            ICastData rawPreCast = playerMagicData.getAdditionalCastData();
            List<ICastData> snapshots = rawPreCast instanceof AkashicPreCastData data ? data.snapshots() : List.of();
            List<SpellData> storedSpells = AkashicRecordHelper.getPageSpells(entity, pageIndex);

            for (int i = 0; i < storedSpells.size(); i++) {
                SpellData data = storedSpells.get(i);
                AbstractSpell storedSpell = data.getSpell();
                int storedLevel = data.getLevel();
                if (isBlockedSpell(storedSpell, storedLevel, entity)) {
                    continue;
                }

                try {
                    playerMagicData.resetAdditionalCastData();
                    boolean ready = storedSpell.checkPreCastConditions(level, storedLevel, entity, playerMagicData);
                    ICastData fallback = i < snapshots.size() ? snapshots.get(i) : null;
                    if ((!ready || playerMagicData.getAdditionalCastData() == null) && fallback != null) {
                        playerMagicData.setAdditionalCastData(fallback);
                        ready = true;
                    }
                    if (ready) {
                        storedSpell.onCast(level, storedLevel, entity, castSource, playerMagicData);
                    }
                } catch (Throwable t) {
                    LOGGER.error("Akashic Record failed while executing stored spell {}", storedSpell.getSpellId(), t);
                }
            }
            playerMagicData.resetAdditionalCastData();
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private static boolean isBlockedSpell(AbstractSpell spell, int spellLevel, LivingEntity entity) {
        return spell instanceof AkashicRecordExecSpell
                || spell instanceof AkashicRecordAccessSpell
                || AkashicRecordHelper.isRecastSpell(spell, spellLevel, entity);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }

    private record AkashicPreCastData(List<ICastData> snapshots) implements ICastData {
        @Override
        public void reset() {
            for (ICastData data : snapshots) {
                if (data != null) {
                    data.reset();
                }
            }
        }
    }
}
