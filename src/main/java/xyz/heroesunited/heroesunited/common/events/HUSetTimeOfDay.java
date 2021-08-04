package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.DimensionType;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when entity want sprint.
 */
public class HUSetTimeOfDay extends Event {

    private DimensionType dimensionType;
    private double value;

    public HUSetTimeOfDay(DimensionType dimensionType, double value) {
        this.value = value;
        this.dimensionType = dimensionType;
    }

    public void setNewValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }
}
