package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when entity want sprint.
 */
@Cancelable
public class HUCancelSprinting extends EntityEvent {

    public HUCancelSprinting(Entity entity) {
        super(entity);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
