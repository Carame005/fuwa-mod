package com.fuwa.event;

import com.fuwa.FuwaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reverts temporary magma platforms created by Soleil Boots back into lava.
 */
@Mod.EventBusSubscriber(modid = FuwaMod.MOD_ID)
public class SoleilLavaWalkEvents {
    private static final List<PendingRevert> PENDING = new ArrayList<>();

    public static void scheduleRevert(Level level, BlockPos pos, int delayTicks) {
        if (level.isClientSide()) {
            return;
        }
        PENDING.add(new PendingRevert(level.dimension(), pos.immutable(), delayTicks));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty()) {
            return;
        }

        Iterator<PendingRevert> iterator = PENDING.iterator();
        while (iterator.hasNext()) {
            PendingRevert pending = iterator.next();
            pending.ticksLeft--;
            if (pending.ticksLeft > 0) {
                continue;
            }

            Level level = event.getServer().getLevel(pending.dimension);
            if (level != null && level.getBlockState(pending.pos).is(Blocks.MAGMA_BLOCK)) {
                level.setBlockAndUpdate(pending.pos, Blocks.LAVA.defaultBlockState());
            }
            iterator.remove();
        }
    }

    private static final class PendingRevert {
        private final ResourceKey<Level> dimension;
        private final BlockPos pos;
        private int ticksLeft;

        private PendingRevert(ResourceKey<Level> dimension, BlockPos pos, int ticksLeft) {
            this.dimension = dimension;
            this.pos = pos;
            this.ticksLeft = ticksLeft;
        }
    }
}
