package com.fuwa.entity;

import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class FuwaEntity extends TamableAnimal implements GeoEntity, FlyingAnimal {
    private static final RawAnimation ANIM_NOD = RawAnimation.begin().thenPlay("head_idle_nod");
    private static final RawAnimation ANIM_TURN_LEFT = RawAnimation.begin().thenPlay("head_turn_left");
    private static final RawAnimation ANIM_TURN_RIGHT = RawAnimation.begin().thenPlay("head_turn_right");
    private static final RawAnimation ANIM_TILT_LEFT = RawAnimation.begin().thenPlay("head_tilt_left");
    private static final RawAnimation ANIM_TILT_RIGHT = RawAnimation.begin().thenPlay("head_tilt_right");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int gestureCooldown;

    public FuwaEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.gestureCooldown = 20 + this.random.nextInt(20);
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

        // Gestos de cabeza aleatorios con pausas (controlados desde el servidor).
        if (!this.level().isClientSide() && this.gestureCooldown-- <= 0) {
            this.gestureCooldown = 60 + this.random.nextInt(40);
            switch (this.random.nextInt(4)) {
                case 0 -> this.triggerAnim("gesture", "turn_left");
                case 1 -> this.triggerAnim("gesture", "turn_right");
                case 2 -> this.triggerAnim("gesture", "tilt_left");
                default -> this.triggerAnim("gesture", "tilt_right");
            }
        }
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
                        this.triggerAnim("gesture", "nod");
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

            // Shift + empty Star Twinkle Book is handled by the item (capture).
            if (player.isSecondaryUseActive()
                    && stack.is(ModItems.STAR_TWINKLE_BOOK.get())
                    && !com.fuwa.item.CompanionCatchItem.isFilled(stack)) {
                return InteractionResult.PASS;
            }

            if (!this.level().isClientSide()) {
                boolean sit = !this.isOrderedToSit();
                this.setOrderedToSit(sit);
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                this.setNoGravity(!sit);
                if (sit) {
                    this.triggerAnim("gesture", "nod");
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return this.isTame() && super.canBeLeashed(player);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "gesture", 5, state -> PlayState.STOP)
                .triggerableAnim("nod", ANIM_NOD)
                .triggerableAnim("turn_left", ANIM_TURN_LEFT)
                .triggerableAnim("turn_right", ANIM_TURN_RIGHT)
                .triggerableAnim("tilt_left", ANIM_TILT_LEFT)
                .triggerableAnim("tilt_right", ANIM_TILT_RIGHT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
