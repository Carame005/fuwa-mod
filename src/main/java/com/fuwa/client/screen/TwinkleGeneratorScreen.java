package com.fuwa.client.screen;

import com.fuwa.FuwaMod;
import com.fuwa.menu.TwinkleGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TwinkleGeneratorScreen extends AbstractContainerScreen<TwinkleGeneratorMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/gui/twinkle_generator.png");

    public TwinkleGeneratorScreen(TwinkleGeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 133;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelY = 38;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        // Explicit 256x256 atlas size — the 6-arg blit defaults to that and breaks smaller textures.
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
