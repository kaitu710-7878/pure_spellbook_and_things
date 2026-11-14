package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.purespellbook.registry.ModMobEffects;

import java.util.List;

public class SustainedPurificationSpell extends AbstractPureSpell {
    private static final int DURATION_TICKS = 300; // 15 seconds

    public SustainedPurificationSpell() {
        super("sustained_purification", 180.0D, 300, 180);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public int getManaCost(int level) {
        return scaledManaCost(level);
    }

    @Override
    public int getCastTime(int level) {
        return scaledCastTime(level);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(DURATION_TICKS, 1)),
                Component.translatable("ui.pure_spellbook_and_things.purification_desc")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModMobEffects.PURIFICATION.get(),
                DURATION_TICKS, 0, false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CAST_KNEELING_PRAYER;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.SELF_CAST_TWO_HANDS;
    }
}
