// Made with Blockbench 5.2.1
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class MilkyAntenna<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "milkyantenna"), "main");
	private final ModelPart milkyAntenna;
	private final ModelPart antennaRight;
	private final ModelPart borders2;
	private final ModelPart string;
	private final ModelPart halo;
	private final ModelPart antennaLeft;
	private final ModelPart borders3;
	private final ModelPart string2;
	private final ModelPart halo2;
	private final ModelPart headband;

	public MilkyAntenna(ModelPart root) {
		this.milkyAntenna = root.getChild("milkyAntenna");
		this.antennaRight = this.milkyAntenna.getChild("antennaRight");
		this.borders2 = this.antennaRight.getChild("borders2");
		this.string = this.antennaRight.getChild("string");
		this.halo = this.antennaRight.getChild("halo");
		this.antennaLeft = this.milkyAntenna.getChild("antennaLeft");
		this.borders3 = this.antennaLeft.getChild("borders3");
		this.string2 = this.antennaLeft.getChild("string2");
		this.halo2 = this.antennaLeft.getChild("halo2");
		this.headband = this.milkyAntenna.getChild("headband");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition milkyAntenna = partdefinition.addOrReplaceChild("milkyAntenna", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition antennaRight = milkyAntenna.addOrReplaceChild("antennaRight", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 1.0F));

		PartDefinition borders2 = antennaRight.addOrReplaceChild("borders2", CubeListBuilder.create().texOffs(53, 52).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
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
		.texOffs(59, 56).addBox(-3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -20.0F, -7.0F, -1.5708F, 0.7854F, 0.0F));

		PartDefinition string = antennaRight.addOrReplaceChild("string", CubeListBuilder.create().texOffs(24, 22).addBox(-9.0F, -29.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 24).addBox(-8.0F, -30.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 25).addBox(-7.0F, -31.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 25).addBox(-6.0F, -32.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition halo = antennaRight.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(4, 59).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 59).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 58).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(4, 58).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(-2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, -28.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition antennaLeft = milkyAntenna.addOrReplaceChild("antennaLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 2.0F, 3.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition borders3 = antennaLeft.addOrReplaceChild("borders3", CubeListBuilder.create().texOffs(51, 57).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
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
		.texOffs(53, 61).addBox(-3.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.0F, -20.0F, -7.0F, -1.5708F, 0.7854F, 0.0F));

		PartDefinition string2 = antennaLeft.addOrReplaceChild("string2", CubeListBuilder.create().texOffs(24, 26).addBox(-9.0F, -29.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(-8.0F, -30.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 27).addBox(-7.0F, -31.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(8, 27).addBox(-6.0F, -32.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition halo2 = antennaLeft.addOrReplaceChild("halo2", CubeListBuilder.create().texOffs(4, 59).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 59).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(4, 58).addBox(-3.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(4, 58).addBox(2.0F, -1.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(1.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(5, 59).addBox(-2.0F, -1.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -28.0F, -4.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition headband = milkyAntenna.addOrReplaceChild("headband", CubeListBuilder.create().texOffs(42, 60).addBox(-5.0F, -32.0F, -5.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(46, 45).addBox(4.0F, -32.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(42, 51).addBox(-5.0F, -32.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(42, 43).addBox(-5.0F, -32.0F, 4.0F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		milkyAntenna.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}