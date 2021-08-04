package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import xyz.heroesunited.heroesunited.client.render.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;

public class GeckoSuitRenderer extends GeoArmorRenderer<SuitItem> {

    public GeckoSuitRenderer() {
        super(new GeckoSuitModel());
    }

    @Override
    public RenderLayer getRenderType(SuitItem animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}