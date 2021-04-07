package xyz.heroesunited.heroesunited.client.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public interface IGeoAbility extends IAnimatable {

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getTexture();

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getModelPath();

    @OnlyIn(Dist.CLIENT)
    ResourceLocation getAnimationFile();

    default <A extends IGeoAbility> AnimatedGeoModel<A> getGeoModel() {
        return new AnimatedGeoModel<A>() {
            @Override
            public ResourceLocation getModelLocation(A ability) {
                return ability.getModelPath();
            }

            @Override
            public ResourceLocation getTextureLocation(A ability) {
                return ability.getTexture();
            }

            @Override
            public ResourceLocation getAnimationFileLocation(A ability) {
                return ability.getAnimationFile();
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    default boolean renderAsDefault() {
        return true;
    }

    default void renderGeoAbilityRenderer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, GeoModel model, AnimationEvent event, AbstractClientPlayerEntity player, GeoAbilityRenderer renderer) {

    }
}
