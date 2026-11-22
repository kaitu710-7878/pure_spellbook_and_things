package net.purespellbook.entity;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.purespellbook.registry.ModEntities;
import net.purespellbook.registry.ModSpells;

import java.util.Optional;

/**
 * ISS 3.4.0.4-era Echoing Strike AoE entity.
 */
public class LegacyEchoStrikeEntity extends AoeEntity {
    public final int waitTime = 20;

    public LegacyEchoStrikeEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.setCircular();
    }

    public LegacyEchoStrikeEntity(Level level, LivingEntity owner, float damage, float radius) {
        this(ModEntities.LEGACY_ECHO_STRIKE.get(), level);
        setOwner(owner);
        this.setRadius(radius);
        this.setDamage(damage);
    }

    @Override
    public void applyEffect(LivingEntity target) {
        // Effect handling is done in tick
    }

    @Override
    public void tick() {
        if (tickCount == waitTime) {
            this.playSound(SoundRegistry.ECHOING_STRIKE.get(), 1.0F, Utils.random.nextIntBetweenInclusive(8, 12) * 0.1F);
            if (!level().isClientSide) {
                Vec3 center = this.getBoundingBox().getCenter();
                MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, center.x, center.y, center.z, 25, 0, 0, 0, 0.18D, false);
                MagicManager.spawnParticles(level(), new BlastwaveParticleOptions(
                        ModSpells.LEGACY_ECHO_STRIKE.get().getSchoolType().getTargetingColor(),
                        this.getRadius() * 0.9F), center.x, center.y, center.z, 1, 0, 0, 0, 0, true);
                float explosionRadius = getRadius();
                float explosionRadiusSqr = explosionRadius * explosionRadius;
                var entities = level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
                Vec3 losCenter = Utils.moveToRelativeGroundLevel(level(), center, 2);
                losCenter = Utils.raycastForBlock(level(), losCenter, losCenter.add(0, 3, 0), ClipContext.Fluid.NONE)
                        .getLocation().add(losCenter).scale(0.5D);
                for (Entity entity : entities) {
                    double distanceSqr = entity.distanceToSqr(center);
                    if (distanceSqr < explosionRadiusSqr
                            && canHitEntity(entity)
                            && Utils.hasLineOfSight(level(), losCenter, entity.getBoundingBox().getCenter(), true)) {
                        double p = Mth.clamp((1.0D - distanceSqr / explosionRadiusSqr) + 0.4F, 0.0D, 1.0D);
                        float finalDamage = (float) (this.damage * p);
                        DamageSources.applyDamage(entity, finalDamage, ModSpells.LEGACY_ECHO_STRIKE.get().getDamageSource(this, getOwner()));
                    }
                }
            }
        } else if (tickCount > waitTime) {
            discard();
        }
        if (level().isClientSide && tickCount < waitTime / 2) {
            Vec3 position = this.getBoundingBox().getCenter();
            for (int i = 0; i < 3; i++) {
                Vec3 vec3 = Utils.getRandomVec3(1.0F);
                vec3 = vec3.multiply(vec3).multiply(Mth.sign(vec3.x), Mth.sign(vec3.y), Mth.sign(vec3.z))
                        .scale(this.getRadius()).add(position);
                Vec3 motion = position.subtract(vec3).scale(0.125F);
                level().addParticle(ParticleHelper.UNSTABLE_ENDER, vec3.x, vec3.y - 0.5D, vec3.z, motion.x, motion.y, motion.z);
            }
        }
    }

    @Override
    protected boolean canHitTargetForGroundContext(LivingEntity target) {
        return true;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    @Override
    public void ambientParticles() {
    }

    @Override
    public float getParticleCount() {
        return 0;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.empty();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
