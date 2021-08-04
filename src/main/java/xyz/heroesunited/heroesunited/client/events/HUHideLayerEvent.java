package xyz.heroesunited.heroesunited.client.events;

import com.google.common.collect.Lists;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.List;

/**
 * This event is called before layer should rendering.
 * Can be used to hide layers
 */
public class HUHideLayerEvent extends EntityEvent {

    private final List<Class<? extends FeatureRenderer>> blockedLayers;

    public HUHideLayerEvent(Entity entity) {
        super(entity);
        this.blockedLayers = Lists.newArrayList();
    }

    public void blockLayer(Class<? extends FeatureRenderer> layer) {
        this.blockedLayers.add(layer);
    }

    public void blockLayers(Class<? extends FeatureRenderer>... layers) {
        for (Class<? extends FeatureRenderer> layer : layers) {
            blockLayer(layer);
        }
    }

    public List<Class<? extends FeatureRenderer>> getBlockedLayers() {
        return blockedLayers;
    }
}
