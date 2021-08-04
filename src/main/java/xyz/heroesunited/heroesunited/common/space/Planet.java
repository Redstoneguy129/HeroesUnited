package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

import java.util.HashMap;
import java.util.function.Function;

public class Planet extends CelestialBody {

    public static final HashMap<RegistryKey<World>, Planet> PLANETS_MAP = new HashMap<>();

    private RegistryKey<World> dimension;

    private Star star;

    private float scale;

    private float speed;

    public Planet(RegistryKey<World> dimension, Vec3d coordinates, float scale, float speed, Star star) {
        super(coordinates);
        this.dimension = dimension;
        this.speed = speed;
        this.scale = scale;
        this.star = star;
        if (dimension != null)
            PLANETS_MAP.put(dimension, this);
    }

    public void tick() {
        coordinates = yRot(speed, coordinates);
    }
    public Vec3d yRot(float angle, Vec3d vector3d) {
        double f = Math.cos(angle);
        double f1 = Math.sin(angle);
        double d0 = vector3d.x * f + vector3d.z * f1;
        double d1 = vector3d.y;
        double d2 = vector3d.z * f - vector3d.x * f1;
        return new Vec3d(d0, d1, d2);
    }

    @Override
    public void entityInside(Entity entity) {
        if(dimension != null){
            if(entity.getVehicle() == null){
                entity.moveToWorld(((ServerWorld) entity.world).getServer().getWorld(dimension), new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);

                        repositionedEntity.requestTeleport(0, 9000, 0);
                        repositionedEntity.setNoGravity(false);
                        return repositionedEntity;
                    }
                });
            } else {
                entity.getVehicle().moveToWorld(((ServerWorld) entity.world).getServer().getWorld(dimension), new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);

                        repositionedEntity.requestTeleport(0, 9000, 0);
                        repositionedEntity.setNoGravity(false);
                        return repositionedEntity;
                    }
                });
            }
        }
    }

    @Override
    public Box getHitbox() {
        return new Box(getCoordinates().x - scale / 2, getCoordinates().y - scale / 2, getCoordinates().z - scale / 2, getCoordinates().x + scale / 2, getCoordinates().y + scale / 2, getCoordinates().z + scale / 2);
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    @Override
    public Vec3d getCoordinates() {
        return star.getCoordinates().add(coordinates);
    }

    public Vec3d getOutCoordinates() {
        return getCoordinates().add(new Vec3d(0, scale / 2 + 3, 0));
    }

    public boolean hasOxygen() {
        return this == CelestialBodies.EARTH;
    }
}
