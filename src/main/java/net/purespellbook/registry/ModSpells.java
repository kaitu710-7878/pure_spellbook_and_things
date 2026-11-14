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
                EVASIVE_MOVEMENT
        );
    }

    public static void register(IEventBus eventBus) {
        SPELLS.register(eventBus);
    }
}
