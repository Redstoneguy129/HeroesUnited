package xyz.heroesunited.heroesunited.client.render.model.space;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class EarthModel extends PlanetModel{

	public static final SpriteIdentifier EARTH_TEXTURE_MATERIAL = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(HeroesUnited.MODID,"planets/earth"));
	private final ModelPart earth;
	private float counter = 0;

	public EarthModel() {
		super(RenderLayer::getEntityCutoutNoCull);
		ModelData mesh = new ModelData();
		ModelPartData root = mesh.getRoot();
		ModelPartData venus = root.addChild("earth", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, false), ModelTransform.pivot(0, 24.0F, 0));
		venus.addChild("clouds", ModelPartBuilder.create().uv(64, 0).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.5F)), ModelTransform.NONE);
		this.earth = root.createPart(128, 128);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		earth.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void prepareModel(float partialTicks) {
		if(!MinecraftClient.getInstance().isPaused()){
			if (counter < 360) {
				counter += 0.05;
			} else {
				counter = 0;
			}
		}
		earth.yaw = (float) (Math.toRadians(-counter));
	}
}