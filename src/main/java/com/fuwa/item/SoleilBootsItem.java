package com.fuwa.item;

import com.fuwa.FuwaMod;
import com.fuwa.client.SoleilBootsClientExtensions;
import com.fuwa.event.SoleilLavaWalkEvents;
import com.fuwa.registry.ModArmorMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SoleilBootsItem extends ArmorItem {
    private static final String ARMOR_TEXTURE = FuwaMod.MOD_ID + ":textures/item/soleil_boots.png";
    private static final int LAVA_WALK_RADIUS = 2;
    private static final int MAGMA_LIFETIME_TICKS = 60;

    public SoleilBootsItem(Properties properties) {
        // Boots slot (feet). Model covers lower legs + feet like tall boots.
        super(ModArmorMaterials.SOLEIL, Type.BOOTS, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            consumer.accept(SoleilBootsClientExtensions.INSTANCE);
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) {
            return;
        }

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, true));
        tryWalkOnLava(player, level);
    }

    /**
     * Frost-Walker style: replace nearby lava source blocks under the player with
     * temporary magma. Hold sneak to sink into lava instead.
     */
    private static void tryWalkOnLava(Player player, Level level) {
        if (player.isShiftKeyDown() || player.getAbilities().flying) {
            return;
        }

        BlockPos playerPos = player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int dx = -LAVA_WALK_RADIUS; dx <= LAVA_WALK_RADIUS; dx++) {
            for (int dz = -LAVA_WALK_RADIUS; dz <= LAVA_WALK_RADIUS; dz++) {
                cursor.set(playerPos.getX() + dx, playerPos.getY() - 1, playerPos.getZ() + dz);
                if (!cursor.closerToCenterThan(player.position(), LAVA_WALK_RADIUS + 0.5D)) {
                    continue;
                }

                BlockState state = level.getBlockState(cursor);
                FluidState fluid = state.getFluidState();
                if (!fluid.is(FluidTags.LAVA) || !fluid.isSource()) {
                    continue;
                }
                if (!level.getBlockState(cursor.above()).isAir()) {
                    continue;
                }

                BlockPos frozen = cursor.immutable();
                level.setBlockAndUpdate(frozen, Blocks.MAGMA_BLOCK.defaultBlockState());
                SoleilLavaWalkEvents.scheduleRevert(level, frozen, MAGMA_LIFETIME_TICKS);
            }
        }
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }
}
