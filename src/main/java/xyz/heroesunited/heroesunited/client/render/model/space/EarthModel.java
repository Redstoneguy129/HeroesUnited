package xyz.heroesunited.heroesunited.client.render.model.space;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

public class EarthModel extends PlanetModel{
	private final ModelRenderer earth;
	private final ModelRenderer clouds;
	private final ModelRenderer moon;
	private float counter = 0;
	private float moonCounter = 0;

	public EarthModel() {
		super(RenderType::entityCutoutNoCull);
		texWidth = 128;
		texHeight = 64;

		earth = new ModelRenderer(this);
		earth.setPos(0.0F, 24.0F, 0.0F);
		earth.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);

		clouds = new ModelRenderer(this);
		clouds.setPos(0.0F, 0.0F, 0.0F);
		earth.addChild(clouds);
		clouds.texOffs(64, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.5F, false);

		moon = new ModelRenderer(this);
		moon.setPos(0.0F, 0.0F, 0.0F);
		earth.addChild(moon);
		moon.texOffs(0, 32).addBox(20.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, -6.0F, false);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		matrixStack.scale(3, 3, 3);
		matrixStack.translate(0,-1,0);
		earth.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	@Override
	public void prepareModel(float partialTicks) {
		if(!Minecraft.getInstance().isPaused()){
			if (counter < 360) {
				counter += 0.05;
			} else {
				counter = 0;
			}
			if (moonCounter < 360) {
				moonCounter += 0.03;
			} else {
				moonCounter = 0;
			}
		}
		earth.yRot = (float) (Math.toRadians(-counter));
		moon.yRot = (float) (Math.toRadians(moonCounter));
	}
}