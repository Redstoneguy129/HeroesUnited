package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Star extends CelestialBody {


    private final AABB hitbox;

    public Star(Vec3 coordinates, float scale) {
        super(coordinates);
        hitbox = new AABB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }

    @Override
    public AABB getHitbox() {
        return hitbox;
    }

    @Override
    public void entityInside(Entity entity) {
        entity.setSecondsOnFire(200);
    }
}
