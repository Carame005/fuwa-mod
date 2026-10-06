package com.fuwa.entity.client;

import com.fuwa.FuwaMod;
import com.fuwa.client.model.MeteoriteModel;
import com.fuwa.entity.MeteoriteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MeteoriteRenderer extends EntityRenderer<MeteoriteEntity> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/item/meteorite.png");

    private final MeteoriteModel model;

    public MeteoriteRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new MeteoriteModel(context.bakeLayer(MeteoriteModel.LAYER_LOCATION));
        this.shadowRadius = 0.6F;
    }

    @Override
    public ResourceLocation getTextureLocation(MeteoriteEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(MeteoriteEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float age = entity.tickCount + partialTicks;
        // Slow tumble so the fall reads clearly.
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 3.5F));
        poseStack.mulPose(Axis.XP.rotationDegrees(age * 5.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * 2.2F));

        // Slightly smaller than a full block so it reads as a rock, not a voxel.
        poseStack.scale(0.85F, 0.85F, 0.85F);

        VertexConsumer consumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
