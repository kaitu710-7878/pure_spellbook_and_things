package net.purespellbook.effect;

import dev.xkmc.l2library.base.effects.EffectUtil;
import dev.xkmc.l2library.base.effects.api.ForceEffect;
import dev.xkmc.l2library.base.effects.api.InherentEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Purification effect: continuously clears debuffs using the same logic as L2Complements' CleanseEffect.
 * Requires L2Library (InherentEffect, ForceEffect, EffectUtil).
 */
public class PurificationEffect extends InherentEffect implements ForceEffect {
    private static final TagKey<MobEffect> L2C_SKILL_EFFECT =
            TagKey.create(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), new ResourceLocation("l2complements", "skill_effect"));
    private static int recursive = 0;

    public PurificationEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean isProtectedEffect(MobEffectInstance instance) {
        MobEffect effect = instance.getEffect();
        if (effect instanceof InherentEffect || effect instanceof ForceEffect) {
            return true;
        }
        if (EffectUtil.getReason() == EffectUtil.AddReason.SKILL) {
            return true;
        }
        if (effect.isBeneficial()) {
            return true;
        }
        var tags = ForgeRegistries.MOB_EFFECTS.tags();
        if (tags != null && tags.getTag(L2C_SKILL_EFFECT).contains(effect)) {
            return true;
        }
        return false;
    }

    public static void clearOnEntity(LivingEntity entity) {
        recursive++;
        try {
            List<MobEffectInstance> list = new ArrayList<>(entity.getActiveEffects());
            for (MobEffectInstance instance : list) {
                if (isProtectedEffect(instance)) {
                    continue;
                }
                MobEffect effect = instance.getEffect();
                if (recursive <= 1) {
                    entity.removeEffect(effect);
                }
                if (entity.hasEffect(effect)) {
                    entity.getActiveEffectsMap().remove(effect);
                }
            }
        } finally {
            recursive--;
        }
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.addAttributeModifiers(entity, map, amplifier);
        clearOnEntity(entity);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        clearOnEntity(entity);
    }
}
