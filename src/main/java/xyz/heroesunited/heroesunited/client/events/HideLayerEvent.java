package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This event is called before layer should rendering.
 * Can be used to hide layers
 */
@SuppressWarnings("rawtypes")
public class HideLayerEvent extends EntityEvent {

    private final List<RenderLayer> layers;
    private final List<Class<? extends RenderLayer>> blockedLayers;

    public HideLayerEvent(Entity entity, List<RenderLayer> layers) {
        super(entity);
        this.layers = layers;
        this.blockedLayers = new ArrayList<>();
    }

    public void blockLayer(Class<? extends RenderLayer> layer) {
        this.blockedLayers.add(layer);
    }

    @SafeVarargs
    public final void blockLayers(Class<? extends RenderLayer>... layers) {
        for (Class<? extends RenderLayer> layer : layers) {
            blockLayer(layer);
        }
    }

    public List<RenderLayer> getLayers() {
        return layers;
    }

    public List<Class<? extends RenderLayer>> getBlockedLayers() {
        return blockedLayers;
    }
}
