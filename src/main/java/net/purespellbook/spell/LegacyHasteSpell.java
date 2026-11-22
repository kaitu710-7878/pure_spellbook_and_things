package net.purespellbook.spell;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetedTargetAreaCastData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.purespellbook.registry.ModMobEffects;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Pure-school recreation of ISS 3.4.0.4's Haste spell.
 * Level 1 matches the original spell's max level (6), and addon tiers scale identically.
 */
public class LegacyHasteSpell extends AbstractPureSpell {
    private static final int ORIGINAL_MAX_LEVEL = 6;
    private static final int MAX_TARGETS = 5;
    private static final ResourceLocation ISS_ICON =
            new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/spell_icons/haste.png");

    public LegacyHasteSpell() {
        super("legacy_haste", 45.0D, 100, 30);
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 55;
        this.spellPowerPerLevel = 5;
    }

    @Override
    public ResourceLocation getCustomSpellIconResource() {
        return ISS_ICON;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundRegistry.CLOUD_OF_REGEN_LOOP.get());
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.hastened",
                        Utils.stringTruncation((1 + getAmplifier(spellLevel)) * 0.1F * 100.0F, 1)),
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Utils.timeFromTicks(getDuration(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.max_victims", MAX_TARGETS)
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (!Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F, false)) {
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(entity));
            if (entity instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("ui.irons_spellbooks.spell_target_success_self",
                                this.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
            }
        }
        float radius = 3.0F;
        LivingEntity target = ((TargetEntityCastData) playerMagicData.getAdditionalCastData()).getTarget((ServerLevel) level);
        TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(
                level, target.position(), radius, Utils.packRGB(this.getTargetingColor()), target);
        playerMagicData.setAdditionalCastData(new TargetedTargetAreaCastData(target, area));
        return true;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetedTargetAreaCastData targetData) {
            LivingEntity targetEntity = targetData.getTarget((ServerLevel) world);
            if (targetEntity != null) {
                float radius = 3.0F;
                AtomicInteger targets = new AtomicInteger(0);
                targetEntity.level().getEntitiesOfClass(LivingEntity.class, targetEntity.getBoundingBox().inflate(radius))
                        .forEach(victim -> {
                            if (targets.get() < MAX_TARGETS
                                    && victim.distanceToSqr(targetEntity) < radius * radius
                                    && Utils.shouldHealEntity(entity, victim)) {
                                victim.addEffect(new MobEffectInstance(
                                        ModMobEffects.LEGACY_HASTENED.get(),
                                        getDuration(spellLevel, entity),
                                        getAmplifier(spellLevel)));
                                targets.incrementAndGet();
                            }
                        });
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    public int getAmplifier(int spellLevel) {
        int effectiveLevel = ORIGINAL_MAX_LEVEL + (spellLevel - 1);
        return effectiveLevel - 1;
    }

    public int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 20);
    }
}
