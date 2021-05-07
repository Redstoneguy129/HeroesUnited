package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class Star extends CelestialBody {


    private AxisAlignedBB hitbox;

    public Star(Vector3d coordinates, float scale) {
        super(coordinates);
        hitbox = new AxisAlignedBB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }

    @Override
    public AxisAlignedBB getHitbox() {
        return hitbox;
    }

    @Override
    public void entityInside(Entity entity) {
        entity.setSecondsOnFire(200);
    }
}
