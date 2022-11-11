package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.client.renderer.GeoAbilityRenderer;

public abstract class GeoAbilityClientProperties<T extends Ability & IAnimatable> implements IAbilityClientProperties {

    protected final T ability;
    protected final GeoAbilityRenderer<T> abilityRenderer;

    public GeoAbilityClientProperties(T ability) {
        this.ability = ability;
        this.abilityRenderer = this.createGeoRenderer(ability);
    }

    public GeoAbilityRenderer<T> createGeoRenderer(T ability) {
        return new GeoAbilityRenderer<>(this, ability);
    }

    public abstract ResourceLocation getModelPath();

    public ResourceLocation getTexture() {
        return null;
    }
    
    public ResourceLocation getAnimationFile() {
        return null;
    }

    public AnimatedGeoModel<T> getGeoModel() {
        return new AnimatedGeoModel<>() {
            @Override
            public ResourceLocation getModelLocation(T ability) {
                return GeoAbilityClientProperties.this.getModelPath();
            }

            @Override
            public ResourceLocation getTextureLocation(T ability) {
                return GeoAbilityClientProperties.this.getTexture();
            }

            @Override
            public ResourceLocation getAnimationFileLocation(T ability) {
                return GeoAbilityClientProperties.this.getAnimationFile();
            }
        };
    }

    public boolean showingAnimationAlways() {
        return true;
    }

    public boolean renderGeoAbilityRenderer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean firstPerson, GeoModel model, AbstractClientPlayer player, GeoAbilityRenderer<T> renderer) {
        return false;
    }

    public GeoAbilityRenderer<T> getGeoRenderer() {
        return this.abilityRenderer;
    }
}
