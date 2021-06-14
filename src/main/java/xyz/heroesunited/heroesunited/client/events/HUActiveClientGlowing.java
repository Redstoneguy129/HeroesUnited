package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event can be used to make entity glowing. (Like with spectral arrow)
 */
public class HUActiveClientGlowing extends Event {

    private Entity entity;
    private boolean shouldGlow = false;

    public HUActiveClientGlowing(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public boolean shouldGlow() {
        return shouldGlow;
    }

    public void setShouldGlow(boolean shouldGlow) {
        this.shouldGlow = shouldGlow;
    }
}
