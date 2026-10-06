package com.fuwa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Idle: vanilla item hand pose.
 * While right-click spraying: charged crossbow hold (not the charging animation).
 */
@OnlyIn(Dist.CLIENT)
public class CosmoShiningClientExtensions implements IClientItemExtensions {
    public static final CosmoShiningClientExtensions INSTANCE = new CosmoShiningClientExtensions();

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (entity.isUsingItem() && entity.getUsedItemHand() == hand) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        }
        return null;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                           ItemStack itemInHand, float partialTick, float equipProcess,
                                           float swingProcess) {
        InteractionHand hand = arm == player.getMainArm()
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;

        if (!player.isUsingItem() || player.getUsedItemHand() != hand) {
            return false;
        }

        int side = arm == HumanoidArm.RIGHT ? 1 : -1;

        // Vanilla applyItemArmTransform
        poseStack.translate(side * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);

        // Vanilla charged-crossbow hold (not the pull/charge animation)
        if (swingProcess < 0.001F) {
            poseStack.translate(side * -0.641864F, 0.0F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(side * 10.0F));
        }

        return true;
    }
}
