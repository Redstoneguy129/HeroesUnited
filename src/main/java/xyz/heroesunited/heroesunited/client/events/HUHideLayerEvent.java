package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This event is called before layer should rendering.
 * Can be used to hide layers
 */
public class HUHideLayerEvent extends EntityEvent {

    private final List<Class<? extends LayerRenderer>> blockedLayers;

    public HUHideLayerEvent(Entity entity) {
        super(entity);
        this.blockedLayers = new ArrayList<>();
    }

    public void blockLayer(Class<? extends LayerRenderer> layer) {
        this.blockedLayers.add(layer);
    }

    public void blockLayers(Class<? extends LayerRenderer>... layers) {
        for (Class<? extends LayerRenderer> layer : layers) {
            blockLayer(layer);
        }
    }

    public List<Class<? extends LayerRenderer>> getBlockedLayers() {
        return blockedLayers;
    }
}
