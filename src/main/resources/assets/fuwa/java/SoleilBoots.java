// Made with Blockbench 5.2.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class SoleilBoots<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "soleilboots"), "main");
	private final ModelPart rightBoot;
	private final ModelPart outline;
	private final ModelPart leftboot;
	private final ModelPart outline2;

	public SoleilBoots(ModelPart root) {
		this.rightBoot = root.getChild("rightBoot");
		this.outline = this.rightBoot.getChild("outline");
		this.leftboot = root.getChild("leftboot");
		this.outline2 = this.leftboot.getChild("outline2");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition rightBoot = partdefinition.addOrReplaceChild("rightBoot", CubeListBuilder.create().texOffs(-3, 1).addBox(-6.0F, -2.0F, -4.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 2).addBox(-5.0F, -1.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(50, 1).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(-1, 1).addBox(-5.0F, -7.0F, -2.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 2).addBox(-6.0F, -3.0F, 3.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 8).addBox(-6.0F, -7.0F, 3.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(-2, -3).addBox(-1.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(-2, -3).addBox(-6.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(-1, 1).addBox(-5.0F, -2.0F, -5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 1).addBox(-6.0F, -3.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 24.0F, 0.0F));

		PartDefinition outline = rightBoot.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 53).addBox(0.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 53).addBox(-7.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(3, 60).addBox(-6.0F, -8.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 60).addBox(-6.0F, -10.0F, 4.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 57).addBox(-1.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 57).addBox(-6.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftboot = partdefinition.addOrReplaceChild("leftboot", CubeListBuilder.create().texOffs(-3, 1).addBox(-6.0F, -2.0F, -4.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 1).addBox(-6.0F, -2.0F, -3.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(-1, 1).addBox(-5.0F, -7.0F, -2.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(52, 2).addBox(-6.0F, -3.0F, 3.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 8).addBox(-6.0F, -7.0F, 3.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(-2, -3).addBox(-1.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(-2, -3).addBox(-6.0F, -7.0F, -2.0F, 1.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(-1, 1).addBox(-5.0F, -2.0F, -5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 1).addBox(-6.0F, -3.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 2).addBox(-5.0F, -1.0F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, 0.0F));

		PartDefinition outline2 = leftboot.addOrReplaceChild("outline2", CubeListBuilder.create().texOffs(0, 53).addBox(0.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 53).addBox(-7.0F, -10.0F, -3.0F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(3, 60).addBox(-6.0F, -8.0F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(3, 60).addBox(-6.0F, -10.0F, 4.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 57).addBox(-1.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 57).addBox(-6.0F, -9.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		rightBoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		leftboot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}