package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.EarthModel;
import xyz.heroesunited.heroesunited.client.render.model.space.MoonModel;

public class MoonRenderer extends SatelliteRenderer {
    public MoonRenderer() {
        super(new MoonModel());
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
        satelliteModel.prepareModel(partialTicks);
        satelliteModel.render(matrixStack,buffer,packedLight, OverlayTexture.DEFAULT_UV, 1f,1f, 1f, 1f);
    }

    @Override
    protected RenderLayer getRenderType() {
        return RenderLayer.getEntityTranslucent(getTextureLocation());
    }
}
