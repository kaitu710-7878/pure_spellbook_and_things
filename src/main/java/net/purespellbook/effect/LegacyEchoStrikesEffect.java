package net.purespellbook.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.purespellbook.registry.ModSpells;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * ISS 3.4.0.4-era Echoing Strikes effect, scaling with Pure spell power.
 */
public class LegacyEchoStrikesEffect extends MagicMobEffect {
    public LegacyEchoStrikesEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static float getDamageModifier(int effectAmplifier, @Nullable LivingEntity caster) {
        float power = caster == null ? 1.0F : ModSpells.LEGACY_ECHO_STRIKE.get().getEntityPowerMultiplier(caster);
        return (((effectAmplifier - 4) * power) + 5.0F) * 0.1F;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        IssIconHelper.initIssIcon(consumer, "echoing_strikes");
    }
}
