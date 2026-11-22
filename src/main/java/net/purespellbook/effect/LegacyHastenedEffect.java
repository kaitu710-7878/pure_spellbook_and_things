package net.purespellbook.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

/**
 * ISS 3.4.0.4-era Hastened effect (+10% movement speed, attack speed, mana regen, cast time reduction per level).
 */
public class LegacyHastenedEffect extends MagicMobEffect {
    public LegacyHastenedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        IssIconHelper.initIssIcon(consumer, "hastened");
    }
}
