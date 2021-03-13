package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.client.Minecraft;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.GeckoSuitItem;

public class GeckoSuitModel<T extends GeckoSuitItem> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelLocation(T item) {
        ResourceLocation res = new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json");
        return item.getSuit().getJsonObject() != null ? new ResourceLocation(JSONUtils.getAsString(item.getSuit().getJsonObject(), "model", res.toString())) : res;
    }

    @Override
    public ResourceLocation getTextureLocation(T item) {
        if (item.getSuit().getJsonObject() != null && item.getSuit().getJsonObject().has("texture")) {
            if (JSONUtils.getAsString(item.getSuit().getJsonObject(), "texture").equals("player")) {
                return Minecraft.getInstance().player.getSkinTextureLocation();
            } else {
                return new ResourceLocation(JSONUtils.getAsString(item.getSuit().getJsonObject(), "texture"));
            }
        } else return new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T item) {
        return new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "animations/" + item.getSuit().getRegistryName().getPath() + ".animation.json");
    }
}