package xyz.heroesunited.heroesunited.client.render.renderer;

import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import xyz.heroesunited.heroesunited.common.objects.items.GeckoAccessory;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

import javax.annotation.Nullable;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GeckoAccessoryRenderer extends GeoItemRenderer<GeckoAccessory> {

    public GeckoAccessoryRenderer() {
        this(null);
    }

    public GeckoAccessoryRenderer(Identifier modelFile) {
        super(new AnimatedGeoModel<>() {
            @Override
            public Identifier getAnimationFileLocation(GeckoAccessory accessory) {
                return accessory.getAnimationFile();
            }

            @Override
            public Identifier getModelLocation(GeckoAccessory accessory) {
                return modelFile == null ? accessory.getModelFile() : modelFile;
            }

            @Override
            public Identifier getTextureLocation(GeckoAccessory accessory) {
                return accessory.getTextureFile();
            }
        });
    }

    @Override
    public RenderLayer getRenderType(GeckoAccessory animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return animatable == HUItems.JASON_MASK || animatable == HUItems.ZEK_GLASSES ? RenderLayer.getEntityTranslucent(textureLocation) : RenderLayer.getEntityCutoutNoCull(textureLocation);
    }
}
