package xyz.heroesunited.heroesunited.client.events;

import net.minecraftforge.eventbus.api.Event;

/**
 * This event is called when light updated.
 * Can be used to make night vision and etc.
 */
public class HUChangeLightEvent extends Event {

    private final float defaultValue;
    private float value;

    public HUChangeLightEvent(float defaultValue) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public void setNewValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }
}
