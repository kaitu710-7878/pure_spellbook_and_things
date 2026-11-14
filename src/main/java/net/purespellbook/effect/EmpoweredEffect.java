package net.purespellbook.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Empowered effect: +40% Spell Power, +85% Attack Damage, and prevents spell cast interruption.
 * Amplifier has no effect on attribute values (even if applied via commands at higher levels).
 */
public class EmpoweredEffect extends MobEffect {
    public EmpoweredEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount();
    }
}
