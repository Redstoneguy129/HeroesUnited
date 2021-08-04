package xyz.heroesunited.heroesunited.client.render.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.JsonSuit;
import xyz.heroesunited.heroesunited.common.abilities.suit.SuitItem;
import xyz.heroesunited.heroesunited.hupacks.HUPackLayers;

public class GeckoSuitModel<T extends SuitItem> extends AnimatedGeoModel<T> {
    @Override
    public Identifier getModelLocation(T item) {
        Identifier res = new Identifier(item.getSuit().getRegistryName().getNamespace(), "geo/" + item.getSuit().getRegistryName().getPath() + ".geo.json");
        if (getLayer(item, "texture") != null) {
            return getLayer(item, "texture");
        }
        if (item.getSuit() instanceof JsonSuit && ((JsonSuit) item.getSuit()).getJsonObject() != null)
            return new Identifier(JsonHelper.getString(((JsonSuit) item.getSuit()).getJsonObject(), "model", res.toString()));
        return res;
    }

    @Override
    public Identifier getTextureLocation(T item) {
        if (getLayer(item, "texture") != null) {
            return getLayer(item, "texture");
        }
        if (item.getSuit() instanceof JsonSuit && ((JsonSuit) item.getSuit()).getJsonObject() != null && ((JsonSuit) item.getSuit()).getJsonObject().has("texture")) {
            return new Identifier(JsonHelper.getString(((JsonSuit) item.getSuit()).getJsonObject(), "texture"));
        } else return new Identifier(item.getSuit().getRegistryName().getNamespace(), "textures/suits/" + item.getSuit().getRegistryName().getPath() + ".png");
    }

    public Identifier getLayer(T item, String type) {
        HUPackLayers.Layer layer = HUPackLayers.getInstance().getLayer(item.getSuit().getRegistryName());
        if (layer != null && layer.getTexture(type) != null) {
            return layer.getTexture(type);
        }
        return null;
    }

    @Override
    public Identifier getAnimationFileLocation(T item) {
        return new Identifier(item.getSuit().getRegistryName().getNamespace(), "animations/" + item.getSuit().getRegistryName().getPath() + ".animation.json");
    }
}