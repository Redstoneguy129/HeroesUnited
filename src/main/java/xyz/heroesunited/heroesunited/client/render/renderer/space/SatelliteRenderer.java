package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import xyz.heroesunited.heroesunited.client.render.model.space.SatelliteModel;

public abstract class SatelliteRenderer extends CelestialBodyRenderer{

    protected final SatelliteModel satelliteModel;

    public SatelliteRenderer(SatelliteModel satelliteModel) {
        this.satelliteModel = satelliteModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {
        satelliteModel.prepareModel(partialTicks);
        satelliteModel.render(matrixStack,buffers.getBuffer(getRenderType()),packedLight, OverlayTexture.DEFAULT_UV, 1f,1f, 1f, 1f);
    }
}
