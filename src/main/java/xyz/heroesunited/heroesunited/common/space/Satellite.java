package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class Satellite extends CelestialBody{

    private RegistryKey<World> dimension;

    private Planet planet;

    private float scale;

    private float speed;

    public Satellite(RegistryKey<World> dimension, Vec3d coordinates, float scale, float speed,  Planet planet) {
        super(coordinates);
        this.dimension = dimension;
        this.scale = scale;
        this.speed = speed;
        this.planet = planet;
    }

    public void tick(){
        coordinates = this.coordinates.rotateY(speed);
    }

    @Override
    public void entityInside(Entity entity) {
        if(dimension != null){
            entity.moveToWorld(((ServerWorld) entity.world).getServer().getWorld(dimension), new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity repositionedEntity = repositionEntity.apply(false);

                    repositionedEntity.requestTeleport(0, 10000, 0);
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
    public Vec3d getCoordinates() {
        return planet.getCoordinates().add(coordinates);
    }

    @Override
    public Box getHitbox() {
        return new Box(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }
}
