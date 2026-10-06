package com.fuwa.client;

import com.fuwa.FuwaMod;
import com.fuwa.item.StarPunchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID, value = Dist.CLIENT)
public class StarPunchChargeHud {
    private static final ResourceLocation[] CHARGE_BAR = {
            new ResourceLocation(FuwaMod.MOD_ID, "textures/hud/charge_bar_0.png"),
            new ResourceLocation(FuwaMod.MOD_ID, "textures/hud/charge_bar_1.png"),
            new ResourceLocation(FuwaMod.MOD_ID, "textures/hud/charge_bar_2.png"),
            new ResourceLocation(FuwaMod.MOD_ID, "textures/hud/charge_bar_3.png"),
            new ResourceLocation(FuwaMod.MOD_ID, "textures/hud/charge_bar_4.png")
    };

    private static final int TEX_WIDTH = 64;
    private static final int TEX_HEIGHT = 32;
    /** Drawn size — close to native so it stays subtle above the hotbar. */
    private static final int DRAW_WIDTH = 48;
    private static final int DRAW_HEIGHT = 24;
    /** Pixels above the bottom of the screen (hotbar sits near the bottom). */
    private static final int BOTTOM_OFFSET = 48;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (!player.isUsingItem()) {
            return;
        }

        ItemStack useItem = player.getUseItem();
        if (!(useItem.getItem() instanceof StarPunchItem)) {
            return;
        }

        int chargedTicks = useItem.getUseDuration() - player.getUseItemRemainingTicks();
        int stage = getChargeStage(chargedTicks);

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        int x = (screenWidth - DRAW_WIDTH) / 2;
        int y = screenHeight - BOTTOM_OFFSET;

        graphics.blit(
                CHARGE_BAR[stage],
                x, y,
                DRAW_WIDTH, DRAW_HEIGHT,
                0.0F, 0.0F,
                TEX_WIDTH, TEX_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT
        );
    }

    /**
     * Vanilla multiplies move input by 0.2 while using any item (after this event).
     * Pre-scale by 5 so charging Star Punch keeps full walk/sprint speed.
     */
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!event.getEntity().isUsingItem()) {
            return;
        }
        if (!(event.getEntity().getUseItem().getItem() instanceof StarPunchItem)) {
            return;
        }

        event.getInput().leftImpulse *= 5.0F;
        event.getInput().forwardImpulse *= 5.0F;
    }

    private static int getChargeStage(int chargedTicks) {
        float fraction = StarPunchItem.getChargeFraction(chargedTicks);
        return Mth.clamp(Math.round(fraction * 4.0F), 0, 4);
    }
}
