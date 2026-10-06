package com.fuwa.entity.client;

import com.fuwa.client.SeleneArrowSprites;
import com.fuwa.entity.SeleneArrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Renders the Selene arrow as two crossed textured planes (0° and 90°) for a simple 3D look.
 */
public class SeleneArrowRenderer extends EntityRenderer<SeleneArrowEntity> {
    public SeleneArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SeleneArrowEntity entity) {
        return SeleneArrowSprites.HEAD_TEXTURE;
    }

    @Override
    public void render(SeleneArrowEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));

        SeleneArrowSprites.renderArrow(poseStack, buffer, packedLight);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
