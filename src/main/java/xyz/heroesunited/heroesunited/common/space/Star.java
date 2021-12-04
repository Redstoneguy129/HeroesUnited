package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Star extends CelestialBody {
    private final AABB boundingBox;

    public Star(Vec3 coordinates, float scale) {
        super(coordinates);
        this.boundingBox = new AABB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }

    @Override
    public AABB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void entityInside(Entity entity) {
        entity.setSecondsOnFire(200);
    }
}
