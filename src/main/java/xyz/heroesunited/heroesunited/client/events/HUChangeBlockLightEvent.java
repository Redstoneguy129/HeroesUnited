package xyz.heroesunited.heroesunited.client.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is called when block light updated.
 * Can be used to make dynamic lights
 */
public class HUChangeBlockLightEvent extends Event {

    private final int defaultValue;
    private int value;
    private BlockPos pos;
    private BlockView world;

    public HUChangeBlockLightEvent(int defaultValue, BlockPos pos, BlockView world) {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.pos = pos;
        this.world = world;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setNewValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockView getWorld() {
        return world;
    }
}
