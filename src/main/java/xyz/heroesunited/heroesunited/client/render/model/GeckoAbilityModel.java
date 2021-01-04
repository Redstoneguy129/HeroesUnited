package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.GeckoAbility;

public class GeckoAbilityModel<T extends GeckoAbility> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelLocation(T ability) {
        ResourceLocation res = new ResourceLocation(ability.getSuperpower().getNamespace(), "geo/" + ability.getSuperpower().getPath() + "_" + ability.name + ".geo.json");
        return ability.getJsonObject()!=null ? new ResourceLocation(JSONUtils.getString(ability.getJsonObject(), "model", res.toString())) : res;

    }

    @Override
    public ResourceLocation getTextureLocation(T ability) {
        ResourceLocation res = new ResourceLocation(ability.getSuperpower().getNamespace(), "textures/ability/" + ability.getSuperpower().getPath() + "_" + ability.name +".png");
        return ability.getJsonObject()!=null ? new ResourceLocation(JSONUtils.getString(ability.getJsonObject(), "texture", res.toString())) : res;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T ability) {
        ResourceLocation res = ability.getSuperpower();
        return new ResourceLocation(res.getNamespace(), "animations/" + res.getPath() + "_" + ability.name + ".animation.json");
    }
}