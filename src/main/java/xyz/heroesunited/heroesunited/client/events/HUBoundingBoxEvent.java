package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This event is called when the bounding box changes.
 * Can be used for those who want to change the player's bounding box.
 */
public class HUBoundingBoxEvent extends PlayerEvent {

    private final EntitySize oldSize;
    private EntitySize newSize;

    public HUBoundingBoxEvent(PlayerEntity entity, EntitySize oldSize) {
        super(entity);
        this.oldSize = oldSize;
        this.newSize = oldSize;
    }

    public EntitySize getNewSize() {
        return newSize;
    }

    public EntitySize getOldSize() {
        return oldSize;
    }

    public void setNewSize(EntitySize newSize) {
        this.newSize = newSize;
    }
}
