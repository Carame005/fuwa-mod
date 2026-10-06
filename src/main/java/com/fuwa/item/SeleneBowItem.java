package com.fuwa.item;

import com.fuwa.client.SeleneBowClientExtensions;
import com.fuwa.entity.SeleneArrowEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Consumer;

/**
 * Infinite special bow: no ammo required. Fires {@link SeleneArrowEntity} with freeze splash on impact.
 */
public class SeleneBowItem extends Item {
    public static final int USE_DURATION = 72000;
    public static final int MIN_CHARGE_TICKS = 5;
    public static final int FULL_CHARGE_TICKS = 20;

    private static final float MIN_VELOCITY = 1.0F;
    private static final float MAX_VELOCITY = 3.0F;

    public SeleneBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            consumer.accept(SeleneBowClientExtensions.INSTANCE);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        int chargedTicks = getUseDuration(stack) - timeLeft;
        if (chargedTicks < MIN_CHARGE_TICKS) {
            return;
        }

        float power = getPowerForTime(chargedTicks);
        if (power < 0.1F) {
            return;
        }

        if (!level.isClientSide()) {
            SeleneArrowEntity arrow = new SeleneArrowEntity(level, player);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F,
                    Mth.lerp(power, MIN_VELOCITY, MAX_VELOCITY), 1.0F);
            arrow.setBaseDamage(2.0D + power * 2.0D);
            if (power >= 1.0F) {
                arrow.setCritArrow(true);
            }
            level.addFreshEntity(arrow);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

        player.awardStat(Stats.ITEM_USED.get(this));

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    public static float getPowerForTime(int charge) {
        float power = charge / (float) FULL_CHARGE_TICKS;
        power = (power * power + power * 2.0F) / 3.0F;
        return Math.min(power, 1.0F);
    }
}
