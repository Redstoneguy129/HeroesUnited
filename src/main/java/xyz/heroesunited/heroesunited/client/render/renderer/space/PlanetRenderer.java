package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import xyz.heroesunited.heroesunited.client.render.model.space.PlanetModel;

public abstract class PlanetRenderer extends CelestialBodyRenderer{

    protected final PlanetModel planetModel;

    public PlanetRenderer(PlanetModel planetModel) {
        this.planetModel = planetModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {
        planetModel.prepareModel(partialTicks);
        planetModel.render(matrixStack,buffers.getBuffer(getRenderType()),packedLight, OverlayTexture.DEFAULT_UV, 1f,1f, 1f, 1f);
    }
}
