package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.VenusModel;

public class VenusRenderer extends PlanetRenderer {
    public VenusRenderer() {
        super(new VenusModel());
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier(HeroesUnited.MODID, "textures/planets/venus.png");
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {

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
