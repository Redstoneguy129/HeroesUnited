package xyz.heroesunited.heroesunited.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import xyz.heroesunited.heroesunited.common.objects.items.GeckoAccessory;
import xyz.heroesunited.heroesunited.common.objects.items.HUItems;

public class GeckoAccessoryRenderer extends GeoItemRenderer<GeckoAccessory> {

    public GeckoAccessoryRenderer() {
        this(null);
    }

    public GeckoAccessoryRenderer(ResourceLocation modelFile) {
        super(new DefaultedGeoModel<>(new ResourceLocation("")) {
            @Override
            public ResourceLocation getAnimationResource(GeckoAccessory accessory) {
                return accessory.getAnimationFile();
            }

            @Override
            protected String subtype() {
                return "item";
            }

            @Override
            public ResourceLocation getModelResource(GeckoAccessory accessory) {
                return modelFile == null ? accessory.getModelFile() : modelFile;
            }

            @Override
            public ResourceLocation getTextureResource(GeckoAccessory accessory) {
                return accessory.getTextureFile();
            }
        });
    }

    @Override
    public RenderType getRenderType(GeckoAccessory animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        if (animatable == HUItems.JASON_MASK.get() || animatable == HUItems.ZEK_GLASSES.get() || animatable == HUItems.MADNESSCOMBAT.get()) {
            return RenderType.entityTranslucent(texture);
        }
        return RenderType.entityCutoutNoCull(texture);
    }
}
