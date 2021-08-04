package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.EarthModel;

public class EarthRenderer extends PlanetRenderer {
    public EarthRenderer() {
        super(new EarthModel());
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier(HeroesUnited.MODID,"textures/planets/earth.png");
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {

        matrixStack.scale(0.95F, 0.95F, 0.95F);
        matrixStack.translate(0,-1,0);
        VertexConsumer buffer = EarthModel.EARTH_TEXTURE_MATERIAL.getVertexConsumer(buffers, RenderLayer::getEntityTranslucent);
        planetModel.prepareModel(partialTicks);
        planetModel.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderLayer getRenderType() {
        return RenderLayer.getEntityTranslucent(getTextureLocation());
    }
}
