package com.fuwa.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public class StarPunchClientExtensions implements IClientItemExtensions {
    public static final StarPunchClientExtensions INSTANCE = new StarPunchClientExtensions();

    /**
     * Arm stretched straight forward while charging, like a held punch.
     */
    public static final HumanoidModel.ArmPose PUNCH_CHARGE = HumanoidModel.ArmPose.create(
            "fuwa_star_punch",
            false,
            (model, entity, arm) -> {
                ModelPart modelArm = arm == HumanoidArm.LEFT ? model.leftArm : model.rightArm;
                // -90deg: arm points forward from the shoulder
                modelArm.xRot = -((float) Math.PI / 2.0F);
                modelArm.yRot = arm == HumanoidArm.LEFT ? 0.12F : -0.12F;
                modelArm.zRot = 0.0F;
            }
    );

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (entity.isUsingItem() && entity.getUsedItemHand() == hand) {
            return PUNCH_CHARGE;
        }
        return null;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                           ItemStack itemInHand, float partialTick, float equipProcess,
                                           float swingProcess) {
        if (!player.isUsingItem() || player.getUsedItemHand() !=
                (arm == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)) {
            return false;
        }

        // Hold the star out in front of the camera, lower so it doesn't cover the crosshair.
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(side * 0.42F, -0.55F + equipProcess * -0.4F, -0.85F);
        poseStack.mulPose(Axis.YP.rotationDegrees(side * 8.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-18.0F));
        return true;
    }
}
