package net.purespellbook.registry;

import io.redspace.ironsspellbooks.item.armor.UpgradeOrbType;
import io.redspace.ironsspellbooks.registries.UpgradeOrbTypeRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.purespellbook.PureSpellbookMod;

/**
 * Datapack UpgradeOrbType keys for Pure Spellbook and things.
 */
public final class ModUpgradeOrbTypes {
    public static final ResourceKey<UpgradeOrbType> PURE_SPELL_POWER = ResourceKey.create(
            UpgradeOrbTypeRegistry.UPGRADE_ORB_REGISTRY_KEY,
            new ResourceLocation(PureSpellbookMod.MOD_ID, "pure_power")
    );

    private ModUpgradeOrbTypes() {}
}
