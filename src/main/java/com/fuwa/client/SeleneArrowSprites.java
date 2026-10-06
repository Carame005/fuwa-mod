package com.fuwa.client;

import com.fuwa.FuwaMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Selene arrow sprites: separate head + stick, each as crossed planes (0°/90°).
 * Tip points toward +X.
 */
public final class SeleneArrowSprites {
    public static final ResourceLocation HEAD_TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/item/selene_arrow_head.png");
    public static final ResourceLocation STICK_TEXTURE =
            new ResourceLocation(FuwaMod.MOD_ID, "textures/item/selene_arrow_stick.png");

    /** @deprecated use {@link #HEAD_TEXTURE} */
    public static final ResourceLocation TEXTURE = HEAD_TEXTURE;

    private SeleneArrowSprites() {
    }

    public static void renderArrow(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Star tip at +X — a bit larger for readability
        poseStack.pushPose();
        poseStack.translate(0.32F, 0.0F, 0.0F);
        renderCrossed(poseStack, buffer, packedLight, HEAD_TEXTURE, 0.30F, 0.30F);
        poseStack.popPose();

        // Shaft behind the tip
        poseStack.pushPose();
        poseStack.translate(-0.08F, 0.0F, 0.0F);
        renderCrossed(poseStack, buffer, packedLight, STICK_TEXTURE, 0.38F, 0.05F);
        poseStack.popPose();
    }

    public static void renderCrossed(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                     ResourceLocation texture, float halfLength, float halfWidth) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        renderPlane(poseStack, consumer, packedLight, halfLength, halfWidth);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        renderPlane(poseStack, consumer, packedLight, halfLength, halfWidth);
        poseStack.popPose();
    }

    private static void renderPlane(PoseStack poseStack, VertexConsumer consumer, int packedLight,
                                    float halfLength, float halfWidth) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        vertex(consumer, matrix, normal, halfLength, -halfWidth, 0.0F, 0.0F, 1.0F, packedLight, 0, 1, 0);
        vertex(consumer, matrix, normal, -halfLength, -halfWidth, 0.0F, 1.0F, 1.0F, packedLight, 0, 1, 0);
        vertex(consumer, matrix, normal, -halfLength, halfWidth, 0.0F, 1.0F, 0.0F, packedLight, 0, 1, 0);
        vertex(consumer, matrix, normal, halfLength, halfWidth, 0.0F, 0.0F, 0.0F, packedLight, 0, 1, 0);

        vertex(consumer, matrix, normal, halfLength, halfWidth, 0.0F, 0.0F, 0.0F, packedLight, 0, -1, 0);
        vertex(consumer, matrix, normal, -halfLength, halfWidth, 0.0F, 1.0F, 0.0F, packedLight, 0, -1, 0);
        vertex(consumer, matrix, normal, -halfLength, -halfWidth, 0.0F, 1.0F, 1.0F, packedLight, 0, -1, 0);
        vertex(consumer, matrix, normal, halfLength, -halfWidth, 0.0F, 0.0F, 1.0F, packedLight, 0, -1, 0);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                               float x, float y, float z, float u, float v, int packedLight,
                               float nx, float ny, float nz) {
        consumer.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, nx, ny, nz)
                .endVertex();
    }
}
