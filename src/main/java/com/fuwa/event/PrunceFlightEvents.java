package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.entity.PrunceEntity;
import com.fuwa.entity.flight.PrunceFlightHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class PrunceFlightEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }

        PrunceFlightHelper.ensureFlightState(event.player);

        if (PrunceFlightHelper.isCarryingPrunce(event.player)) {
            event.player.fallDistance = 0.0F;
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        // Vanilla divides mining speed by 5 while airborne; cancel that while riding Prunce.
        if (PrunceFlightHelper.isCarryingPrunce(player) && !player.onGround()) {
            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        dismountOrClear(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        dismountOrClear(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            dismountOrClear(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            PrunceFlightHelper.disableFlight(event.getEntity());
        }
    }

    private static void dismountOrClear(Player player) {
        PrunceEntity prunce = PrunceFlightHelper.getMountedPrunce(player);
        if (prunce != null) {
            prunce.dismountFromHead();
        } else {
            PrunceFlightHelper.disableFlight(player);
        }
    }
}
