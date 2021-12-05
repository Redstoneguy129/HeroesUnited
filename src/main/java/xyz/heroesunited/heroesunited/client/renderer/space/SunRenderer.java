package xyz.heroesunited.heroesunited.client.renderer.space;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.client.model.space.SunModel;
import xyz.heroesunited.heroesunited.util.HUClientUtil;

public class SunRenderer extends StarRenderer {
    public SunRenderer(ModelPart part) {
        super(new SunModel(part));
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(HeroesUnited.MODID, "textures/planets/sun.png");
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLight, float partialTicks) {
        matrixStack.scale(12.5F, 12.5F, 12.5F);
        matrixStack.translate(0, -1.5, 0);
        VertexConsumer buffer = SunModel.SUN_TEXTURE_MATERIAL.buffer(buffers, HUClientUtil.HURenderTypes::sunRenderer);
        starModel.prepareModel(partialTicks);
        starModel.renderToBuffer(matrixStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    @Override
    protected RenderType getRenderType() {
        return RenderType.entityTranslucent(getTextureLocation());
    }
}
