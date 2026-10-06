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
 * Blockbench Milky Antenna model, adapted as a helmet overlay parented to the player head.
 * Uses an empty Humanoid skeleton (no default head cube) so only the antenna renders.
 */
@OnlyIn(Dist.CLIENT)
public class MilkyAntennaModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(FuwaMod.MOD_ID, "milky_antenna"), "main");

    public MilkyAntennaModel(ModelPart root) {
        super(root);
        this.setAllVisible(false);
        this.head.visible = true;
        this.hat.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        // Empty humanoid parts — avoid the default 8x8 head cube showing as armor.
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // BB export root was at y=24 (feet-space). Parented to head, that offset
        // places the headband (~y=-32 local) at the top of the head (~y=-8).
        PartDefinition milkyAntenna = head.addOrReplaceChild("milkyAntenna", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition antennaRight = milkyAntenna.addOrReplaceChild("antennaRight", CubeListBuilder.create(),
                PartPose.offset(0.0F, 2.0F, 1.0F));

        antennaRight.addOrReplaceChild("borders2", CubeListBuilder.create()
                        .texOffs(53, 52).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 56).addBox(1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 56).addBox(-3.0F, -1.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 58).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 46).addBox(1.0F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(47, 60).addBox(-2.0F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 38).addBox(1.0F, -1.0F, -6.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 40).addBox(-4.0F, -1.0F, -6.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 48).addBox(-5.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 50).addBox(4.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 60).addBox(3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 52).addBox(-4.0F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(55, 60).addBox(2.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 46).addBox(3.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 49).addBox(-4.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 56).addBox(-3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-9.0F, -20.0F, -7.0F, -1.5708F, 0.7854F, 0.0F));

        antennaRight.addOrReplaceChild("string", CubeListBuilder.create()
                        .texOffs(24, 22).addBox(-9.0F, -29.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 24).addBox(-8.0F, -30.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 25).addBox(-7.0F, -31.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 25).addBox(-6.0F, -32.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        antennaRight.addOrReplaceChild("halo", CubeListBuilder.create()
                        .texOffs(4, 59).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 59).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 58).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 58).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(-2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-10.0F, -28.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition antennaLeft = milkyAntenna.addOrReplaceChild("antennaLeft", CubeListBuilder.create(),
                PartPose.offsetAndRotation(2.0F, 2.0F, 3.0F, 0.0F, -1.5708F, 0.0F));

        antennaLeft.addOrReplaceChild("borders3", CubeListBuilder.create()
                        .texOffs(51, 57).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(55, 53).addBox(1.0F, -1.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(33, 58).addBox(-3.0F, -1.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(39, 58).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(41, 60).addBox(1.0F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 37).addBox(-2.0F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 41).addBox(1.0F, -1.0F, -6.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(51, 43).addBox(-4.0F, -1.0F, -6.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 39).addBox(-5.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 41).addBox(4.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(59, 43).addBox(3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(45, 61).addBox(-4.0F, -1.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(49, 61).addBox(2.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(33, 55).addBox(3.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(39, 55).addBox(-4.0F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(53, 61).addBox(-3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-9.0F, -20.0F, -7.0F, -1.5708F, 0.7854F, 0.0F));

        antennaLeft.addOrReplaceChild("string2", CubeListBuilder.create()
                        .texOffs(24, 26).addBox(-9.0F, -29.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 27).addBox(-8.0F, -30.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 27).addBox(-7.0F, -31.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 27).addBox(-6.0F, -32.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        antennaLeft.addOrReplaceChild("halo2", CubeListBuilder.create()
                        .texOffs(4, 59).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 59).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 58).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(4, 58).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 59).addBox(-2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -28.0F, -4.0F, 0.0F, 0.7854F, 0.0F));

        milkyAntenna.addOrReplaceChild("headband", CubeListBuilder.create()
                        .texOffs(42, 60).addBox(-5.0F, -32.0F, -5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(46, 45).addBox(4.0F, -32.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 51).addBox(-5.0F, -32.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(42, 43).addBox(-5.0F, -32.0F, 4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
