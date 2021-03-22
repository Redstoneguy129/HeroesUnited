package xyz.heroesunited.heroesunited.common.networking;

import net.minecraft.entity.Entity;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

public enum HUTypes {
    TYPE, TIMER, ANIMATION_TIMER, FLYING, INTAGIBLE, IN_TIMER;

    public static void set(Entity entity, HUTypes data, int value, boolean server) {
        entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
            boolean booleans = value == 1;
            switch (data) {
                case TYPE: {
                    a.setType(value);
                    break;
                }
                case TIMER: {
                    a.setTimer(value);
                    break;
                }
                case ANIMATION_TIMER: {
                    a.setAnimationTimer(value);
                    break;
                }
                case FLYING: {
                    a.setFlying(booleans);
                    break;
                }
                case INTAGIBLE: {
                    a.setIntangible(booleans);
                    break;
                }
                case IN_TIMER: {
                    a.setInTimer(booleans);
                    break;
                }
            }
            if (!server) {
                a.sync();
            }
        });
    }

}
