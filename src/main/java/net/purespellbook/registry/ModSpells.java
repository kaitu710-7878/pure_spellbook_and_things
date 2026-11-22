package net.purespellbook.registry;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.spell.*;

import java.util.List;
import java.util.function.Supplier;

public class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<AbstractSpell> EMPOWERED = registerSpell(EmpoweredSpell::new);
    public static final RegistryObject<AbstractSpell> WEAPON_INTERACTION = registerSpell(WeaponInteractionSpell::new);
    public static final RegistryObject<AbstractSpell> SOUL_SHAVING = registerSpell(SoulShavingSpell::new);
    public static final RegistryObject<AbstractSpell> HEALING_ATTACK = registerSpell(HealingAttackSpell::new);
    public static final RegistryObject<AbstractSpell> OVERLOAD_OVERCLOCKING = registerSpell(OverloadOverclockingSpell::new);
    public static final RegistryObject<AbstractSpell> VITALITY_RECOVERY = registerSpell(VitalityRecoverySpell::new);
    public static final RegistryObject<AbstractSpell> SUSTAINED_PURIFICATION = registerSpell(SustainedPurificationSpell::new);
    public static final RegistryObject<AbstractSpell> OVERHEEL = registerSpell(OverheelSpell::new);
    public static final RegistryObject<AbstractSpell> EVASIVE_MOVEMENT = registerSpell(EvasiveMovementSpell::new);
    public static final RegistryObject<AbstractSpell> AKASHIC_RECORD_ACCESS = registerSpell(AkashicRecordAccessSpell::new);
    public static final RegistryObject<AbstractSpell> AKASHIC_RECORD_EXEC_1 = registerSpell(() -> new AkashicRecordExecSpell(1));
    public static final RegistryObject<AbstractSpell> AKASHIC_RECORD_EXEC_2 = registerSpell(() -> new AkashicRecordExecSpell(2));
    public static final RegistryObject<AbstractSpell> AKASHIC_RECORD_EXEC_3 = registerSpell(() -> new AkashicRecordExecSpell(3));
    public static final RegistryObject<AbstractSpell> AKASHIC_RECORD_EXEC_4 = registerSpell(() -> new AkashicRecordExecSpell(4));
    public static final RegistryObject<AbstractSpell> EXTENSION = registerSpell(ExtensionSpell::new);
    public static final RegistryObject<AbstractSpell> LEGACY_ECHO_STRIKE = registerSpell(LegacyEchoStrikeSpell::new);
    public static final RegistryObject<AbstractSpell> LEGACY_OAKSKIN = registerSpell(LegacyOakskinSpell::new);
    public static final RegistryObject<AbstractSpell> LEGACY_HASTE = registerSpell(LegacyHasteSpell::new);

    private static RegistryObject<AbstractSpell> registerSpell(Supplier<AbstractSpell> supplier) {
        AbstractSpell spell = supplier.get();
        return SPELLS.register(spell.getSpellName(), () -> spell);
    }

    public static List<RegistryObject<AbstractSpell>> allPureSpells() {
        return List.of(
                EMPOWERED,
                WEAPON_INTERACTION,
                SOUL_SHAVING,
                HEALING_ATTACK,
                OVERLOAD_OVERCLOCKING,
                VITALITY_RECOVERY,
                SUSTAINED_PURIFICATION,
                OVERHEEL,
                EVASIVE_MOVEMENT,
                AKASHIC_RECORD_ACCESS,
                AKASHIC_RECORD_EXEC_1,
                AKASHIC_RECORD_EXEC_2,
                AKASHIC_RECORD_EXEC_3,
                AKASHIC_RECORD_EXEC_4,
                EXTENSION,
                LEGACY_ECHO_STRIKE,
                LEGACY_OAKSKIN,
                LEGACY_HASTE
        );
    }

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
