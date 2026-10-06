package com.fuwa.client;

import com.fuwa.FuwaMod;
import com.fuwa.item.CosmoShiningItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client helpers for Cosmo Shining. Keeps full move speed while spraying.
 * Charge is shown via the item bar on the stack, not the Star Punch HUD textures.
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, value = Dist.CLIENT)
public class CosmoShiningChargeHud {
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!event.getEntity().isUsingItem()) {
            return;
        }
        if (!(event.getEntity().getUseItem().getItem() instanceof CosmoShiningItem)) {
            return;
        }

        event.getInput().leftImpulse *= 5.0F;
        event.getInput().forwardImpulse *= 5.0F;
    }
}
