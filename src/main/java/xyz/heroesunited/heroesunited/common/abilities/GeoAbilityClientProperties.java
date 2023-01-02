package xyz.heroesunited.heroesunited.common.abilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import xyz.heroesunited.heroesunited.client.renderer.GeoAbilityRenderer;
import xyz.heroesunited.heroesunited.common.abilities.animatable.GeoAbility;

public abstract class GeoAbilityClientProperties<T extends Ability & GeoAbility> implements IAbilityClientProperties {

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

    public GeoModel<T> getGeoModel() {
        return new GeoModel<T>() {
            @Override
            public ResourceLocation getModelResource(T ability) {
                return GeoAbilityClientProperties.this.getModelPath();
            }

            @Override
            public ResourceLocation getTextureResource(T ability) {
                return GeoAbilityClientProperties.this.getTexture();
            }

            @Override
            public ResourceLocation getAnimationResource(T ability) {
                return GeoAbilityClientProperties.this.getAnimationFile();
            }
        };
    }

    public boolean showingAnimationAlways() {
        return true;
    }

    public boolean renderGeoAbilityRenderer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, boolean firstPerson, BakedGeoModel model, AbstractClientPlayer player, GeoAbilityRenderer<T> renderer) {
        return false;
    }

    public GeoAbilityRenderer<T> getGeoRenderer() {
        return this.abilityRenderer;
    }
}
