package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.GeckoLibUtil;
import xyz.heroesunited.heroesunited.client.render.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;

public class GeckoSuitRenderer<T extends SuitItem> extends GeoArmorRenderer<T> {

    public GeckoSuitRenderer() {
        this(new GeckoSuitModel<>());
    }

    public GeckoSuitRenderer(AnimatedGeoModel<T> modelProvider) {
        super(modelProvider);
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public Integer getUniqueID(T animatable) {
        return this.itemStack.isEmpty() ? super.getUniqueID(animatable) : GeckoLibUtil.getIDFromStack(this.itemStack);
    }
}