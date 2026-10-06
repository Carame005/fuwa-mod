package com.fuwa.item;

import com.fuwa.client.CosmoShiningClientExtensions;
import com.fuwa.event.CosmoMistEvents;
import com.fuwa.registry.ModItems;
import com.fuwa.registry.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;
import java.util.function.Consumer;

/**
 * Cosmo Shining: crossbow-held spray weapon. Drains a charge bar while spraying;
 * leaves damaging mist clouds that stop at blocks. Reloads with Twinkle Imagination.
 */
public class CosmoShiningItem extends Item {
    public static final String TAG_CHARGE = "CosmoCharge";
    public static final int MAX_CHARGE = 100;
    public static final int USE_DURATION = 72000;

    private static final int CHARGE_DRAIN_PER_TICK = 1;
    private static final int SPRAY_INTERVAL = 2;
    private static final double SPRAY_RANGE = 8.0D;
    private static final double SPRAY_HIT_RADIUS = 1.4D;
    private static final float SPRAY_DAMAGE = 1.5F;
    private static final double MIST_RADIUS = 2.0D;
    private static final int MIST_DURATION_TICKS = 80;
    private static final int MIST_DAMAGE_INTERVAL = 20;
    private static final float MIST_DAMAGE = 2.0F;

    public CosmoShiningItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            consumer.accept(CosmoShiningClientExtensions.INSTANCE);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        // NONE avoids the crossbow *charging* first-person animation.
        // Charged *hold* pose is applied by CosmoShiningClientExtensions.
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (getCharge(stack) <= 0 && !tryReload(player, stack)) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 0.4F, 1.6F);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        int charged = getUseDuration(stack) - remainingUseDuration;
        if (charged <= 0) {
            return;
        }

        if (getCharge(stack) <= 0) {
            if (!level.isClientSide()) {
                if (!tryReload(player, stack)) {
                    player.stopUsingItem();
                }
            }
            return;
        }

        if (!level.isClientSide()) {
            setCharge(stack, getCharge(stack) - CHARGE_DRAIN_PER_TICK);
            if (getCharge(stack) <= 0) {
                setCharge(stack, 0);
                if (!tryReload(player, stack)) {
                    player.stopUsingItem();
                    return;
                }
            }
        }

        if (charged % SPRAY_INTERVAL != 0) {
            return;
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 tip = eye.add(look.scale(1.2D));
        Vec3 maxEnd = eye.add(look.scale(SPRAY_RANGE));
        Vec3 end = clipSprayEnd(level, player, tip, maxEnd);

        if (level.isClientSide()) {
            spawnSprayParticles(level, player, tip, end, look);
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        hurtAlongSpray(serverLevel, player, tip, end);
        CosmoMistEvents.spawn(serverLevel, player, end,
                MIST_RADIUS, MIST_DURATION_TICKS, MIST_DAMAGE_INTERVAL, MIST_DAMAGE);

        if (charged % 6 == 0) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.PLAYERS,
                    0.35F, 1.4F + level.random.nextFloat() * 0.3F);
        }
    }

    /**
     * Stops the spray at the first solid block along the look ray.
     */
    private static Vec3 clipSprayEnd(Level level, Player player, Vec3 from, Vec3 to) {
        BlockHitResult hit = level.clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK) {
            // Pull back slightly so mist sits just in front of the wall.
            Vec3 hitPos = hit.getLocation();
            Vec3 dir = to.subtract(from);
            double len = dir.length();
            if (len > 1.0E-4D) {
                return hitPos.subtract(dir.scale(0.15D / len));
            }
            return hitPos;
        }
        return to;
    }

    private static void spawnSprayParticles(Level level, Player player, Vec3 from, Vec3 to, Vec3 look) {
        // Triangles around the player while spraying.
        for (int i = 0; i < 4; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0D;
            double radius = 0.45D + level.random.nextDouble() * 0.55D;
            double px = player.getX() + Math.cos(angle) * radius;
            double py = player.getY() + 0.4D + level.random.nextDouble() * 1.2D;
            double pz = player.getZ() + Math.sin(angle) * radius;
            level.addParticle(ModParticles.COSMO_TRIANGLE.get(),
                    px, py, pz,
                    (level.random.nextDouble() - 0.5D) * 0.04D,
                    0.02D + level.random.nextDouble() * 0.03D,
                    (level.random.nextDouble() - 0.5D) * 0.04D);
        }

        // Blue mist along the spray path.
        for (int i = 0; i < 5; i++) {
            double t = level.random.nextDouble();
            Vec3 pos = from.lerp(to, t).add(
                    (level.random.nextDouble() - 0.5D) * 0.45D,
                    (level.random.nextDouble() - 0.5D) * 0.45D,
                    (level.random.nextDouble() - 0.5D) * 0.45D);
            level.addParticle(ModParticles.COSMO_BLUE_MIST.get(),
                    pos.x, pos.y, pos.z,
                    look.x * 0.12D + (level.random.nextDouble() - 0.5D) * 0.03D,
                    look.y * 0.12D + (level.random.nextDouble() - 0.5D) * 0.03D,
                    look.z * 0.12D + (level.random.nextDouble() - 0.5D) * 0.03D);
        }
    }

    private static void hurtAlongSpray(ServerLevel level, Player owner, Vec3 from, Vec3 to) {
        AABB sweep = new AABB(from, to).inflate(SPRAY_HIT_RADIUS + 0.5D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, sweep,
                entity -> entity.isAlive()
                        && entity != owner
                        && !entity.isAlliedTo(owner));

        Vec3 segment = to.subtract(from);
        double lengthSq = segment.lengthSqr();

        for (LivingEntity target : targets) {
            Vec3 center = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            double radius = SPRAY_HIT_RADIUS + Math.max(target.getBbWidth(), target.getBbHeight()) * 0.3D;

            double t = lengthSq < 1.0E-6D ? 0.0D
                    : Mth.clamp(center.subtract(from).dot(segment) / lengthSq, 0.0D, 1.0D);
            Vec3 closest = from.add(segment.scale(t));
            if (closest.distanceToSqr(center) > radius * radius) {
                continue;
            }

            target.hurt(level.damageSources().playerAttack(owner), SPRAY_DAMAGE);
            level.sendParticles(ModParticles.COSMO_BLUE_MIST.get(),
                    target.getX(), target.getY(0.5D), target.getZ(),
                    3, 0.2D, 0.25D, 0.2D, 0.02D);
            level.sendParticles(ModParticles.COSMO_TRIANGLE.get(),
                    target.getX(), target.getY(0.55D), target.getZ(),
                    2, 0.2D, 0.2D, 0.2D, 0.01D);
        }
    }

    public static boolean tryReload(Player player, ItemStack weapon) {
        if (player.getAbilities().instabuild) {
            setCharge(weapon, MAX_CHARGE);
            return true;
        }

        ItemStack ammo = findReloadAmmo(player);
        if (ammo.isEmpty()) {
            return false;
        }

        ammo.shrink(1);
        setCharge(weapon, MAX_CHARGE);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.7F, 1.35F);
        return true;
    }

    private static ItemStack findReloadAmmo(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.TWINKLE_IMAGINATION.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_CHARGE)) {
            return MAX_CHARGE;
        }
        return Mth.clamp(tag.getInt(TAG_CHARGE), 0, MAX_CHARGE);
    }

    public static void setCharge(ItemStack stack, int charge) {
        stack.getOrCreateTag().putInt(TAG_CHARGE, Mth.clamp(charge, 0, MAX_CHARGE));
    }

    public static float getChargeFraction(ItemStack stack) {
        return getCharge(stack) / (float) MAX_CHARGE;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCharge(stack) < MAX_CHARGE;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getChargeFraction(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fraction = getChargeFraction(stack);
        return Mth.hsvToRgb(0.55F + fraction * 0.08F, 0.7F, 1.0F);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
}
