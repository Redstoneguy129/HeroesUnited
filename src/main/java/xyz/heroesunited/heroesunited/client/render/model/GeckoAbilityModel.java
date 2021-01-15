package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.client.render.renderer.IGeoAbility;

public class GeckoAbilityModel<T extends IGeoAbility> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelLocation(T ability) {
        return ability.getModelPath();
    }

    @Override
    public ResourceLocation getTextureLocation(T ability) {
        return ability.getTexture();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T ability) {
        return ability.getAnimationFile();
    }
}