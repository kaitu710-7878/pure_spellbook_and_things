package net.purespellbook.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Simple non-scaling Pure buff effect whose logic is handled by events.
 */
public class SimplePureBuffEffect extends MobEffect {
    public SimplePureBuffEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
