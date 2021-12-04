package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Fired when entity want sprint.
 */
@Cancelable
public class EntitySprintingEvent extends EntityEvent {

    public EntitySprintingEvent(Entity entity) {
        super(entity);
    }
}
