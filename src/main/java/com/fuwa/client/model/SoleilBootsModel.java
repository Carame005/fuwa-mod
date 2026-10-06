package com.fuwa.client.model;

import com.fuwa.FuwaMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Soleil Boots — each boot parented to its leg.
 * <p>
 * Blockbench exported the left boot as a translated copy of the right (not mirrored),
 * so world-space offsets looked fine in BB but drifted when parented to legs.
 * Here the left boot is X-mirrored and both are centered so the 4-wide cavity
 * wraps each vanilla leg (-2..2) without fusing into one block.
 * <p>
 * Texture atlas (64x64): cream (0,0)-(31,31), orange (32,0)-(63,31), yellow (0,32)-(31,63).
 */
@OnlyIn(Dist.CLIENT)
public class SoleilBootsModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(FuwaMod.MOD_ID, "soleil_boots"), "main");

    private static final int CREAM_U = 0;
    private static final int CREAM_V = 0;
    private static final int ORANGE_U = 32;
    private static final int ORANGE_V = 0;
    private static final int YELLOW_U = 0;
    private static final int YELLOW_V = 32;

    public SoleilBootsModel(ModelPart root) {
        super(root);
        this.setAllVisible(false);
        this.rightLeg.visible = true;
        this.leftLeg.visible = true;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition rightLeg = root.addOrReplaceChild("right_leg", CubeListBuilder.create(),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        // Cavity is x=-5..-1; offset 3 places it on the leg cube x=-2..2.
        PartDefinition rightBoot = rightLeg.addOrReplaceChild("rightBoot", buildRightBootCubes(),
                PartPose.offset(3.0F, 12.0F, 0.0F));
        rightBoot.addOrReplaceChild("outline", buildRightOutlineCubes(), PartPose.ZERO);

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        // Mirrored cavity is x=1..5; offset -3 places it on the leg cube x=-2..2.
        PartDefinition leftBoot = leftLeg.addOrReplaceChild("leftboot", buildLeftBootCubes(),
                PartPose.offset(-3.0F, 12.0F, 0.0F));
        leftBoot.addOrReplaceChild("outline2", buildLeftOutlineCubes(), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private static CubeListBuilder buildRightBootCubes() {
        return CubeListBuilder.create()
                .texOffs(CREAM_U, CREAM_V).addBox(-6.0F, -2.0F, -4.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(-5.0F, -1.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(-5.0F, -7.0F, -2.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(-6.0F, -3.0F, 3.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(-6.0F, -7.0F, 3.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(-1.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(-6.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(-5.0F, -2.0F, -5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(-6.0F, -3.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F));
    }

    private static CubeListBuilder buildLeftBootCubes() {
        // X-mirror of right: addBox(x,y,z,w,h,d) → addBox(-(x+w),y,z,w,h,d)
        return CubeListBuilder.create()
                .texOffs(CREAM_U, CREAM_V).addBox(0.0F, -2.0F, -4.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(1.0F, -1.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(0.0F, -2.0F, -3.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(1.0F, -7.0F, -2.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(0.0F, -3.0F, 3.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(0.0F, -7.0F, 3.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(0.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(5.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(CREAM_U, CREAM_V).addBox(1.0F, -2.0F, -5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(ORANGE_U, ORANGE_V).addBox(0.0F, -3.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F));
    }

    private static CubeListBuilder buildRightOutlineCubes() {
        return CubeListBuilder.create()
                .texOffs(YELLOW_U, YELLOW_V).addBox(0.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(-7.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(-6.0F, -8.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(-6.0F, -10.0F, 4.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(-1.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(-6.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F));
    }

    private static CubeListBuilder buildLeftOutlineCubes() {
        return CubeListBuilder.create()
                .texOffs(YELLOW_U, YELLOW_V).addBox(-1.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(6.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(0.0F, -8.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(0.0F, -10.0F, 4.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(0.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(YELLOW_U, YELLOW_V).addBox(5.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F));
    }
}
