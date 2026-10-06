package com.fuwa.entity;

import com.fuwa.item.CompanionCatchItem;
import com.fuwa.registry.ModEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

/**
 * Tracks whether a player already owns Fuwa / Prunce (tamed, stored in a catch item, or nearby).
 */
public final class CompanionProgress {
    public static final String TAG_TAMED_FUWA = "FuwaTamedFuwa";
    public static final String TAG_TAMED_PRUNCE = "FuwaTamedPrunce";

    private CompanionProgress() {
    }

    public static void markFuwaTamed(Player player) {
        player.getPersistentData().putBoolean(TAG_TAMED_FUWA, true);
    }

    public static void markPrunceTamed(Player player) {
        player.getPersistentData().putBoolean(TAG_TAMED_PRUNCE, true);
    }

    public static void copyOnClone(Player original, Player clone) {
        CompoundTag from = original.getPersistentData();
        CompoundTag to = clone.getPersistentData();
        if (from.getBoolean(TAG_TAMED_FUWA)) {
            to.putBoolean(TAG_TAMED_FUWA, true);
        }
        if (from.getBoolean(TAG_TAMED_PRUNCE)) {
            to.putBoolean(TAG_TAMED_PRUNCE, true);
        }
    }

    public static boolean hasFuwa(Player player) {
        return hasCompanion(player, ModEntities.FUWA.get(), TAG_TAMED_FUWA, true);
    }

    public static boolean hasPrunce(Player player) {
        return hasCompanion(player, ModEntities.PRUNCE.get(), TAG_TAMED_PRUNCE, false);
    }

    public static boolean hasBoth(Player player) {
        return hasFuwa(player) && hasPrunce(player);
    }

    private static boolean hasCompanion(Player player, EntityType<? extends TamableAnimal> type,
                                       String progressTag, boolean fuwaItem) {
        if (player.getPersistentData().getBoolean(progressTag)) {
            return true;
        }

        if (hasStoredInInventory(player, fuwaItem)) {
            // Persist so we do not re-scan forever after finding a filled catch item.
            player.getPersistentData().putBoolean(progressTag, true);
            return true;
        }

        if (player.level() instanceof ServerLevel serverLevel && hasOwnedNearby(serverLevel, player, type)) {
            player.getPersistentData().putBoolean(progressTag, true);
            return true;
        }

        return false;
    }

    private static boolean hasStoredInInventory(Player player, boolean fuwaItem) {
        for (ItemStack stack : player.getInventory().items) {
            if (!CompanionCatchItem.isFilled(stack)) {
                continue;
            }
            if (fuwaItem && stack.is(ModItems.TWINKLE_BOOK.get())) {
                return true;
            }
            if (!fuwaItem && stack.is(ModItems.PRUNCE_CAPSULE.get())) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (!CompanionCatchItem.isFilled(stack)) {
                continue;
            }
            if (fuwaItem && stack.is(ModItems.TWINKLE_BOOK.get())) {
                return true;
            }
            if (!fuwaItem && stack.is(ModItems.PRUNCE_CAPSULE.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasOwnedNearby(ServerLevel level, Player player,
                                          EntityType<? extends TamableAnimal> type) {
        AABB area = player.getBoundingBox().inflate(160.0D);
        for (Entity entity : level.getEntities(type, area, e -> true)) {
            if (entity instanceof TamableAnimal tame && tame.isTame() && tame.isOwnedBy(player)) {
                return true;
            }
        }
        return false;
    }
}
