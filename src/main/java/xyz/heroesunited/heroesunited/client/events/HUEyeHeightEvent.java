package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;


/**
 * This event is called when the eye height changes.
 * Can be used for those who want to change the creature's normal eye height
 */
public class HUEyeHeightEvent extends LivingEvent {

    private final float oldEyeHeight;
    private float newEyeHeight;

    public HUEyeHeightEvent(LivingEntity entity, float oldEyeHeight) {
        super(entity);
        this.oldEyeHeight = oldEyeHeight;
        this.newEyeHeight = oldEyeHeight;
    }

    public float getOldEyeHeight() {
        return oldEyeHeight;
    }

    public void setNewEyeHeight(float eyeHeight) {
        this.newEyeHeight = eyeHeight;
    }

    public float getNewEyeHeight() {
        return newEyeHeight;
    }
}
