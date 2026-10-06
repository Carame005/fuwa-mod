package com.fuwa.entity;

import com.fuwa.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Special Selene arrow: on impact creates a 3x3 freeze splash that damages mobs and turns water into ice.
 */
public class SeleneArrowEntity extends AbstractArrow {
    /** Half-extent of the splash cube: ±1 block = 3x3x3 area. */
    public static final int SPLASH_BLOCK_RANGE = 1;
    /** Entity hit radius matching the 3x3 footprint (~1.5 blocks from center). */
    public static final double SPLASH_RADIUS = 1.5D;
    public static final float SPLASH_DAMAGE = 3.0F;
    /** ~10 seconds of freeze buildup (powder-snow style ticks). */
    public static final int FREEZE_TICKS = 400;
    /** Slowness lasts ~12 seconds so the chill remains after the freeze peaks. */
    public static final int SLOW_DURATION_TICKS = 240;

    public SeleneArrowEntity(EntityType<? extends SeleneArrowEntity> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
    }

    public SeleneArrowEntity(Level level, LivingEntity shooter) {
        super(ModEntities.SELENE_ARROW.get(), shooter, level);
        this.pickup = Pickup.DISALLOWED;
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        if (owner != null && target.is(owner)) {
            return;
        }

        float directDamage = (float) this.getBaseDamage();
        if (this.isCritArrow()) {
            long bonus = this.random.nextInt(Mth.ceil(directDamage / 2.0F) + 2);
            directDamage += bonus;
        }

        // Hurt any entity (End crystals, Ender Dragon parts, etc.), not only LivingEntity.
        Entity damageOwner = owner == null ? this : owner;
        target.hurt(this.damageSources().arrow(this, damageOwner), directDamage);
        if (damageOwner instanceof LivingEntity livingOwner) {
            livingOwner.setLastHurtMob(target);
        }

        createFreezeSplash(result.getLocation());
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        createFreezeSplash(result.getLocation());
        this.discard();
    }

    private void createFreezeSplash(Vec3 hitPos) {
        Level level = this.level();
        if (level.isClientSide()) {
            return;
        }

        Entity owner = this.getOwner();
        AABB box = new AABB(hitPos, hitPos).inflate(SPLASH_RADIUS);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, box,
                entity -> entity.isAlive()
                        && (owner == null || !entity.is(owner))
                        && entity.distanceToSqr(hitPos) <= SPLASH_RADIUS * SPLASH_RADIUS);

        for (LivingEntity victim : victims) {
            victim.hurt(this.damageSources().magic(), SPLASH_DAMAGE);
            victim.setTicksFrozen(Math.max(victim.getTicksFrozen(), FREEZE_TICKS));
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION_TICKS, 1, false, true, true));
        }

        freezeWaterInSplash(BlockPos.containing(hitPos));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    hitPos.x, hitPos.y, hitPos.z,
                    28, SPLASH_RADIUS * 0.45D, SPLASH_RADIUS * 0.35D, SPLASH_RADIUS * 0.45D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                    hitPos.x, hitPos.y, hitPos.z,
                    12, SPLASH_RADIUS * 0.4D, SPLASH_RADIUS * 0.25D, SPLASH_RADIUS * 0.4D, 0.08D);
        }

        level.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.7F, 1.4F);
        level.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.PLAYER_HURT_FREEZE, SoundSource.NEUTRAL, 0.85F, 0.9F);
    }

    private void freezeWaterInSplash(BlockPos center) {
        Level level = this.level();
        boolean frozeAny = false;

        for (int dx = -SPLASH_BLOCK_RANGE; dx <= SPLASH_BLOCK_RANGE; dx++) {
            for (int dy = -SPLASH_BLOCK_RANGE; dy <= SPLASH_BLOCK_RANGE; dy++) {
                for (int dz = -SPLASH_BLOCK_RANGE; dz <= SPLASH_BLOCK_RANGE; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (tryFreezeWaterAt(pos)) {
                        frozeAny = true;
                    }
                }
            }
        }

        if (frozeAny) {
            level.playSound(null, center, SoundEvents.GLASS_PLACE, SoundSource.BLOCKS, 0.8F, 1.2F);
        }
    }

    private boolean tryFreezeWaterAt(BlockPos pos) {
        Level level = this.level();
        BlockState state = level.getBlockState(pos);
        FluidState fluid = state.getFluidState();

        if (!fluid.is(FluidTags.WATER)) {
            return false;
        }

        // Full water blocks (source or flowing liquid block) become ice.
        if (state.getBlock() instanceof LiquidBlock) {
            level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
            return true;
        }

        // Waterlogged blocks: remove water by setting non-waterlogged if possible via replacing fluid.
        // For simplicity, only convert pure water liquid blocks above.
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && !this.isRemoved()) {
            // Colliding with / entering water also triggers the freeze splash.
            if (this.isInWater() || this.level().getFluidState(this.blockPosition()).is(FluidTags.WATER)) {
                createFreezeSplash(this.position());
                this.discard();
                return;
            }
        }

        if (this.level().isClientSide() && !this.inGround) {
            Vec3 motion = this.getDeltaMovement();
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                    this.getX(), this.getY(), this.getZ(),
                    -motion.x * 0.05D, 0.05D, -motion.z * 0.05D);
        }
    }

}
