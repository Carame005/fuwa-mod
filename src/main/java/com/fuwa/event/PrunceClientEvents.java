package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.entity.PrunceEntity;
import com.fuwa.entity.flight.PrunceFlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, value = Dist.CLIENT)
public class PrunceClientEvents {
    private static final double FLIGHT_SPEED = 0.35D;
    private static final double DESCEND_SPEED = 0.35D;
    private static final double DRAG = 0.90D;
    private static final String DISMOUNT_CD = "fuwa_prunce_dismount_cd";
    private static final String CLIENT_FLIGHT = "fuwa_prunce_client_flight";

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.isPaused()) {
            return;
        }

        boolean carrying = PrunceFlightHelper.isCarryingPrunce(player);
        if (carrying) {
            player.getPersistentData().putBoolean(CLIENT_FLIGHT, true);
            player.setNoGravity(true);
            player.fallDistance = 0.0F;

            Vec3 motion = player.getDeltaMovement();

            if (mc.options.keyUp.isDown()) {
                Vec3 look = player.getLookAngle();
                Vec3 target = look.scale(FLIGHT_SPEED);
                motion = new Vec3(
                        motion.x * 0.2D + target.x * 0.8D,
                        motion.y * 0.2D + target.y * 0.8D,
                        motion.z * 0.2D + target.z * 0.8D
                );
            } else {
                motion = motion.scale(DRAG);
            }

            // Shift = descend while flying.
            if (mc.options.keyShift.isDown()) {
                motion = new Vec3(motion.x, Math.min(motion.y, -DESCEND_SPEED), motion.z);
            }

            player.setDeltaMovement(motion);
            tryDismountWithSpace(mc, player);
        } else if (player.getPersistentData().getBoolean(CLIENT_FLIGHT)) {
            restoreWalking(player);
        }

        int cd = player.getPersistentData().getInt(DISMOUNT_CD);
        if (cd > 0) {
            player.getPersistentData().putInt(DISMOUNT_CD, cd - 1);
        }
    }

    private static void restoreWalking(LocalPlayer player) {
        player.getPersistentData().remove(CLIENT_FLIGHT);
        player.setNoGravity(false);
        player.fallDistance = 0.0F;

        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    private static void tryDismountWithSpace(Minecraft mc, LocalPlayer player) {
        PrunceEntity prunce = PrunceFlightHelper.getMountedPrunce(player);
        if (prunce == null || !prunce.isOwnedBy(player) || mc.screen != null || mc.gameMode == null) {
            return;
        }

        // Space = remove Prunce from head.
        if (mc.options.keyJump.isDown() && player.getPersistentData().getInt(DISMOUNT_CD) <= 0) {
            player.getPersistentData().putInt(DISMOUNT_CD, 10);
            mc.gameMode.interact(player, prunce, InteractionHand.MAIN_HAND);
            // Restore walking immediately; server sync confirms a tick later.
            restoreWalking(player);
        }
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!PrunceFlightHelper.isCarryingPrunce(event.getEntity())) {
            return;
        }

        // Space is for dismount, not vanilla fly-up. Shift is handled manually for descent.
        event.getInput().jumping = false;
        event.getInput().shiftKeyDown = false;
        event.getInput().leftImpulse = 0.0F;
        event.getInput().forwardImpulse = 0.0F;
    }
}
