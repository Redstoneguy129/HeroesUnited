package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This event is called when the bounding box changes.
 * Can be used for those who want to change the player's bounding box.
 */
public class HUBoundingBoxEvent extends PlayerEvent {

    private final EntityDimensions oldSize;
    private EntityDimensions newSize;

    public HUBoundingBoxEvent(PlayerEntity entity, EntityDimensions oldSize) {
        super(entity);
        this.oldSize = oldSize;
        this.newSize = oldSize;
    }

    public EntityDimensions getNewSize() {
        return newSize;
    }

    public EntityDimensions getOldSize() {
        return oldSize;
    }

    public void setNewSize(EntityDimensions newSize) {
        this.newSize = newSize;
    }
}
