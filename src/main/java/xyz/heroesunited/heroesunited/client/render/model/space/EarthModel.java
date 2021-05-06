package xyz.heroesunited.heroesunited.client.render.model.space;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;

public class EarthModel extends PlanetModel{

	public static final RenderMaterial EARTH_TEXTURE_MATERIAL = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(HeroesUnited.MODID,"planets/earth"));
	private final ModelRenderer earth;
	private final ModelRenderer clouds;
	private float counter = 0;

	public EarthModel() {
		super(RenderType::entityCutoutNoCull);
		texWidth = 128;
		texHeight = 128;

		earth = new ModelRenderer(this);
		earth.setPos(0.0F, 24.0F, 0.0F);
		earth.texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.0F, false);

		clouds = new ModelRenderer(this);
		clouds.setPos(0.0F, 0.0F, 0.0F);
		earth.addChild(clouds);
		clouds.texOffs(64, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 0.5F, false);

	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
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
		}
		earth.yRot = (float) (Math.toRadians(-counter));
	}
}