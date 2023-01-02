package xyz.heroesunited.heroesunited.client.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.JsonSuit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;

public class GeckoSuitModel<T extends SuitItem> extends DefaultedItemGeoModel<T> {

    private boolean slim;

    public GeckoSuitModel() {
        super(new ResourceLocation(""));
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
    }

    public ResourceLocation getSlimModelResource(T item) {
        return getLayer(item, "slim_model", getLayer(item, "model", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json")));
    }

    public ResourceLocation getSlimTextureResource(T item) {
        return getLayer(item, "slim_texture", getLayer(item, "texture", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png")));
    }

    @Override
    public ResourceLocation getModelResource(T item) {
        if (slim) {
            return getSlimModelResource(item);
        }

        return getLayer(item, "model", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(T item) {
        if (slim) {
            return getSlimTextureResource(item);
        }
        return getLayer(item, "texture", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png"));
    }

    @Override
    public ResourceLocation getAnimationResource(T item) {
        return getLayer(item, "animation", new ResourceLocation(item.getSuit().getRegistryName().getNamespace(), "animations/" + item.getSuit().getRegistryName().getPath() + ".animation.json"));
    }

    public ResourceLocation getLayer(T item, String type, ResourceLocation path) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(item.getSuit().getRegistryName());
        if (layer != null && layer.getTexture(type) != null) {
            return layer.getTexture(type);
        }
        if (item.getSuit() instanceof JsonSuit && ((JsonSuit) item.getSuit()).getJsonObject() != null && ((JsonSuit) item.getSuit()).getJsonObject().has(type))
            return new ResourceLocation(GsonHelper.getAsString(((JsonSuit) item.getSuit()).getJsonObject(), type));
        return path;
    }
}