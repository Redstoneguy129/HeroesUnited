package xyz.heroesunited.heroesunited.client.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.model.space.EarthModel;

public class EarthRenderer extends PlanetRenderer {
    public EarthRenderer(ModelPart planet) {
        super(new EarthModel(planet));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(HeroesUnited.MODID,"textures/planets/earth.png");
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        matrixStack.scale(0.95F, 0.95F, 0.95F);
        matrixStack.translate(0,-1,0);
        VertexConsumer buffer = EarthModel.EARTH_TEXTURE_MATERIAL.buffer(buffers, RenderType::entityTranslucent);
        planetModel.prepareModel(partialTicks);
        planetModel.renderToBuffer(matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.entityTranslucent(getTextureLocation());
    }
}
