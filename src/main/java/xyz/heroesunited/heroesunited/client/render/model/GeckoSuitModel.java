package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.GeckoSuitItem;

public class GeckoSuitModel<T extends GeckoSuitItem> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelLocation(T item) {
        return new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(T item) {
        return new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T item) {
        return new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "animations/" + item.getSuit().getRegistryName().getPath() + ".animation.json");
    }
}