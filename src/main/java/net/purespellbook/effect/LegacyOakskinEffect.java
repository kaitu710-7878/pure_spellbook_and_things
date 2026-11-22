package net.purespellbook.effect;

import io.redspace.ironsspellbooks.effect.CustomDescriptionMobEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Consumer;

/**
 * ISS 3.4.0.4-era Oakskin effect (5% damage reduction per level + 10% base, no slowness).
 */
public class LegacyOakskinEffect extends CustomDescriptionMobEffect {
    public static final float REDUCTION_PER_LEVEL = 0.05F;
    public static final float BASE_REDUCTION = 0.10F;

    public LegacyOakskinEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public Component getDescriptionLine(MobEffectInstance instance) {
        int level = instance.getAmplifier() + 1;
        float reductionAmount = getReductionAmount(level);
        return Component.translatable("tooltip.irons_spellbooks.oakskin_description", (int) (reductionAmount * 100))
                .withStyle(ChatFormatting.BLUE);
    }

    public static float getReductionAmount(int level) {
        return BASE_REDUCTION + REDUCTION_PER_LEVEL * level;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        IssIconHelper.initIssIcon(consumer, "oakskin");
    }
}
