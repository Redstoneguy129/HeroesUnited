package xyz.heroesunited.heroesunited.common.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

public class HURegisterDataEvent extends PlayerEvent {

    private final IHUPlayer data;

    public HURegisterDataEvent(PlayerEntity player, IHUPlayer data) {
        super(player);
        this.data = data;
    }

    public IHUPlayer getData() {
        return data;
    }

    public <T> void register(String key, T defaultValue) {
        getData().register(key, defaultValue, false);
    }

    public <T> void register(String key, T defaultValue, boolean saving) {
        getData().register(key, defaultValue, saving);
    }

}
