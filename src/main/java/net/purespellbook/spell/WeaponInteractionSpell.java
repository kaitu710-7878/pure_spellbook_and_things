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

public class WeaponInteractionSpell extends AbstractPureSpell {
    public WeaponInteractionSpell() {
        super("weapon_interaction", 72.0D, 108, 0);
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
        return Math.max(20, (int) (2400.0F * getEntityPowerMultiplier(caster)));
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getDurationTicks(caster), 1)),
                Component.translatable("ui.pure_spellbook_and_things.weapon_interaction_desc")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModMobEffects.WEAPON_INTERACTION.get(),
                getDurationTicks(entity), 0, false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
