package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * Fired when entity want sprint.
 */
public class HUCancelSprinting extends EntityEvent {

    public HUCancelSprinting(Entity entity) {
        super(entity);
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
