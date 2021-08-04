package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import xyz.heroesunited.heroesunited.client.render.model.space.StarModel;

public abstract class StarRenderer extends CelestialBodyRenderer{

    protected final StarModel starModel;

    public StarRenderer(StarModel starModel) {
        this.starModel = starModel;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {
        starModel.prepareModel(partialTicks);
        starModel.render(matrixStack,buffers.getBuffer(getRenderType()),packedLight, OverlayTexture.DEFAULT_UV, 1f,1f, 1f, 1f);
    }
}
