package xyz.heroesunited.heroesunited.common.planets;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class Satellite extends CelestialBody{

    private Planet planet;

    private float scale;

    private float speed;

    public Satellite(Vector3d coordinates, float scale, float speed,  Planet planet) {
        super(coordinates);
        this.scale = scale;
        this.speed = speed;
        this.planet = planet;
    }

    public void tick(){
        coordinates = this.coordinates.yRot(speed);
    }

    @Override
    public Vector3d getCoordinates() {
        return planet.getCoordinates().add(coordinates);
    }

    public AxisAlignedBB getHitbox() {
        return new AxisAlignedBB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }
}
