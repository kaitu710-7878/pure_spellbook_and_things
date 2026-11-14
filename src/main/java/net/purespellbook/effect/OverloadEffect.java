package net.purespellbook.effect;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * Overload neutral effect: reduces Max Mana by 10% * level.
 * Designed as an InherentEffect so it is not removed by purification or cleanse.
 */
public class OverloadEffect extends InherentEffect {
    public OverloadEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        double value = modifier.getAmount() * (amplifier + 1);
        return Math.max(-0.99D, value);
    }
}
