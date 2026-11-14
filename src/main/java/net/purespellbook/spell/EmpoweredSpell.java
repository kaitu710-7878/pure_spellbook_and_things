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

public class EmpoweredSpell extends AbstractPureSpell {
    public EmpoweredSpell() {
        super("empowered", 48.0D, 120, 0);
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public boolean hasCooldownLevelScaling() {
        return true;
    }

    @Override
    public int getManaCost(int level) {
        return scaledManaCost(level);
    }

    public int getDurationTicks(LivingEntity caster) {
        return Math.max(20, (int) (600.0F * getEntityPowerMultiplier(caster)));
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getDurationTicks(caster), 1)),
                Component.translatable("attribute.modifier.plus.1", "40",
                        Component.translatable("attribute.irons_spellbooks.spell_power")),
                Component.translatable("attribute.modifier.plus.1", "85",
                        Component.translatable("attribute.name.generic.attack_damage")),
                Component.translatable("ui.pure_spellbook_and_things.uninterruptible_casting")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModMobEffects.EMPOWERED.get(),
                getDurationTicks(entity), 0, false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
