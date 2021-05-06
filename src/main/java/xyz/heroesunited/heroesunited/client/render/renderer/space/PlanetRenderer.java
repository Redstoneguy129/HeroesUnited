package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import xyz.heroesunited.heroesunited.client.render.model.space.PlanetModel;

public abstract class PlanetRenderer extends CelestialBodyRenderer{

    protected final PlanetModel planetModel;

    public PlanetRenderer(PlanetModel planetModel) {
        this.planetModel = planetModel;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLight, float partialTicks) {
        planetModel.prepareModel(partialTicks);
        planetModel.renderToBuffer(matrixStack,buffers.getBuffer(getRenderType()),packedLight, OverlayTexture.NO_OVERLAY, 1f,1f, 1f, 1f);
    }
}
