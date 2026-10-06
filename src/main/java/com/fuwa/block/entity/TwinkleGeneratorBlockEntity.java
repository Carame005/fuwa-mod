package com.fuwa.block.entity;

import com.fuwa.entity.FuwaEntity;
import com.fuwa.menu.TwinkleGeneratorMenu;
import com.fuwa.registry.ModBlockEntities;
import com.fuwa.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TwinkleGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    /** Ticks between generations while a sitting tamed Fuwa is nearby (~30 seconds). */
    public static final int GENERATION_INTERVAL_TICKS = 20 * 30;
    /** Search radius (blocks) for a sitting tamed Fuwa. */
    public static final double FUWA_RANGE = 5.0D;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.is(ModItems.TWINKLE_IMAGINATION.get());
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int progress;

    public TwinkleGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TWINKLE_GENERATOR.get(), pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void drops() {
        Containers.dropContents(this.level, this.worldPosition, new net.minecraft.world.SimpleContainer(
                this.itemHandler.getStackInSlot(0).copy()
        ));
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TwinkleGeneratorBlockEntity be) {
        if (!be.hasSittingTamedFuwaNearby()) {
            return;
        }

        ItemStack slot = be.itemHandler.getStackInSlot(0);
        if (!slot.isEmpty() && slot.getCount() >= slot.getMaxStackSize()) {
            return;
        }

        be.progress++;
        if (be.progress < GENERATION_INTERVAL_TICKS) {
            return;
        }

        be.progress = 0;
        if (slot.isEmpty()) {
            be.itemHandler.setStackInSlot(0, new ItemStack(ModItems.TWINKLE_IMAGINATION.get()));
        } else if (slot.is(ModItems.TWINKLE_IMAGINATION.get())) {
            slot.grow(1);
            be.itemHandler.setStackInSlot(0, slot);
        }
        be.setChanged();
    }

    private boolean hasSittingTamedFuwaNearby() {
        if (this.level == null) {
            return false;
        }
        AABB area = new AABB(this.worldPosition).inflate(FUWA_RANGE);
        return !this.level.getEntitiesOfClass(FuwaEntity.class, area,
                fuwa -> fuwa.isTame() && fuwa.isOrderedToSit() && fuwa.isAlive()).isEmpty();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.fuwa.twinkle_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new TwinkleGeneratorMenu(containerId, playerInventory, this);
    }

    @Override
    public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyItemHandler = LazyOptional.of(() -> this.itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", this.itemHandler.serializeNBT());
        tag.putInt("progress", this.progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("progress");
    }
}
