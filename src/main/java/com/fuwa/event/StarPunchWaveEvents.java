package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Star Punch projectile: a traveling star that deals damage on contact, penetrates entities,
 * and fades after {@link #MAX_DISTANCE} blocks in any look direction (including diagonal/down).
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class StarPunchWaveEvents {
    private static final List<StarProjectile> ACTIVE = new ArrayList<>();

    public static final double MAX_DISTANCE = 6.0D;
    /** Hit radius of the star projectile (blocks). */
    public static final double HIT_RADIUS = 1.6D;
    /** Blocks advanced per tick — kept in sync with the leading particle velocity. */
    public static final double STEP_PER_TICK = 0.35D;

    public static void spawn(ServerLevel level, Player owner, Vec3 origin, Vec3 direction,
                             float damage, float knockback) {
        Vec3 dir = direction.normalize();
        StarProjectile projectile = new StarProjectile(level, owner.getUUID(), origin, dir, damage, knockback);
        projectile.spawnLeadingStar();
        ACTIVE.add(projectile);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE.isEmpty()) {
            return;
        }

        Iterator<StarProjectile> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().tick()) {
                iterator.remove();
            }
        }
    }

    private static final class StarProjectile {
        private final ServerLevel level;
        private final UUID ownerId;
        private final Vec3 direction;
        private final float damage;
        private final float knockback;
        private final Set<UUID> hitEntities = new HashSet<>();

        private Vec3 position;
        private double traveled;

        private StarProjectile(ServerLevel level, UUID ownerId, Vec3 origin, Vec3 direction,
                               float damage, float knockback) {
            this.level = level;
            this.ownerId = ownerId;
            this.position = origin;
            this.direction = direction;
            this.damage = damage;
            this.knockback = knockback;
        }

        private void spawnLeadingStar() {
            // One continuous particle that flies with the projectile (no face-stacking).
            Vec3 velocity = this.direction.scale(STEP_PER_TICK);
            this.level.sendParticles(ModParticles.STAR_WAVE.get(),
                    this.position.x, this.position.y, this.position.z,
                    0, velocity.x, velocity.y, velocity.z, 1.0D);
        }

        private boolean tick() {
            if (this.level == null || this.level.getServer() == null) {
                return false;
            }

            Vec3 previous = this.position;
            double remaining = MAX_DISTANCE - this.traveled;
            if (remaining <= 0.0D) {
                fadeOut();
                return false;
            }

            double step = Math.min(STEP_PER_TICK, remaining);
            Vec3 next = previous.add(this.direction.scale(step));
            this.position = next;
            this.traveled += step;

            spawnTrail(previous, next);
            hurtAlongPath(previous, next);

            if (this.traveled >= MAX_DISTANCE - 1.0E-4D) {
                fadeOut();
                return false;
            }
            return true;
        }

        private void spawnTrail(Vec3 from, Vec3 to) {
            // Light sparkles only — the pink star itself is the single leading particle.
            for (int i = 0; i < 2; i++) {
                double t = this.level.random.nextDouble();
                Vec3 pos = from.lerp(to, t).add(
                        (this.level.random.nextDouble() - 0.5D) * HIT_RADIUS * 0.45D,
                        (this.level.random.nextDouble() - 0.5D) * HIT_RADIUS * 0.45D,
                        (this.level.random.nextDouble() - 0.5D) * HIT_RADIUS * 0.45D);
                this.level.sendParticles(ModParticles.STAR_PARTICLE.get(),
                        pos.x, pos.y, pos.z,
                        0,
                        (this.level.random.nextDouble() - 0.5D) * 0.03D,
                        (this.level.random.nextDouble() - 0.5D) * 0.03D,
                        (this.level.random.nextDouble() - 0.5D) * 0.03D,
                        1.0D);
            }
        }

        private void hurtAlongPath(Vec3 from, Vec3 to) {
            AABB sweep = new AABB(from, to).inflate(HIT_RADIUS + 0.75D);
            List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, sweep,
                    entity -> entity.isAlive()
                            && !entity.getUUID().equals(this.ownerId)
                            && !this.hitEntities.contains(entity.getUUID()));

            for (LivingEntity target : targets) {
                if (!intersectsPath(target, from, to)) {
                    continue;
                }

                this.hitEntities.add(target.getUUID());
                target.hurt(createDamageSource(), this.damage);

                Vec3 push = this.direction.scale(this.knockback * 0.85D);
                target.setDeltaMovement(target.getDeltaMovement().add(push.x, push.y + 0.15D, push.z));
                target.hasImpulse = true;

                this.level.sendParticles(ModParticles.STAR_PARTICLE.get(),
                        target.getX(), target.getY(0.5D), target.getZ(),
                        12, 0.35D, 0.4D, 0.35D, 0.08D);
                this.level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.55F, 1.35F);
            }
        }

        private boolean intersectsPath(LivingEntity target, Vec3 from, Vec3 to) {
            Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            double radius = HIT_RADIUS + Math.max(target.getBbWidth(), target.getBbHeight()) * 0.35D;

            Vec3 segment = to.subtract(from);
            double lengthSq = segment.lengthSqr();
            if (lengthSq < 1.0E-6D) {
                return center.distanceToSqr(from) <= radius * radius;
            }

            double t = center.subtract(from).dot(segment) / lengthSq;
            t = Math.max(0.0D, Math.min(1.0D, t));
            Vec3 closest = from.add(segment.scale(t));
            return closest.distanceToSqr(center) <= radius * radius;
        }

        private void fadeOut() {
            this.level.sendParticles(ModParticles.STAR_PARTICLE.get(),
                    this.position.x, this.position.y, this.position.z,
                    18, 0.45D, 0.45D, 0.45D, 0.05D);
            this.level.playSound(null, this.position.x, this.position.y, this.position.z,
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7F, 1.5F);
        }

        private DamageSource createDamageSource() {
            Player owner = this.level.getPlayerByUUID(this.ownerId);
            if (owner != null) {
                return this.level.damageSources().playerAttack(owner);
            }
            return this.level.damageSources().magic();
        }
    }
}
