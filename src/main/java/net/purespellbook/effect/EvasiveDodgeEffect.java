package net.purespellbook.effect;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.datagen.DamageTypeTagGenerator;
import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.purespellbook.registry.ModMobEffects;

/**
 * Evasion-equivalent buff that negates incoming hits using the same damage-filtering logic
 * as Iron's Spells 'n Spellbooks' EvasionEffect, without triggering any teleportation.
 */
public class EvasiveDodgeEffect extends CustomDescriptionMobEffect {
    public EvasiveDodgeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public Component getDescriptionLine(MobEffectInstance instance) {
        int hits = instance.getAmplifier() + 1;
        return Component.translatable("tooltip.irons_spellbooks.evasion_description", hits)
                .withStyle(ChatFormatting.BLUE);
    }

    /**
     * Attempts to dodge an incoming hit. Returns true if the attack/projectile should be negated.
     */
    public static boolean doEffect(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide
                || source.is(DamageTypeTags.IS_FALL)
                || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || source.is(DamageTypeTagGenerator.BYPASS_EVASION)) {
            return false;
        }

        MobEffectInstance current = entity.getEffect(ModMobEffects.EVASIVE_DODGE.get());
        if (current == null) {
            return false;
        }

        int remainingDuration = current.getDuration();
        int remainingAmplifier = current.getAmplifier();
        boolean ambient = current.isAmbient();
        boolean visible = current.isVisible();
        boolean showIcon = current.showIcon();

        entity.removeEffect(ModMobEffects.EVASIVE_DODGE.get());
        if (remainingAmplifier > 0 && remainingDuration > 0) {
            entity.addEffect(new MobEffectInstance(
                    ModMobEffects.EVASIVE_DODGE.get(),
                    remainingDuration,
                    remainingAmplifier - 1,
                    ambient,
                    visible,
                    showIcon
            ));
        }

        var center = entity.getBoundingBox().getCenter();
        MagicManager.spawnParticles(
                entity.level(),
                ParticleTypes.PORTAL,
                center.x, center.y, center.z,
                24,
                entity.getBbWidth() / 4.0D,
                entity.getBbHeight() / 5.0D,
                entity.getBbWidth() / 4.0D,
                0.035D,
                false
        );
        return true;
    }
}
