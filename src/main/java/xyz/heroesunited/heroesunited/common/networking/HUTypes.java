package xyz.heroesunited.heroesunited.common.networking;

import net.minecraft.entity.Entity;
import xyz.heroesunited.heroesunited.common.capabilities.HUPlayerProvider;

public enum HUTypes {
    FLYING, INTAGIBLE;

    public static void set(Entity entity, HUTypes data, boolean value, boolean server) {
        entity.getCapability(HUPlayerProvider.CAPABILITY).ifPresent((a) -> {
            switch (data) {
                case FLYING: {
                    a.setFlying(value);
                    break;
                }
                case INTAGIBLE: {
                    a.setIntangible(value);
                    break;
                }
            }
            if (!server) {
                a.sync();
            }
        });
    }

}
