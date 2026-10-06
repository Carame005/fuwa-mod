package com.fuwa.entity;

import com.fuwa.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Falling meteorite that slowly spins down and spawns a wild Fuwa or Prunce on impact.
 */
public class MeteoriteEntity extends Entity {
    private static final EntityDataAccessor<Boolean> DATA_SPAWN_FUWA =
            SynchedEntityData.defineId(MeteoriteEntity.class, EntityDataSerializers.BOOLEAN);

    /** Blocks fallen per tick — slow enough to watch the spin and trail. */
    public static final double FALL_SPEED = 0.18D;
    private static final int MAX_LIFE_TICKS = 20 * 45;

    public MeteoriteEntity(EntityType<? extends MeteoriteEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public MeteoriteEntity(Level level, double x, double y, double z, boolean spawnFuwa) {
        this(ModEntities.METEORITE.get(), level);
        this.setPos(x, y, z);
        this.setSpawnFuwa(spawnFuwa);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public void setSpawnFuwa(boolean spawnFuwa) {
        this.entityData.set(DATA_SPAWN_FUWA, spawnFuwa);
    }

    public boolean shouldSpawnFuwa() {
        return this.entityData.get(DATA_SPAWN_FUWA);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_SPAWN_FUWA, true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > MAX_LIFE_TICKS) {
            this.discard();
            return;
        }

        this.setDeltaMovement(0.0D, -FALL_SPEED, 0.0D);
        this.move(MoverType.SELF, this.getDeltaMovement());

        spawnTrailParticles();

        if (!this.level().isClientSide() && (this.onGround() || this.verticalCollision)) {
            this.impact();
        }
    }

    private void spawnTrailParticles() {
        Level level = this.level();
        double x = this.getX();
        double y = this.getY() + this.getBbHeight() * 0.5D;
        double z = this.getZ();

        for (int i = 0; i < 4; i++) {
            double ox = (this.random.nextDouble() - 0.5D) * 0.7D;
            double oy = (this.random.nextDouble() - 0.5D) * 0.7D;
            double oz = (this.random.nextDouble() - 0.5D) * 0.7D;
            level.addParticle(ParticleTypes.FLAME, x + ox, y + oy, z + oz, 0.0D, 0.02D, 0.0D);
        }

        if (this.tickCount % 2 == 0) {
            level.addParticle(ParticleTypes.LARGE_SMOKE,
                    x + (this.random.nextDouble() - 0.5D) * 0.5D,
                    y,
                    z + (this.random.nextDouble() - 0.5D) * 0.5D,
                    0.0D, 0.05D, 0.0D);
            level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private void impact() {
        if (!(this.level() instanceof ServerLevel server)) {
            this.discard();
            return;
        }

        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        server.sendParticles(ParticleTypes.EXPLOSION, x, y + 0.5D, z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
        server.sendParticles(ParticleTypes.FLAME, x, y + 0.3D, z, 40, 0.6D, 0.4D, 0.6D, 0.05D);
        server.sendParticles(ParticleTypes.LAVA, x, y + 0.2D, z, 18, 0.5D, 0.2D, 0.5D, 0.0D);
        server.sendParticles(ParticleTypes.SMOKE, x, y + 0.4D, z, 25, 0.5D, 0.3D, 0.5D, 0.02D);

        server.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.WEATHER, 1.2F, 0.85F);
        server.playSound(null, x, y, z, SoundEvents.FIRECHARGE_USE, SoundSource.WEATHER, 1.0F, 0.7F);

        TamableAnimal companion = this.shouldSpawnFuwa()
                ? ModEntities.FUWA.get().create(server)
                : ModEntities.PRUNCE.get().create(server);

        if (companion != null) {
            companion.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
            companion.setPersistenceRequired();
            server.addFreshEntity(companion);
        }

        this.discard();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("SpawnFuwa")) {
            this.setSpawnFuwa(tag.getBoolean("SpawnFuwa"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("SpawnFuwa", this.shouldSpawnFuwa());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 128.0D * 128.0D;
    }
}
