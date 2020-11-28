package xyz.heroesunited.heroesunited.common.networking;

import net.minecraft.entity.Entity;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

public enum HUData {
    COOLDOWN,
    TYPE,
    TIMER,
    FLYING,
    INTAGIBLE,
    IN_TIMER;

    public static void set(Entity entity, HUData data, int value) {
        entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
            boolean booleans = value == 1 ? true : false;
            switch (data) {
                case COOLDOWN: {
                    a.setCooldown(value);
                }
                case TYPE: {
                    a.setType(value);
                }
                case TIMER: {
                    a.setTimer(value);
                }
                case FLYING: {
                    a.setFlying(booleans);
                }
                case INTAGIBLE: {
                    a.setIntangible(booleans);
                }
                case IN_TIMER: {
                    a.setInTimer(booleans);
                }
            }
        });
    }

}
