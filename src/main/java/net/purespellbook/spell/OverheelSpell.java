package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.particles.HealParticlesPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.purespellbook.registry.ModAttributes;

import java.util.List;

public class OverheelSpell extends AbstractPureSpell {
    public OverheelSpell() {
        super("overheel", 75.0D, 235, 120);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public boolean hasCooldownLevelScaling() {
        return true;
    }

    @Override
    public int getCastTime(int level) {
        return scaledCastTime(level);
    }

    public double getOverHpGain(LivingEntity caster) {
        return 10.0D * getEntityPowerMultiplier(caster);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.greater_healing"),
                Component.translatable("ui.pure_spellbook_and_things.over_hp_gain",
                        Utils.stringTruncation(getOverHpGain(caster), 1))
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float heal = entity.getMaxHealth();
        MinecraftForge.EVENT_BUS.post(new SpellHealEvent(entity, entity, heal, getSchoolType()));
        entity.heal(heal);

        AttributeInstance attr = entity.getAttribute(ModAttributes.OVER_HP.get());
        if (attr != null) {
            attr.setBaseValue(attr.getBaseValue() + getOverHpGain(entity));
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new HealParticlesPacket(entity.position()));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
