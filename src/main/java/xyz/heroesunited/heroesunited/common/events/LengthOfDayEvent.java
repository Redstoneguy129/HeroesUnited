package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when need check a length of day.
 */
public class LengthOfDayEvent extends Event {

    private final DimensionType dimensionType;
    private double time;

    public LengthOfDayEvent(DimensionType dimensionType, double time) {
        this.time = time;
        this.dimensionType = dimensionType;
    }

    public void setTime(double value) {
        this.time = value;
    }

    public double getTime() {
        return time;
    }

    public DimensionType getDimensionType() {
        return dimensionType;
    }
}
