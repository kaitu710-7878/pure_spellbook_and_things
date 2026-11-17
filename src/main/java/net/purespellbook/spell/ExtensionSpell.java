package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Extension — 200 mana, 2s cast, 120s cooldown.
 * Extends all active beneficial effects on the caster by 1m30s (1800 ticks)
 * at their current amplifier. Scales with spell power and extra spell levels.
 */
public class ExtensionSpell extends AbstractPureSpell {
    private static final int BASE_EXTENSION_TICKS = 1800; // 1m 30s

    public ExtensionSpell() {
        super("extension", 120.0D, 200, 40);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public int getExtensionTicks(int spellLevel, LivingEntity caster) {
        double levelMultiplier = 1.0D + 0.5D * Math.max(0, spellLevel - 1);
        return Math.max(20, (int) Math.round(BASE_EXTENSION_TICKS * levelMultiplier * getEntityPowerMultiplier(caster)));
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.pure_spellbook_and_things.extension_duration",
                        Utils.timeFromTicks(getExtensionTicks(spellLevel, caster), 1))
        );
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EXPERIENCE_ORB_PICKUP);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.AMETHYST_BLOCK_CHIME);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            int addTicks = getExtensionTicks(spellLevel, entity);
            List<MobEffectInstance> activeCopy = new ArrayList<>(entity.getActiveEffects());
            for (MobEffectInstance effect : activeCopy) {
                if (effect.getEffect().getCategory() == MobEffectCategory.BENEFICIAL && !effect.isInfiniteDuration()) {
                    int newDuration = (int) Math.min(Integer.MAX_VALUE / 2L, (long) effect.getDuration() + addTicks);
                    entity.addEffect(new MobEffectInstance(
                            effect.getEffect(),
                            newDuration,
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.isVisible(),
                            effect.showIcon()
                    ));
                }
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }
}
