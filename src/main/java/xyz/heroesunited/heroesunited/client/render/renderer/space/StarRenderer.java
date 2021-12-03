package xyz.heroesunited.heroesunited.client.render.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import xyz.heroesunited.heroesunited.client.render.model.space.StarModel;

public abstract class StarRenderer extends CelestialBodyRenderer {

    protected final StarModel starModel;

    public StarRenderer(StarModel starModel) {
        this.starModel = starModel;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        starModel.prepareModel(partialTicks);
        starModel.renderToBuffer(matrixStack, buffers.getBuffer(getRenderType()), packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
