package com.fuwa.event;

import com.fuwa.FuwaMod;
import com.fuwa.item.CompanionCatchItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles companion capture on Shift+right-click before sit/follow/mount logic runs.
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class CompanionCatchEvents {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof CompanionCatchItem catchItem) || CompanionCatchItem.isFilled(stack)) {
            return;
        }

        if (!(event.getTarget() instanceof net.minecraft.world.entity.LivingEntity living)) {
            return;
        }

        if (!catchItem.tryCapture(player, living, event.getHand())) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
    }
}
