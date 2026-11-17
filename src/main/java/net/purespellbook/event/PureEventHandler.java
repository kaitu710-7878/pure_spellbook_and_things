package net.purespellbook.event;

import dev.xkmc.l2library.base.effects.ForceAddEffectEvent;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellCooldownAddedEvent;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.akashic.AkashicRecordHelper;
import net.purespellbook.akashic.AkashicRecordSavedData;
import net.purespellbook.effect.EvasiveDodgeEffect;
import net.purespellbook.effect.PurificationEffect;
import net.purespellbook.network.ModNetwork;
import net.purespellbook.network.SyncAkashicRecordPacket;
import net.purespellbook.registry.ModAttributes;
import net.purespellbook.registry.ModMobEffects;
import net.purespellbook.spell.AbstractPureSpell;
import net.purespellbook.spell.AkashicRecordAccessSpell;
import net.purespellbook.spell.AkashicRecordExecSpell;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = PureSpellbookMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PureEventHandler {
    private static final List<Runnable> SCHEDULED_TASKS = new ArrayList<>();

    @SubscribeEvent
    public static void onSpellCooldownAdded(SpellCooldownAddedEvent.Pre event) {
        if (event.getSpell() instanceof AkashicRecordExecSpell execSpell && event.getEntity() != null) {
            int combinedCooldown = AkashicRecordHelper.getCombinedEffectiveCooldown(
                    event.getEntity(), execSpell.getPageIndex(), event.getCastSource());
            event.setEffectiveCooldown(combinedCooldown);
            return;
        }
        if (event.getSpell() instanceof AbstractPureSpell pureSpell && pureSpell.hasCooldownLevelScaling()) {
            int castLevel = MagicData.getPlayerMagicData(event.getEntity()).getCastingSpellLevel();
            if (castLevel > 1) {
                int adjusted = Math.max(1, Math.round(event.getEffectiveCooldown() * pureSpell.getCooldownMultiplierForLevel(castLevel)));
                event.setEffectiveCooldown(adjusted);
            }
        }
    }

    @SubscribeEvent
    public static void onModifySpellLevel(ModifySpellLevelEvent event) {
        if (event.getSpell() instanceof AkashicRecordAccessSpell || event.getSpell() instanceof AkashicRecordExecSpell) {
            event.setLevel(1);
        }
    }

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        if (!(event.getSpellDamageSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (attacker.level().isClientSide) {
            return;
        }
        if (attacker.hasEffect(ModMobEffects.WEAPON_INTERACTION.get())) {
            float weaponDamage = Utils.getWeaponDamage(attacker, event.getEntity().getMobType());
            if (weaponDamage > 0.0F) {
                event.setAmount(event.getAmount() + weaponDamage);
            }
        }
        if (attacker.hasEffect(ModMobEffects.HEALING_ATTACK.get())) {
            attacker.heal(1.0F);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult entityHit
                && entityHit.getEntity() instanceof LivingEntity target
                && !target.level().isClientSide
                && target.hasEffect(ModMobEffects.EVASIVE_DODGE.get())) {
            Projectile projectile = event.getProjectile();
            DamageSource indirect = target.damageSources().indirectMagic(projectile, projectile.getOwner());
            if (EvasiveDodgeEffect.doEffect(target, indirect)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !target.isAlive()) {
            return;
        }
        DamageSource source = event.getSource();
        if (target.hasEffect(ModMobEffects.EVASIVE_DODGE.get()) && EvasiveDodgeEffect.doEffect(target, source)) {
            event.setCanceled(true);
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == target) {
            return;
        }

        // Soul Shaving: triggers on every hit including during invulnerability frames and multi-hit spells
        if (attacker.hasEffect(ModMobEffects.SOUL_SHAVING.get())) {
            float shave = Mth.clamp(target.getMaxHealth() * 0.01F, 1.0F, 40.0F);
            float newHp = Math.max(0.0F, target.getHealth() - shave);
            target.setHealth(newHp);
        }

        // Healing Attack: melee hit restores 3 mana
        if (attacker.hasEffect(ModMobEffects.HEALING_ATTACK.get())
                && source.getDirectEntity() == attacker
                && !(source instanceof SpellDamageSource)
                && attacker instanceof ServerPlayer player) {
            MagicData magicData = MagicData.getPlayerMagicData(player);
            float maxMana = (float) player.getAttributeValue(AttributeRegistry.MAX_MANA.get());
            float nextMana = Math.min(maxMana, magicData.getMana() + 3.0F);
            if (nextMana != magicData.getMana()) {
                magicData.setMana(nextMana);
                PacketDistributor.sendToPlayer(player, new SyncManaPacket(magicData));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || !entity.isAlive()) {
            return;
        }

        // OverHP instant recovery
        double overHp = entity.getAttributeValue(ModAttributes.OVER_HP.get());
        if (overHp > 0.0D && entity.getHealth() < entity.getMaxHealth()) {
            float missing = entity.getMaxHealth() - entity.getHealth();
            float heal = (float) Math.min(missing, overHp);
            if (heal > 0.0F) {
                entity.heal(heal);
                AttributeInstance attr = entity.getAttribute(ModAttributes.OVER_HP.get());
                if (attr != null) {
                    attr.setBaseValue(Math.max(0.0D, attr.getBaseValue() - heal));
                }
            }
        }

        // Clamp mana if Overload reduced max mana below current mana
        if (entity instanceof ServerPlayer player && player.tickCount % 5 == 0
                && player.hasEffect(ModMobEffects.OVERLOAD.get())) {
            MagicData magicData = MagicData.getPlayerMagicData(player);
            float maxMana = (float) player.getAttributeValue(AttributeRegistry.MAX_MANA.get());
            if (magicData.getMana() > maxMana) {
                magicData.setMana(maxMana);
                PacketDistributor.sendToPlayer(player, new SyncManaPacket(magicData));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        AttributeInstance attr = event.getEntity().getAttribute(ModAttributes.OVER_HP.get());
        if (attr != null) {
            attr.setBaseValue(0.0D);
        }
    }

    // Purification protection & continuous cleansing (L2Complements CleanseEffect logic)
    @SubscribeEvent
    public static void onPotionApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().hasEffect(ModMobEffects.PURIFICATION.get())
                && !PurificationEffect.isProtectedEffect(event.getEffectInstance())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onForceAddEffect(ForceAddEffectEvent event) {
        if (event.getEntity().hasEffect(ModMobEffects.PURIFICATION.get())
                && !PurificationEffect.isProtectedEffect(event.getEffectInstance())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPotionAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide
                && entity.hasEffect(ModMobEffects.PURIFICATION.get())
                && !PurificationEffect.isProtectedEffect(event.getEffectInstance())) {
            synchronized (SCHEDULED_TASKS) {
                SCHEDULED_TASKS.add(() -> PurificationEffect.clearOnEntity(entity));
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        List<Runnable> tasks;
        synchronized (SCHEDULED_TASKS) {
            if (SCHEDULED_TASKS.isEmpty()) {
                return;
            }
            tasks = new ArrayList<>(SCHEDULED_TASKS);
            SCHEDULED_TASKS.clear();
        }
        tasks.forEach(Runnable::run);
    }

    private static void syncAkashicRecord(Player player) {
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            AkashicRecordSavedData data = AkashicRecordSavedData.get(serverPlayer.getServer());
            ModNetwork.sendToPlayer(serverPlayer, new SyncAkashicRecordPacket(data.getRecord(serverPlayer.getUUID())));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncAkashicRecord(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncAkashicRecord(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncAkashicRecord(event.getEntity());
    }
}
