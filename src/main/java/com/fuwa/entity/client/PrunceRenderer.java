package com.fuwa.entity.client;

import com.fuwa.entity.PrunceEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrunceRenderer extends GeoEntityRenderer<PrunceEntity> {
    public PrunceRenderer(EntityRendererProvider.Context context) {
        super(context, new PrunceModel());
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(PrunceEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.85F, 0.85F, 0.85F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
