package com.fuwa.item;

import com.fuwa.entity.PrunceEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Catch/release item for a tameable companion (Prunce capsule, Star Twinkle Book, etc.).
 */
public class CompanionCatchItem extends Item {
    public static final String TAG_STORED = "StoredEntity";

    private final Supplier<? extends EntityType<? extends TamableAnimal>> entityType;
    private final String emptyTooltipKey;
    private final String filledTooltipKey;

    public CompanionCatchItem(Properties properties,
                              Supplier<? extends EntityType<? extends TamableAnimal>> entityType,
                              String emptyTooltipKey,
                              String filledTooltipKey) {
        super(properties);
        this.entityType = entityType;
        this.emptyTooltipKey = emptyTooltipKey;
        this.filledTooltipKey = filledTooltipKey;
    }

    public EntityType<? extends TamableAnimal> getCompanionType() {
        return this.entityType.get();
    }

    public static boolean isFilled(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_STORED);
    }

    @Nullable
    public static CompoundTag getStoredEntity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_STORED) ? tag.getCompound(TAG_STORED) : null;
    }

    public static void setStoredEntity(ItemStack stack, CompoundTag entityData) {
        stack.getOrCreateTag().put(TAG_STORED, entityData);
    }

    public static void clearStoredEntity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(TAG_STORED);
            if (tag.isEmpty()) {
                stack.setTag(null);
            }
        }
    }

    /**
     * Server-side capture used by {@link com.fuwa.event.CompanionCatchEvents}.
     * Mutates the held stack in place when possible so the item never "vanishes".
     */
    public boolean tryCapture(Player player, LivingEntity target, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isFilled(stack) || target.getType() != this.entityType.get()) {
            return false;
        }
        if (!(target instanceof TamableAnimal tame) || !tame.isTame() || !tame.isOwnedBy(player)) {
            return false;
        }
        if (player.level().isClientSide()) {
            return true;
        }

        if (tame instanceof PrunceEntity prunce && prunce.isOnHead()) {
            prunce.dismountFromHead();
        }

        CompoundTag entityData = new CompoundTag();
        tame.saveWithoutId(entityData);
        entityData.remove("UUID");
        entityData.putBoolean("OnHead", false);
        entityData.putString("id", EntityType.getKey(tame.getType()).toString());

        giveFilledStack(player, hand, stack, entityData);

        tame.discard();
        player.level().gameEvent(player, GameEvent.ENTITY_INTERACT, tame.position());
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.broadcastChanges();
        }
        return true;
    }

    private void giveFilledStack(Player player, InteractionHand hand, ItemStack handStack, CompoundTag entityData) {
        if (player.getAbilities().instabuild) {
            ItemStack filled = new ItemStack(this);
            setStoredEntity(filled, entityData);
            if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            return;
        }

        // Survival: write NBT onto the single held item so the slot never goes empty.
        if (handStack.getCount() == 1) {
            setStoredEntity(handStack, entityData);
            return;
        }

        handStack.shrink(1);
        ItemStack filled = new ItemStack(this);
        setStoredEntity(filled, entityData);
        if (!player.getInventory().add(filled)) {
            player.drop(filled, false);
        }
    }

    // Capture is handled by CompanionCatchEvents (EntityInteract) to avoid hand-stack loss.
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (!isFilled(stack) || player == null) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = level.getBlockState(clicked);
        BlockPos spawnPos = state.getCollisionShape(level, clicked).isEmpty() ? clicked : clicked.relative(face);

        if (!releaseCompanion((ServerLevel) level, stack, spawnPos, player, context.getHand())) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    private boolean releaseCompanion(ServerLevel level, ItemStack stack, BlockPos pos, Player player, InteractionHand hand) {
        CompoundTag stored = getStoredEntity(stack);
        if (stored == null) {
            return false;
        }

        TamableAnimal tame = this.entityType.get().create(level);
        if (tame == null) {
            return false;
        }

        tame.load(stored);
        tame.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), 0.0F);
        tame.setOwnerUUID(player.getUUID());
        tame.setTame(true);
        tame.setOrderedToSit(false);

        level.addFreshEntity(tame);
        level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);

        ItemStack handStack = player.getItemInHand(hand);
        if (player.getAbilities().instabuild) {
            // Keep filled item in creative.
        } else if (handStack.getCount() == 1 && isFilled(handStack)) {
            clearStoredEntity(handStack);
        } else {
            handStack.shrink(1);
            ItemStack empty = new ItemStack(this);
            if (!player.getInventory().add(empty)) {
                player.drop(empty, false);
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.broadcastChanges();
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isFilled(stack)) {
            tooltip.add(Component.translatable(this.filledTooltipKey).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable(this.emptyTooltipKey).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
