package com.fuwa.entity;

import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FuwaEntity extends TamableAnimal implements FlyingAnimal {
    public static final byte EVENT_NOD = 40;
    public static final byte EVENT_TILT_RIGHT = 41;
    public static final byte EVENT_TILT_LEFT = 42;

    public final AnimationState idleLeftState = new AnimationState();
    public final AnimationState idleRightState = new AnimationState();
    public final AnimationState nodState = new AnimationState();
    public final AnimationState tiltRightState = new AnimationState();
    public final AnimationState tiltLeftState = new AnimationState();

    private static final int IDLE_GESTURE_TICKS = 40;
    private static final int IDLE_TILT_TICKS = 45;

    /** Client-only idle scheduler. */
    private int idlePhaseEndTick;
    private boolean idlePlayingGesture;

    public FuwaEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        // Primera animación pronto (~1–2 s), luego ya entra el ritmo con pausas.
        this.idlePhaseEndTick = 20 + this.random.nextInt(20);
        this.idlePlayingGesture = false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.55D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        // Empieza a seguir si se aleja ~5 bloques y se queda cerca (~1.5)
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 5.0F, 1.5F, true));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.4D) {
            @Override
            public boolean canUse() {
                return !FuwaEntity.this.isTame() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !FuwaEntity.this.isOrderedToSit() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Vuela cuando no está en modo quieto; al sentarse cae / se queda en el sitio.
        this.setNoGravity(!this.isOrderedToSit());

        if (this.level().isClientSide()) {
            this.updateIdleAnimationStates();
        }
    }

    private void updateIdleAnimationStates() {
        if (this.tickCount < this.idlePhaseEndTick) {
            return;
        }

        if (this.idlePlayingGesture) {
            // Terminó el gesto -> pausa quieta 3–5 s
            this.stopAmbientIdleStates();
            this.idlePlayingGesture = false;
            this.idlePhaseEndTick = this.tickCount + 60 + this.random.nextInt(40);
            return;
        }

        // Terminó la pausa -> siempre un gesto (mirar o tilt)
        this.stopAmbientIdleStates();
        int roll = this.random.nextInt(4);
        switch (roll) {
            case 0 -> {
                this.idleLeftState.start(this.tickCount);
                this.idlePhaseEndTick = this.tickCount + IDLE_GESTURE_TICKS;
            }
            case 1 -> {
                this.idleRightState.start(this.tickCount);
                this.idlePhaseEndTick = this.tickCount + IDLE_GESTURE_TICKS;
            }
            case 2 -> {
                this.tiltLeftState.start(this.tickCount);
                this.idlePhaseEndTick = this.tickCount + IDLE_TILT_TICKS;
            }
            default -> {
                this.tiltRightState.start(this.tickCount);
                this.idlePhaseEndTick = this.tickCount + IDLE_TILT_TICKS;
            }
        }
        this.idlePlayingGesture = true;
    }

    private void stopAmbientIdleStates() {
        this.idleLeftState.stop();
        this.idleRightState.stop();
        this.tiltLeftState.stop();
        this.tiltRightState.stop();
    }

    private void playOneShotIdleInterrupt(AnimationState state, int durationTicks) {
        this.stopAmbientIdleStates();
        this.nodState.stop();
        state.start(this.tickCount);
        this.idlePlayingGesture = true;
        this.idlePhaseEndTick = this.tickCount + durationTicks;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8D));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
            } else {
                float friction = 0.91F;
                float speed = this.onGround() ? this.getSpeed() * 0.21600002F : this.getFlyingSpeed();
                this.moveRelative(speed, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(friction));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    protected float getFlyingSpeed() {
        return this.isSprinting() ? 0.1F : 0.05F;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround() && !this.isOrderedToSit();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // No fall damage / no fall particles while floating.
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        FuwaEntity baby = ModEntities.FUWA.get().create(level);
        if (baby != null && this.getOwnerUUID() != null) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true);
        }
        return baby;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTame()) {
            if (stack.is(ModItems.STELLAR_DONUT.get())) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                if (!this.level().isClientSide()) {
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.level().broadcastEntityEvent(this, EVENT_NOD);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            return InteractionResult.PASS;
        }

        if (this.isOwnedBy(player)) {
            if (stack.is(ModItems.STELLAR_DONUT.get()) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.heal(4.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            if (!this.level().isClientSide()) {
                boolean sit = !this.isOrderedToSit();
                this.setOrderedToSit(sit);
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                this.setNoGravity(!sit);
                if (sit) {
                    this.level().broadcastEntityEvent(this, EVENT_NOD);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case EVENT_NOD -> this.playOneShotIdleInterrupt(this.nodState, 30);
            case EVENT_TILT_RIGHT -> this.playOneShotIdleInterrupt(this.tiltRightState, IDLE_TILT_TICKS);
            case EVENT_TILT_LEFT -> this.playOneShotIdleInterrupt(this.tiltLeftState, IDLE_TILT_TICKS);
            default -> super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return this.isTame() && super.canBeLeashed(player);
    }
}
