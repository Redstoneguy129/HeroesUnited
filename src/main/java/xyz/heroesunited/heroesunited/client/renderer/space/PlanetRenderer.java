package xyz.heroesunited.heroesunited.client.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import xyz.heroesunited.heroesunited.client.model.space.PlanetModel;

public abstract class PlanetRenderer extends CelestialBodyRenderer {

    protected final PlanetModel planetModel;

    public PlanetRenderer(ModelPart planet) {
        this(new PlanetModel(planet));
    }

    public PlanetRenderer(PlanetModel model) {
        this.planetModel = model;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        planetModel.prepareModel(partialTicks);
        planetModel.renderToBuffer(matrixStack, buffers.getBuffer(getRenderType()), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
