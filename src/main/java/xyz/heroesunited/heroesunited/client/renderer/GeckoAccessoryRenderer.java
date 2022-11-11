package xyz.heroesunited.heroesunited.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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
        super(new AnimatedGeoModel<>() {
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
    public RenderType getRenderType(GeckoAccessory animatable, float partialTicks, PoseStack poseStack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        if (animatable == HUItems.JASON_MASK.get() || animatable == HUItems.ZEK_GLASSES.get() || animatable == HUItems.MADNESSCOMBAT.get()) {
            return RenderType.entityTranslucent(textureLocation);
        }
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
