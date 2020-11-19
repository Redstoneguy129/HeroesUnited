package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import xyz.heroesunited.heroesunited.common.abilities.Superpower;

import java.util.Map;

public class HURegisterSuperpower extends Event {

    private Map<ResourceLocation, Superpower> map;

    public HURegisterSuperpower(Map<ResourceLocation, Superpower> map) {
        this.map = map;
    }

    public void register(Superpower superpower) {
        map.put(superpower.getRegistryName(), superpower);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
