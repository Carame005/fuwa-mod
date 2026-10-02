package com.fuwa.entity.flight;

import com.fuwa.entity.PrunceEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public final class PrunceFlightHelper {
    public static final String FLIGHT_TAG = "fuwa_prunce_flight";

    private PrunceFlightHelper() {
    }

    public static boolean isCarryingPrunce(Player player) {
        return getMountedPrunce(player) != null;
    }

    public static PrunceEntity getMountedPrunce(Player player) {
        AABB box = player.getBoundingBox().inflate(2.5D, 3.0D, 2.5D);
        for (PrunceEntity prunce : player.level().getEntitiesOfClass(PrunceEntity.class, box)) {
            if (prunce.isOnHead() && prunce.isOwnedBy(player)) {
                return prunce;
            }
        }
        return null;
    }

    public static boolean hasFlightFlag(Player player) {
        return player.getPersistentData().getBoolean(FLIGHT_TAG);
    }

    public static void enableFlight(Player player) {
        CompoundTag data = player.getPersistentData();
        data.putBoolean(FLIGHT_TAG, true);

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }
        player.setNoGravity(true);
    }

    public static void disableFlight(Player player) {
        player.getPersistentData().remove(FLIGHT_TAG);
        player.setNoGravity(false);

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    public static void ensureFlightState(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        if (isCarryingPrunce(player)) {
            enableFlight(player);
        } else if (hasFlightFlag(player)) {
            disableFlight(player);
        }
    }
}
