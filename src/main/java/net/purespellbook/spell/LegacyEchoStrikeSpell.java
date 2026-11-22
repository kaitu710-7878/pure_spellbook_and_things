package net.purespellbook.spell;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.purespellbook.effect.LegacyEchoStrikesEffect;
import net.purespellbook.registry.ModMobEffects;

import java.util.List;

/**
 * Pure-school recreation of ISS 3.4.0.4's Echoing Strikes spell.
 * Level 1 matches the original spell's max level (5), and addon tiers scale identically.
 */
public class LegacyEchoStrikeSpell extends AbstractPureSpell {
    private static final int ORIGINAL_MAX_LEVEL = 5;
    private static final ResourceLocation ISS_ICON =
            new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/spell_icons/echoing_strikes.png");

    public LegacyEchoStrikeSpell() {
        super("legacy_echo_strike", 60.0D, 90, 0);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 40;
        this.spellPowerPerLevel = 5;
    }

    @Override
    public ResourceLocation getCustomSpellIconResource() {
        return ISS_ICON;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.percent_damage",
                        Utils.stringTruncation(getPercentDamage(spellLevel, caster), 0)),
                Component.translatable("ui.irons_spellbooks.radius", 3),
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1))
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(
                ModMobEffects.LEGACY_ECHOING_STRIKES.get(),
                (int) (getSpellPower(spellLevel, entity) * 20),
                getAmplifierForLevel(spellLevel, entity),
                false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getPercentDamage(int spellLevel, LivingEntity entity) {
        return LegacyEchoStrikesEffect.getDamageModifier(getAmplifierForLevel(spellLevel, entity), entity) * 100.0F;
    }

    private int getAmplifierForLevel(int spellLevel, LivingEntity caster) {
        int effectiveLevel = ORIGINAL_MAX_LEVEL + (spellLevel - 1);
        return 4 + (int) ((effectiveLevel - 1) * getEntityPowerMultiplier(caster));
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
