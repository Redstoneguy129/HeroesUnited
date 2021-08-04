package xyz.heroesunited.heroesunited.client.render.renderer.space;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.render.model.space.SunModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public class SunRenderer extends StarRenderer {
    public SunRenderer() {
        super(new SunModel());
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier(HeroesUnited.MODID, "textures/planets/sun.png");
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider buffers, int packedLight, float partialTicks) {
        matrixStack.scale(12.5F, 12.5F, 12.5F);
        matrixStack.translate(0, -1.5, 0);
        VertexConsumer buffer = SunModel.SUN_TEXTURE_MATERIAL.getVertexConsumer(buffers, HUClientUtil.HURenderTypes::sunRenderer);
        starModel.prepareModel(partialTicks);
        starModel.render(matrixStack, buffer, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderLayer getRenderType() {
        return RenderLayer.getEntityTranslucent(getTextureLocation());
    }
}
