package net.purespellbook.spell;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.network.particles.TeleportParticlesPacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class EvasiveMovementSpell extends AbstractPureSpell {
    private static final float TELEPORT_DISTANCE = 10.0F;

    public EvasiveMovementSpell() {
        super("evasive_movement", 6.0D, 25, 0);
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public int getEvasionDurationTicks(int spellLevel) {
        return 100 + Math.max(0, spellLevel - 1) * 40; // 5s at level 1, +2s per extra level
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.distance", Utils.stringTruncation(TELEPORT_DISTANCE, 1)),
                Component.translatable("ui.irons_spellbooks.hits_dodged", 1),
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getEvasionDurationTicks(spellLevel), 1))
        );
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ENDERMAN_TELEPORT);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vec3 dest = TeleportSpell.findTeleportLocation(level, entity, TELEPORT_DISTANCE);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new TeleportParticlesPacket(entity.position(), dest));
        if (entity.isPassenger()) {
            entity.stopRiding();
        }
        Utils.handleSpellTeleport(this, entity, dest);
        entity.resetFallDistance();
        entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 1.0F);

        // Amplifier 0 grants 1 evasion hit in ISS EvasionEffect
        entity.addEffect(new MobEffectInstance(MobEffectRegistry.EVASION.get(),
                getEvasionDurationTicks(spellLevel), 0, false, false, true));

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return AnimationHolder.none();
    }
}
