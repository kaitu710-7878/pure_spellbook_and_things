package net.purespellbook.registry;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.effect.EmpoweredEffect;
import net.purespellbook.effect.EvasiveDodgeEffect;
import net.purespellbook.effect.LegacyEchoStrikesEffect;
import net.purespellbook.effect.LegacyHastenedEffect;
import net.purespellbook.effect.LegacyOakskinEffect;
import net.purespellbook.effect.OverloadEffect;
import net.purespellbook.effect.PurificationEffect;
import net.purespellbook.effect.SimplePureBuffEffect;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<MobEffect> EMPOWERED = MOB_EFFECTS.register("empowered",
            () -> new EmpoweredEffect(MobEffectCategory.BENEFICIAL, 0xA8E6FF)
                    .addAttributeModifier(AttributeRegistry.SPELL_POWER.get(),
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f01", 0.40D, AttributeModifier.Operation.MULTIPLY_BASE)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f02", 0.85D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<MobEffect> WEAPON_INTERACTION = MOB_EFFECTS.register("weapon_interaction",
            () -> new SimplePureBuffEffect(MobEffectCategory.BENEFICIAL, 0x38BDF8));

    public static final RegistryObject<MobEffect> SOUL_SHAVING = MOB_EFFECTS.register("soul_shaving",
            () -> new SimplePureBuffEffect(MobEffectCategory.BENEFICIAL, 0xA855F7));

    public static final RegistryObject<MobEffect> HEALING_ATTACK = MOB_EFFECTS.register("healing_attack",
            () -> new SimplePureBuffEffect(MobEffectCategory.BENEFICIAL, 0x4ADE80));

    public static final RegistryObject<MobEffect> OVERLOAD = MOB_EFFECTS.register("overload",
            () -> new OverloadEffect(MobEffectCategory.NEUTRAL, 0xFB7185)
                    .addAttributeModifier(AttributeRegistry.MAX_MANA.get(),
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f03", -0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> PURIFICATION = MOB_EFFECTS.register("purification",
            () -> new PurificationEffect(MobEffectCategory.BENEFICIAL, 0xFEF08A));

    public static final RegistryObject<MobEffect> EVASIVE_DODGE = MOB_EFFECTS.register("evasive_dodge",
            () -> new EvasiveDodgeEffect(MobEffectCategory.BENEFICIAL, 0x9D6BFF));

    public static final RegistryObject<MobEffect> LEGACY_ECHOING_STRIKES = MOB_EFFECTS.register("legacy_echoing_strikes",
            () -> new LegacyEchoStrikesEffect(MobEffectCategory.BENEFICIAL, 0x9F0BE3));

    public static final RegistryObject<MobEffect> LEGACY_OAKSKIN = MOB_EFFECTS.register("legacy_oakskin",
            () -> new LegacyOakskinEffect(MobEffectCategory.BENEFICIAL, 0xFFEF95));

    public static final RegistryObject<MobEffect> LEGACY_HASTENED = MOB_EFFECTS.register("legacy_hastened",
            () -> new LegacyHastenedEffect(MobEffectCategory.BENEFICIAL, 0xD9C043)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f04", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(Attributes.ATTACK_SPEED,
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f05", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(AttributeRegistry.MANA_REGEN.get(),
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f06", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL)
                    .addAttributeModifier(AttributeRegistry.CAST_TIME_REDUCTION.get(),
                            "8f4c1a2e-3b5d-4c7e-9f1a-2b3c4d5e6f07", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
