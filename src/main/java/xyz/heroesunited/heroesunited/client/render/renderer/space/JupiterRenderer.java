package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.PlanetModel;

public class JupiterRenderer extends PlanetRenderer {
    public JupiterRenderer() {
        super(new PlanetModel());
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier(HeroesUnited.MODID, "textures/planets/jupiter.png");
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {

        matrixStack.scale(4.95F, 4.95F, 4.95F);
        matrixStack.translate(0, -1, 0);
        VertexConsumer buffer = buffers.getBuffer(RenderLayer.getEntityTranslucent(getTextureLocation()));
        planetModel.prepareModel(partialTicks);
        planetModel.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderLayer getRenderType() {
        return RenderLayer.getEntityTranslucent(getTextureLocation());
    }
}
