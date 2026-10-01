package com.fuwa.entity.client;

import com.fuwa.FuwaMod;
import com.fuwa.entity.FuwaEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Blockbench model export (fuwa.java), adapted for HierarchicalModel animations.
 */
public class FuwaModel extends HierarchicalModel<FuwaEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(FuwaMod.MOD_ID, "fuwa"), "main");

    private final ModelPart root;
    private final ModelPart bodyRoot;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart legs;
    private final ModelPart tail;
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart ears;
    private final ModelPart ring1;
    private final ModelPart ring2;

    public FuwaModel(ModelPart root) {
        this.root = root;
        this.bodyRoot = root.getChild("root");
        this.body = this.bodyRoot.getChild("body");
        this.arms = this.bodyRoot.getChild("arms");
        this.legs = this.bodyRoot.getChild("legs");
        this.tail = this.bodyRoot.getChild("tail");
        this.upperBody = root.getChild("upperBody");
        this.head = this.upperBody.getChild("head");
        this.ears = this.upperBody.getChild("ears");
        this.ring1 = this.upperBody.getChild("ring1");
        this.ring2 = this.upperBody.getChild("ring2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -1.0F));

        root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, -7.0F, -4.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));

        root.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(3, 59).addBox(-4.0F, -2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 59).addBox(2.0F, -2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 1.0F));

        root.addOrReplaceChild("legs", CubeListBuilder.create().texOffs(4, 57).addBox(3.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 60).addBox(-5.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 1.0F));

        root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 1).addBox(-1.0F, -1.0F, 2.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(2, 29).addBox(-1.0F, -3.0F, 6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 1.0F));

        PartDefinition upperBody = partdefinition.addOrReplaceChild("upperBody", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));

        upperBody.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 0).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -1.0F));

        upperBody.addOrReplaceChild("ears", CubeListBuilder.create().texOffs(48, 0).addBox(-9.0F, -4.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 0).addBox(5.0F, -4.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition ring1 = upperBody.addOrReplaceChild("ring1", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 0.0F));

        ring1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(-3, 29).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(-3, 29).addBox(-6.0F, -1.0F, -6.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 2.0F, 0.0F, 0.0F, -0.6545F));

        ring1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 0.0F, -2.0F, 0.0F, 0.0F, -0.5672F));

        ring1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, -2.0F, 0.0F, 0.0F, -0.5672F));

        PartDefinition ring2 = upperBody.addOrReplaceChild("ring2", CubeListBuilder.create(), PartPose.offset(0.0F, -10.0F, 0.0F));

        ring2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(-3, 29).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(-3, 29).addBox(-6.0F, -1.0F, -5.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -1.0F, 1.0F, 0.0F, 0.0F, 0.6545F));

        ring2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 28).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -5.0F, -3.0F, 0.0F, 0.0F, 0.7418F));

        ring2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(-1, 27).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.0F, -1.0F, -3.0F, 0.0F, 0.0F, 0.6981F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(FuwaEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.idleLeftState, FuwaAnimations.HEAD_IDLE_LEFT, ageInTicks);
        this.animate(entity.idleRightState, FuwaAnimations.HEAD_IDLE_RIGHT, ageInTicks);
        this.animate(entity.nodState, FuwaAnimations.HEAD_IDLE_NOD, ageInTicks);
        this.animate(entity.tiltRightState, FuwaAnimations.HEAD_IDLE_TILT_RIGHT, ageInTicks);
        this.animate(entity.tiltLeftState, FuwaAnimations.HEAD_IDLE_TILT_LEFT, ageInTicks);
    }
}
