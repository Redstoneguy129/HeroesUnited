package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import xyz.heroesunited.heroesunited.util.hudata.HUDataManager;

/**
 * Event that used to register HUData.
 * Fired when HUDataManager registering HUData.
 */
public class HUDataRegisterEvent extends EntityEvent {

    private final HUDataManager manager;

    public HUDataRegisterEvent(Entity entity, HUDataManager manager) {
        super(entity);
        this.manager = manager;
    }

    public HUDataManager getHUDataManager() {
        return manager;
    }

    public <T> void register(String id, T defaultValue) {
        getHUDataManager().register(id, defaultValue);
    }

}
