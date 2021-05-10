package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class Satellite extends CelestialBody{

    private RegistryKey<World> dimension;

    private Planet planet;

    private float scale;

    private float speed;

    public Satellite(RegistryKey<World> dimension, Vector3d coordinates, float scale, float speed,  Planet planet) {
        super(coordinates);
        this.dimension = dimension;
        this.scale = scale;
        this.speed = speed;
        this.planet = planet;
    }

    public void tick(){
        coordinates = this.coordinates.yRot(speed);
    }

    @Override
    public void entityInside(Entity entity) {
        if(dimension != null){
            entity.changeDimension(((ServerWorld) entity.level).getServer().getLevel(dimension), new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity repositionedEntity = repositionEntity.apply(false);

                    repositionedEntity.teleportTo(0, 10000, 0);
                    repositionedEntity.setNoGravity(false);
                    return repositionedEntity;
                }
            });
        }
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    @Override
    public Vector3d getCoordinates() {
        return planet.getCoordinates().add(coordinates);
    }

    @Override
    public AxisAlignedBB getHitbox() {
        return new AxisAlignedBB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }
}
