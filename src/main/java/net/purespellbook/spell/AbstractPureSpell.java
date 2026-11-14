package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.registry.ModSchools;

/**
 * Base class for all Pure school spells.
 * All Pure spells are Legendary with max level 1 as their standard max enhancement stage,
 * cannot be crafted in a regular Scroll Forge, and support optional addon level scaling.
 */
public abstract class AbstractPureSpell extends AbstractSpell {
    private final ResourceLocation spellId;
    private final DefaultConfig defaultConfig;

    protected AbstractPureSpell(String name, double cooldownSeconds, int baseManaCost, int castTime) {
        this.spellId = new ResourceLocation(PureSpellbookMod.MOD_ID, name);
        this.defaultConfig = new DefaultConfig()
                .setMinRarity(SpellRarity.LEGENDARY)
                .setSchoolResource(ModSchools.PURE_RESOURCE)
                .setMaxLevel(1)
                .setCooldownSeconds(cooldownSeconds)
                .build();
        this.baseManaCost = baseManaCost;
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = castTime;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public boolean allowCrafting() {
        return false;
    }

    /**
     * Whether higher spell levels (from addon tiers) reduce cooldown.
     */
    public boolean hasCooldownLevelScaling() {
        return false;
    }

    public float getCooldownMultiplierForLevel(int spellLevel) {
        if (!hasCooldownLevelScaling() || spellLevel <= 1) {
            return 1.0F;
        }
        return Math.max(0.2F, 1.0F - 0.1F * (spellLevel - 1));
    }

    protected int scaledManaCost(int spellLevel) {
        if (spellLevel <= 1 || this.baseManaCost <= 0) {
            return super.getManaCost(spellLevel);
        }
        double mult = Math.max(0.2D, 1.0D - 0.1D * (spellLevel - 1));
        return Math.max(1, (int) Math.round(super.getManaCost(1) * mult));
    }

    protected int scaledCastTime(int spellLevel) {
        if (spellLevel <= 1 || this.castTime <= 0) {
            return this.castTime;
        }
        float mult = Math.max(0.2F, 1.0F - 0.1F * (spellLevel - 1));
        return Math.max(5, Math.round(this.castTime * mult));
    }
}
