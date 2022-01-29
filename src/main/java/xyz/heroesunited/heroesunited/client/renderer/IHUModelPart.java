package xyz.heroesunited.heroesunited.client.renderer;

import net.minecraft.client.model.geom.builders.CubeDeformation;

public interface IHUModelPart {

    void setSize(CubeDeformation size);

    CubeDeformation size();

    default void resetSize() {
        this.setSize(CubeDeformation.NONE);
    }
}
