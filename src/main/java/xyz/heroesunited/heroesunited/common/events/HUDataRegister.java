package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import xyz.heroesunited.heroesunited.util.hudata.HUData;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

/**
 * Event that used to register HUData.
 * Fired when HUDataManager registering HUData.
 */
public class HUDataRegister extends EntityEvent {

    private final HUDataManager huData;

    public HUDataRegister(Entity entity, HUDataManager huData) {
        super(entity);
        this.huData = huData;
    }

    public HUDataManager getHUData() {
        return huData;
    }

    public <T> void register(HUData<T> data, T defaultValue) {
        getHUData().register(data, defaultValue);
    }

}
