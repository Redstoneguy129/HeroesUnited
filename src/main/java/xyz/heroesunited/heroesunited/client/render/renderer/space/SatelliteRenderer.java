package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import xyz.heroesunited.heroesunited.client.render.model.space.SatelliteModel;

public abstract class SatelliteRenderer extends CelestialBodyRenderer{

    protected final SatelliteModel satelliteModel;

    public SatelliteRenderer(SatelliteModel satelliteModel) {
        this.satelliteModel = satelliteModel;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        satelliteModel.prepareModel(partialTicks);
        satelliteModel.renderToBuffer(matrixStack,buffers.getBuffer(getRenderType()),packedLight, OverlayTexture.NO_OVERLAY, 1f,1f, 1f, 1f);
    }
}
