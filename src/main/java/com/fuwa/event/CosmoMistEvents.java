package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Lingering Cosmo mist patches left by Cosmo Shining sprays.
 * Damages living entities inside on a fixed interval.
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class CosmoMistEvents {
    private static final List<MistPatch> ACTIVE = new ArrayList<>();

    public static void spawn(ServerLevel level, Player owner, Vec3 center, double radius,
                             int durationTicks, int damageInterval, float damage) {
        for (MistPatch patch : ACTIVE) {
            if (patch.level == level && patch.center.distanceToSqr(center) < radius * radius * 0.25D) {
                patch.remainingTicks = Math.max(patch.remainingTicks, durationTicks);
                patch.damage = Math.max(patch.damage, damage);
                return;
            }
        }
        ACTIVE.add(new MistPatch(level, owner.getUUID(), center, radius, durationTicks, damageInterval, damage));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE.isEmpty()) {
            return;
        }

        Iterator<MistPatch> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().tick()) {
                iterator.remove();
            }
        }
    }

    private static final class MistPatch {
        private final ServerLevel level;
        private final UUID ownerId;
        private final Vec3 center;
        private final double radius;
        private final int damageInterval;
        private int remainingTicks;
        private int ticksAlive;
        private float damage;

        private MistPatch(ServerLevel level, UUID ownerId, Vec3 center, double radius,
                          int durationTicks, int damageInterval, float damage) {
            this.level = level;
            this.ownerId = ownerId;
            this.center = center;
            this.radius = radius;
            this.remainingTicks = durationTicks;
            this.damageInterval = Math.max(1, damageInterval);
            this.damage = damage;
        }

        private boolean tick() {
            if (this.level == null || this.level.getServer() == null) {
                return false;
            }

            this.ticksAlive++;
            this.remainingTicks--;

            spawnAmbientParticles();

            if (this.ticksAlive % this.damageInterval == 0) {
                hurtEntities();
            }

            return this.remainingTicks > 0;
        }

        private void spawnAmbientParticles() {
            // Main blue mist cloud, same spread pattern as the old CLOUD placeholder.
            this.level.sendParticles(ModParticles.COSMO_BLUE_MIST.get(),
                    this.center.x, this.center.y + 0.35D, this.center.z,
                    4, this.radius * 0.45D, 0.35D, this.radius * 0.45D, 0.01D);

            // Fewer triangles inside the mist.
            if (this.ticksAlive % 2 == 0) {
                this.level.sendParticles(ModParticles.COSMO_TRIANGLE.get(),
                        this.center.x, this.center.y + 0.4D, this.center.z,
                        1, this.radius * 0.35D, 0.25D, this.radius * 0.35D, 0.01D);
            }
        }

        private void hurtEntities() {
            AABB box = new AABB(this.center, this.center).inflate(this.radius, this.radius * 0.75D, this.radius);
            Player owner = this.level.getPlayerByUUID(this.ownerId);
            List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, box,
                    entity -> entity.isAlive()
                            && !entity.getUUID().equals(this.ownerId)
                            && (owner == null || !entity.isAlliedTo(owner)));

            boolean hitAny = false;
            for (LivingEntity target : targets) {
                double dx = target.getX() - this.center.x;
                double dz = target.getZ() - this.center.z;
                if (dx * dx + dz * dz > this.radius * this.radius) {
                    continue;
                }

                hitAny = true;
                Vec3 motion = target.getDeltaMovement();
                boolean damaged;
                if (owner != null) {
                    damaged = target.hurt(this.level.damageSources().playerAttack(owner), this.damage);
                } else {
                    damaged = target.hurt(this.level.damageSources().magic(), this.damage);
                }
                // Mist should not knock entities back.
                if (damaged) {
                    target.setDeltaMovement(motion);
                    target.hurtMarked = true;
                }

                this.level.sendParticles(ModParticles.COSMO_BLUE_MIST.get(),
                        target.getX(), target.getY(0.5D), target.getZ(),
                        3, 0.2D, 0.25D, 0.2D, 0.02D);
                this.level.sendParticles(ModParticles.COSMO_TRIANGLE.get(),
                        target.getX(), target.getY(0.55D), target.getZ(),
                        2, 0.15D, 0.2D, 0.15D, 0.01D);
            }

            if (hitAny) {
                this.level.playSound(null, this.center.x, this.center.y, this.center.z,
                        SoundEvents.PLAYER_SPLASH, SoundSource.PLAYERS, 0.2F, 1.4F);
            }
        }
    }
}
