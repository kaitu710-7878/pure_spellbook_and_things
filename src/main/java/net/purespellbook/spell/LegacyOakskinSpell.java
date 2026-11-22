package net.purespellbook.spell;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.particles.OakskinParticlesPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.purespellbook.effect.LegacyOakskinEffect;
import net.purespellbook.registry.ModMobEffects;

import java.util.List;
import java.util.Optional;

/**
 * Pure-school recreation of ISS 3.4.0.4's Oakskin spell.
 * Level 1 matches the original spell's max level (8), and addon tiers scale identically.
 */
public class LegacyOakskinSpell extends AbstractPureSpell {
    private static final int ORIGINAL_MAX_LEVEL = 8;
    private static final ResourceLocation ISS_ICON =
            new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/spell_icons/oakskin.png");

    public LegacyOakskinSpell() {
        super("legacy_oakskin", 35.0D, 50, 0);
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 41;
        this.spellPowerPerLevel = 3;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.OAKSKIN_CAST.get());
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage_reduction",
                        Utils.stringTruncation(getPercentDamage(spellLevel), 0)),
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1))
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        int effectiveLevel = ORIGINAL_MAX_LEVEL + (spellLevel - 1);
        entity.addEffect(new MobEffectInstance(
                ModMobEffects.LEGACY_OAKSKIN.get(),
                (int) (getSpellPower(spellLevel, entity) * 20),
                effectiveLevel - 1,
                false, false, true));
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new OakskinParticlesPacket(entity.position()));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    private float getPercentDamage(int spellLevel) {
        int effectiveLevel = ORIGINAL_MAX_LEVEL + (spellLevel - 1);
        return LegacyOakskinEffect.getReductionAmount(effectiveLevel) * 100.0F;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
