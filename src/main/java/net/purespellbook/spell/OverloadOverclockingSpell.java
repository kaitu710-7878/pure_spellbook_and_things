package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.purespellbook.registry.ModMobEffects;

import java.util.List;

public class OverloadOverclockingSpell extends AbstractPureSpell {
    public OverloadOverclockingSpell() {
        super("overload_overclocking", 0.0D, 0, 0);
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public int getManaCost(int level) {
        return 0;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.pure_spellbook_and_things.overload_overclocking_desc")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        MobEffectInstance existing = entity.getEffect(ModMobEffects.OVERLOAD.get());
        int newDuration;
        int newAmplifier;
        if (existing != null) {
            newDuration = existing.getDuration() + 1200; // +1 minute
            newAmplifier = existing.getAmplifier() + 1;
        } else {
            newDuration = 800; // 40 seconds
            newAmplifier = 0;
        }

        // Apply Overload effect first (modifies Max Mana attribute)
        entity.addEffect(new MobEffectInstance(ModMobEffects.OVERLOAD.get(),
                newDuration, newAmplifier, false, false, true));

        // Reset all cooldowns and restore mana to max
        playerMagicData.getPlayerCooldowns().clearCooldowns();
        float maxMana = (float) entity.getAttributeValue(AttributeRegistry.MAX_MANA.get());
        playerMagicData.setMana(maxMana);

        if (entity instanceof ServerPlayer serverPlayer) {
            playerMagicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(playerMagicData));
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
