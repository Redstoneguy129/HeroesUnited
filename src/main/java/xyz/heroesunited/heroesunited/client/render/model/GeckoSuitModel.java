package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.JsonSuit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;

public class GeckoSuitModel<T extends SuitItem> extends AnimatedGeoModel<T> {
    @Override
    public ResourceLocation getModelLocation(T item) {
        return getLayer(item, "model", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json"));
    }

    @Override
    public ResourceLocation getTextureLocation(T item) {
        return getLayer(item, "texture", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png"));
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T item) {
        return getLayer(item, "animation", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "animations/" + item.getSuit().getRegistryName().getPath() + ".animation.json"));
    }

    public ResourceLocation getLayer(T item, String type, ResourceLocation path) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(item.getSuit().getRegistryName());
        if (layer != null && layer.getTexture(type) != null) {
            return layer.getTexture(type);
        }
        if (item.getSuit() instanceof JsonSuit && ((JsonSuit) item.getSuit()).getJsonObject() != null && ((JsonSuit) item.getSuit()).getJsonObject().has(type))
            return new ResourceLocation(JSONUtils.getAsString(((JsonSuit) item.getSuit()).getJsonObject(), type));
        return path;
    }
}