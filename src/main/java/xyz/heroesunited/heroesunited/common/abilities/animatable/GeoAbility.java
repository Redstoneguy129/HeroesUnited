package xyz.heroesunited.heroesunited.common.abilities.animatable;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

public interface GeoAbility extends GeoAnimatable {

    @Override
    default double getTick(Object entity) {
        return ((Entity)entity).tickCount;
    }
}
