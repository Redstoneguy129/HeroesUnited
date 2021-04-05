package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import xyz.heroesunited.heroesunited.HeroesUnited;
import xyz.heroesunited.heroesunited.common.objects.items.ArcReactorAccessory;

import javax.annotation.Nullable;

public class ArcReactorRenderer extends GeoItemRenderer<ArcReactorAccessory> {

    public ArcReactorRenderer() {
        super(new AnimatedGeoModel<ArcReactorAccessory>() {
            @Override
            public ResourceLocation getAnimationFileLocation(ArcReactorAccessory arcReactorAccessory) {
                return arcReactorAccessory.getRegistryName();
            }

            @Override
            public ResourceLocation getModelLocation(ArcReactorAccessory arcReactorAccessory) {
                return new ResourceLocation(HeroesUnited.MODID, "geo/arc_reactor.geo.json");
            }

            @Override
            public ResourceLocation getTextureLocation(ArcReactorAccessory arcReactorAccessory) {
                return new ResourceLocation(HeroesUnited.MODID, "textures/accessories/arc_reactor.png");
            }
        });
    }

    @Override
    public RenderType getRenderType(ArcReactorAccessory animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityCutoutNoCull(textureLocation);
    }
}
