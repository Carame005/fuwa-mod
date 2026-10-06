package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.entity.CompanionProgress;
import com.fuwa.entity.MeteoriteEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

/**
 * Random meteorite event near the player that delivers a wild Fuwa or Prunce.
 * Skips players who already own both companions.
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class MeteoriteFallEvents {
    private static final String TAG_COOLDOWN = "FuwaMeteoriteCooldown";

    /** Ticks between chance rolls (~2 minutes). */
    private static final int CHECK_INTERVAL = 2400;
    /** Chance to trigger when a check succeeds. */
    private static final float EVENT_CHANCE = 0.22F;
    /** Cooldown after a meteorite (~8 minutes). */
    private static final int COOLDOWN_TICKS = 9600;
    /** Horizontal distance from the player. */
    private static final int MIN_RANGE = 10;
    private static final int MAX_RANGE = 28;
    /** Height above the ground surface. */
    private static final int MIN_HEIGHT = 32;
    private static final int MAX_HEIGHT = 48;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        var data = player.getPersistentData();
        int cooldown = data.getInt(TAG_COOLDOWN);
        if (cooldown > 0) {
            data.putInt(TAG_COOLDOWN, cooldown - 1);
            return;
        }

        if (player.tickCount % CHECK_INTERVAL != 0) {
            return;
        }
        if (CompanionProgress.hasBoth(player)) {
            return;
        }
        if (player.getRandom().nextFloat() > EVENT_CHANCE) {
            return;
        }

        if (trySpawnMeteorite(level, player, false, null)) {
            data.putInt(TAG_COOLDOWN, COOLDOWN_TICKS);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompanionProgress.copyOnClone(event.getOriginal(), event.getEntity());
        if (!event.isWasDeath()) {
            int cooldown = event.getOriginal().getPersistentData().getInt(TAG_COOLDOWN);
            if (cooldown > 0) {
                event.getEntity().getPersistentData().putInt(TAG_COOLDOWN, cooldown);
            }
        }
    }

    /**
     * Forces a meteorite near the player (ignores cooldown and ownership checks).
     *
     * @param spawnFuwa {@code true} for Fuwa, {@code false} for Prunce, {@code null} for auto
     * @return {@code false} only if the level is invalid
     */
    public static boolean forceMeteorite(ServerPlayer player, @Nullable Boolean spawnFuwa) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        player.getPersistentData().remove(TAG_COOLDOWN);
        return trySpawnMeteorite(level, player, true, spawnFuwa);
    }

    /**
     * @param forced    when true, ignores "has both" and allows any dimension
     * @param spawnFuwa override; {@code null} uses ownership / random rules
     */
    public static boolean trySpawnMeteorite(ServerLevel level, ServerPlayer player, boolean forced,
                                            @Nullable Boolean spawnFuwa) {
        boolean hasFuwa = CompanionProgress.hasFuwa(player);
        boolean hasPrunce = CompanionProgress.hasPrunce(player);
        if (!forced && hasFuwa && hasPrunce) {
            return false;
        }

        boolean chooseFuwa;
        if (spawnFuwa != null) {
            chooseFuwa = spawnFuwa;
        } else if (hasFuwa && !hasPrunce) {
            chooseFuwa = false;
        } else if (hasPrunce && !hasFuwa) {
            chooseFuwa = true;
        } else {
            chooseFuwa = player.getRandom().nextBoolean();
        }

        double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
        int range = MIN_RANGE + player.getRandom().nextInt(MAX_RANGE - MIN_RANGE + 1);
        double x = player.getX() + Math.cos(angle) * range;
        double z = player.getZ() + Math.sin(angle) * range;

        int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(x), (int) Math.floor(z));
        BlockPos groundPos = new BlockPos((int) Math.floor(x), groundY, (int) Math.floor(z));
        if (!level.canSeeSky(groundPos.above()) && !level.canSeeSky(groundPos)) {
            angle += Math.PI;
            x = player.getX() + Math.cos(angle) * range;
            z = player.getZ() + Math.sin(angle) * range;
            groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(x), (int) Math.floor(z));
        }

        int height = MIN_HEIGHT + player.getRandom().nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        double y = groundY + height;

        MeteoriteEntity meteorite = new MeteoriteEntity(level, x, y, z, chooseFuwa);
        level.addFreshEntity(meteorite);

        Component alert = Component.translatable("event.fuwa.meteorite.incoming")
                .withStyle(ChatFormatting.GOLD);
        for (Player nearby : level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(64.0D))) {
            nearby.displayClientMessage(alert, false);
        }

        return true;
    }
}
