package xyz.heroesunited.heroesunited.client.render.renderer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public interface IGeoAbility extends IAnimatable {

    @OnlyIn(Dist.CLIENT)
    Identifier getTexture();

    @OnlyIn(Dist.CLIENT)
    Identifier getModelPath();

    @OnlyIn(Dist.CLIENT)
    Identifier getAnimationFile();

    default <A extends IGeoAbility> AnimatedGeoModel<A> getGeoModel() {
        return new AnimatedGeoModel<A>() {
            @Override
            public Identifier getModelLocation(A ability) {
                return ability.getModelPath();
            }

            @Override
            public Identifier getTextureLocation(A ability) {
                return ability.getTexture();
            }

            @Override
            public Identifier getAnimationFileLocation(A ability) {
                return ability.getAnimationFile();
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    default boolean renderAsDefault() {
        return true;
    }

    default void renderGeoAbilityRenderer(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, GeoModel model, AnimationEvent event, AbstractClientPlayerEntity player, GeoAbilityRenderer renderer) {

    }
}
