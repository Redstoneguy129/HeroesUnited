package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import xyz.heroesunited.heroesunited.common.objects.items.GeckoAccessory;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

import javax.annotation.Nullable;

public class GeckoAccessoryRenderer extends GeoItemRenderer<GeckoAccessory> {

    public GeckoAccessoryRenderer() {
        this(null);
    }

    public GeckoAccessoryRenderer(ResourceLocation modelFile) {
        super(new AnimatedGeoModel<GeckoAccessory>() {
            @Override
            public ResourceLocation getAnimationFileLocation(GeckoAccessory accessory) {
                return accessory.getAnimationFile();
            }

            @Override
            public ResourceLocation getModelLocation(GeckoAccessory accessory) {
                return modelFile == null ? accessory.getModelFile() : modelFile;
            }

            @Override
            public ResourceLocation getTextureLocation(GeckoAccessory accessory) {
                return accessory.getTextureFile();
            }
        });
    }

    @Override
    public RenderType getRenderType(GeckoAccessory animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        if (animatable == HUItems.JASON_MASK || animatable == HUItems.ZEK_GLASSES || animatable == HUItems.MADNESSCOMBAT) {
            return RenderType.entityTranslucent(textureLocation);
        }
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
