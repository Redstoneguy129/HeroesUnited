package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Star extends CelestialBody {


    private Box hitbox;

    public Star(Vec3d coordinates, float scale) {
        super(coordinates);
        hitbox = new Box(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }

    @Override
    public Box getHitbox() {
        return hitbox;
    }

    @Override
    public void entityInside(Entity entity) {
        entity.setOnFireFor(200);
    }
}
