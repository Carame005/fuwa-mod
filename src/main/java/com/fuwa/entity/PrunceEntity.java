package com.fuwa.entity;

import com.fuwa.entity.flight.PrunceFlightHelper;
import com.fuwa.item.CompanionCatchItem;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class PrunceEntity extends TamableAnimal implements GeoEntity, FlyingAnimal {
    private static final EntityDataAccessor<Boolean> DATA_ON_HEAD =
            SynchedEntityData.defineId(PrunceEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation ANIM_JUMP = RawAnimation.begin().thenPlay("jump_on_ufo");
    private static final RawAnimation ANIM_UFO_SPIN = RawAnimation.begin().thenLoop("ufo_spin_idle");
    private static final RawAnimation ANIM_ANTENNA_LEFT = RawAnimation.begin().thenPlay("left_antenna_move_down_up");
    private static final RawAnimation ANIM_ANTENNA_RIGHT = RawAnimation.begin().thenPlay("right_antenna_move_down_up");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int gestureCooldown;

    public PrunceEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.gestureCooldown = 40 + this.random.nextInt(40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FLYING_SPEED, 0.55D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ON_HEAD, false);
    }

    public boolean isOnHead() {
        return this.entityData.get(DATA_ON_HEAD);
    }

    private void setOnHead(boolean onHead) {
        this.entityData.set(DATA_ON_HEAD, onHead);
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
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.2D, 5.0F, 1.5F, true) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isOnHead() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !PrunceEntity.this.isOnHead() && super.canContinueToUse();
            }
        });
        // Wild Prunce follow players holding a Stellar Donut.
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.15D, Ingredient.of(ModItems.STELLAR_DONUT.get()), false) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isTame()
                        && !PrunceEntity.this.isOnHead()
                        && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !PrunceEntity.this.isTame()
                        && !PrunceEntity.this.isOnHead()
                        && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(4, new PanicGoal(this, 1.4D) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isTame() && !PrunceEntity.this.isOnHead() && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isOrderedToSit()
                        && !PrunceEntity.this.isOnHead()
                        && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isOnHead() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !PrunceEntity.this.isOnHead() && super.canUse();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isOnHead()) {
            tickOnHead();
            return;
        }

        this.setNoGravity(!this.isOrderedToSit());
        this.noPhysics = false;

        // Antenna gestures only while sitting still (quieto).
        if (!this.level().isClientSide() && this.isOrderedToSit() && this.gestureCooldown-- <= 0) {
            this.gestureCooldown = 60 + this.random.nextInt(80);
            if (this.random.nextBoolean()) {
                this.triggerAnim("gesture", "antenna_left");
            } else {
                this.triggerAnim("gesture", "antenna_right");
            }
        }
    }

    private void tickOnHead() {
        LivingEntity owner = this.getOwner();
        if (!(owner instanceof Player player) || !player.isAlive() || !this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                this.dismountFromHead();
            }
            return;
        }

        this.noPhysics = true;
        this.setNoGravity(true);
        this.navigation.stop();
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(player.getX(), player.getY() + player.getBbHeight(), player.getZ());
        this.setYRot(player.yBodyRot);
        this.yRotO = this.getYRot();
        this.setXRot(0.0F);
        this.yBodyRot = player.yBodyRot;
        this.yHeadRot = player.yHeadRot;
    }

    @Override
    public void aiStep() {
        if (this.isOnHead()) {
            this.updateSwingTime();
            return;
        }
        super.aiStep();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isOnHead()) {
            this.calculateEntityAnimation(false);
            return;
        }

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
        return !this.onGround() && !this.isOrderedToSit() && !this.isOnHead();
    }

    @Override
    public boolean isPushable() {
        return !this.isOnHead() && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isOnHead() && super.canBeCollidedWith();
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // No fall damage while floating.
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
        PrunceEntity baby = ModEntities.PRUNCE.get().create(level);
        if (baby != null && this.getOwnerUUID() != null) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true);
        }
        return baby;
    }

    public void mountOnHead(Player player) {
        if (this.level().isClientSide()) {
            return;
        }

        this.setOrderedToSit(false);
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.setOnHead(true);
        this.triggerAnim("main", "jump");
        PrunceFlightHelper.enableFlight(player);
    }

    public void dismountFromHead() {
        if (this.level().isClientSide()) {
            return;
        }

        LivingEntity owner = this.getOwner();
        this.setOnHead(false);
        this.noPhysics = false;
        this.setOrderedToSit(false);
        this.navigation.stop();
        this.setNoGravity(true);
        this.triggerAnim("main", "jump");

        if (owner instanceof Player player) {
            PrunceFlightHelper.disableFlight(player);
            // Drop beside the owner so they can still interact if high up.
            this.setPos(player.getX(), player.getY(), player.getZ());
        }
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
                        CompanionProgress.markPrunceTamed(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.setOrderedToSit(true);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                        this.triggerAnim("main", "jump");
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
            return InteractionResult.PASS;
        }

        if (!this.isOwnedBy(player)) {
            return super.mobInteract(player, hand);
        }

        if (stack.is(ModItems.STELLAR_DONUT.get()) && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.heal(4.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // Shift + right-click: capsule capture runs first via the item.
        // If holding an empty Prunce Capsule, skip mounting so capture wins.
        if (player.isSecondaryUseActive() && !this.isOnHead()) {
            if (isHoldingEmptyPrunceCapsule(player, hand)) {
                return InteractionResult.PASS;
            }
            if (!this.level().isClientSide() && !this.isOrderedToSit()) {
                this.mountOnHead(player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // Interact while on head dismounts (triggered by Space on the client).
        if (this.isOnHead()) {
            if (!this.level().isClientSide()) {
                this.dismountFromHead();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // Right-click: toggle sit / follow.
        if (!this.level().isClientSide()) {
            boolean sit = !this.isOrderedToSit();
            this.setOrderedToSit(sit);
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            this.setNoGravity(!sit);
            this.gestureCooldown = 40 + this.random.nextInt(40);
            this.triggerAnim("main", "jump");
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    private static boolean isHoldingEmptyPrunceCapsule(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.is(ModItems.PRUNCE_CAPSULE.get()) && !CompanionCatchItem.isFilled(stack);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return this.isTame() && !this.isOnHead() && super.canBeLeashed(player);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.isOnHead()) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof Player player) {
                PrunceFlightHelper.disableFlight(player);
            }
        }
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("OnHead", this.isOnHead());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setOnHead(tag.getBoolean("OnHead"));
    }

    private PlayState mainPredicate(AnimationState<PrunceEntity> state) {
        // Jump is trigger-only on this controller.
        return PlayState.STOP;
    }

    private PlayState flightPredicate(AnimationState<PrunceEntity> state) {
        // Only spin while on the player's head or clearly airborne (not sitting / grounded).
        boolean shouldSpin = this.isOnHead()
                || (!this.isOrderedToSit() && !this.isInSittingPose() && !this.onGround());

        if (shouldSpin) {
            return state.setAndContinue(ANIM_UFO_SPIN);
        }

        // Looping anims can keep going after STOP unless the controller is reset.
        state.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0, this::mainPredicate)
                .triggerableAnim("jump", ANIM_JUMP));
        controllers.add(new AnimationController<>(this, "flight", 0, this::flightPredicate));
        controllers.add(new AnimationController<>(this, "gesture", 5, state -> PlayState.STOP)
                .triggerableAnim("antenna_left", ANIM_ANTENNA_LEFT)
                .triggerableAnim("antenna_right", ANIM_ANTENNA_RIGHT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
