package com.fuwa.menu;

import com.fuwa.block.entity.TwinkleGeneratorBlockEntity;
import com.fuwa.registry.ModBlocks;
import com.fuwa.registry.ModItems;
import com.fuwa.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class TwinkleGeneratorMenu extends AbstractContainerMenu {
    private final TwinkleGeneratorBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public TwinkleGeneratorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public TwinkleGeneratorMenu(int containerId, Inventory playerInventory, BlockEntity entity) {
        super(ModMenuTypes.TWINKLE_GENERATOR.get(), containerId);
        if (!(entity instanceof TwinkleGeneratorBlockEntity generator)) {
            throw new IllegalStateException("Expected TwinkleGeneratorBlockEntity");
        }
        this.blockEntity = generator;
        this.access = ContainerLevelAccess.create(generator.getLevel(), generator.getBlockPos());

        this.addSlot(new SlotItemHandler(generator.getItemHandler(), 0, 80, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.TWINKLE_IMAGINATION.get());
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public TwinkleGeneratorBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            final int generatorSlot = 0;
            final int invStart = 1;
            final int invEnd = 28;
            final int hotbarEnd = 37;

            if (index == generatorSlot) {
                if (!this.moveItemStackTo(stack, invStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModItems.TWINKLE_IMAGINATION.get())) {
                if (!this.moveItemStackTo(stack, generatorSlot, generatorSlot + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < invEnd) {
                if (!this.moveItemStackTo(stack, invEnd, hotbarEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, invStart, invEnd, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.TWINKLE_GENERATOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 109));
        }
    }
}
