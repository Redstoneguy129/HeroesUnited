package xyz.heroesunited.heroesunited.client.render.renderer;

import software.bernie.geckolib3.core.IAnimatable;

public interface IGeoAbility extends IAnimatable {

    default boolean copyRotations() {
        return true;
    }

    default boolean copyPos() {
        return true;
    }
}
