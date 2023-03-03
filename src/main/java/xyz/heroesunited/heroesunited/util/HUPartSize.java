package xyz.heroesunited.heroesunited.util;

import org.joml.Vector3f;

public interface HUPartSize {

    default void changedScale() {

    }

    // For suit models scale
    void setSize(Vector3f size);

    Vector3f size();
}
