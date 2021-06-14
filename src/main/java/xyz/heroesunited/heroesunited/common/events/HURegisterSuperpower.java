package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;

import java.util.Map;

/**
 * Event that used to register superpowers from java.
 * Fired when HUPackSuperpowers registering superpowers.
 */
public class HURegisterSuperpower extends Event {

    private Map<ResourceLocation, Superpower> map;

    public HURegisterSuperpower(Map<ResourceLocation, Superpower> map) {
        this.map = map;
    }

    public void register(Superpower superpower) {
        map.put(superpower.getRegistryName(), superpower);
    }

    public void registerAll(Superpower... superpowers) {
        for (Superpower superpower : superpowers) {
            this.register(superpower);
        }
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
