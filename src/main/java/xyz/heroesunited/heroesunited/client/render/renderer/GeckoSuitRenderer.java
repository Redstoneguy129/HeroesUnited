package xyz.heroesunited.heroesunited.client.render.renderer;

import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import xyz.heroesunited.heroesunited.client.render.model.GeckoSuitModel;
import xyz.heroesunited.heroesunited.common.abilities.suit.GeckoSuitItem;

public class GeckoSuitRenderer extends GeoArmorRenderer<GeckoSuitItem> {

    public GeckoSuitRenderer() {
        super(new GeckoSuitModel());
    }
}