package com.fuwa.item;

import com.fuwa.client.StarPunchClientExtensions;
import com.fuwa.event.StarPunchWaveEvents;
import com.fuwa.registry.ModParticles;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

public class StarPunchItem extends Item {
    public static final int MAX_CHARGE_TICKS = 40;
    public static final int USE_DURATION = 72000;
    public static final int COOLDOWN_TICKS = 10;

    private static final float MIN_DAMAGE = 2.0F;
    private static final float MAX_DAMAGE = 10.0F;
    private static final float MIN_KNOCKBACK = 0.3F;
    private static final float MAX_KNOCKBACK = 1.4F;

    private static final float MELEE_DAMAGE = 5.0F;
    private static final float MELEE_SPEED = -2.4F;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public StarPunchItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", MELEE_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier", MELEE_SPEED, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            consumer.accept(StarPunchClientExtensions.INSTANCE);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        // NONE avoids the bow draw pose; arm stretch is handled by StarPunchClientExtensions.
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
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
        if (charged <= 0 || charged % 4 != 0) {
            return;
        }

        float charge = getChargeFraction(charged);
        if (level.isClientSide()) {
            Vec3 look = player.getLookAngle();
            Vec3 eye = player.getEyePosition();
            double px = eye.x + look.x * 0.6D;
            double py = eye.y + look.y * 0.6D;
            double pz = eye.z + look.z * 0.6D;
            level.addParticle(ModParticles.STAR_PARTICLE.get(), px, py, pz,
                    (level.random.nextDouble() - 0.5D) * 0.06D * charge,
                    (level.random.nextDouble() - 0.5D) * 0.06D * charge,
                    (level.random.nextDouble() - 0.5D) * 0.06D * charge);
        } else if (charged % 10 == 0) {
            float pitch = 0.8F + charge * 0.6F;
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.35F, pitch);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player) || level.isClientSide()) {
            return;
        }

        int chargedTicks = getUseDuration(stack) - timeLeft;
        if (chargedTicks <= 0) {
            return;
        }

        float charge = getChargeFraction(chargedTicks);
        float damage = Mth.lerp(charge, MIN_DAMAGE, MAX_DAMAGE);
        float knockback = Mth.lerp(charge, MIN_KNOCKBACK, MAX_KNOCKBACK);

        // Start ahead of the camera so the wave doesn't sit in the player's face.
        Vec3 eyePos = player.getEyePosition().add(player.getLookAngle().scale(1.5D));
        Vec3 look = player.getLookAngle().normalize();

        StarPunchWaveEvents.spawn((ServerLevel) level, player, eyePos, look, damage, knockback);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 0.9F + charge * 0.3F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.55F, 1.2F + charge * 0.4F);

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    public static float getChargeFraction(int chargedTicks) {
        return Mth.clamp(chargedTicks / (float) MAX_CHARGE_TICKS, 0.0F, 1.0F);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }
}
