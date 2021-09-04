package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.HashMap;
import java.util.function.Function;

public class Planet extends CelestialBody {

    public static final HashMap<ResourceKey<Level>, Planet> PLANETS_MAP = new HashMap<>();

    private ResourceKey<Level> dimension;

    private Star star;

    private float scale;

    private float speed;

    public Planet(ResourceKey<Level> dimension, Vec3 coordinates, float scale, float speed, Star star) {
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
    public Vec3 yRot(float angle, Vec3 vector3d) {
        double f = Math.cos(angle);
        double f1 = Math.sin(angle);
        double d0 = vector3d.x * f + vector3d.z * f1;
        double d1 = vector3d.y;
        double d2 = vector3d.z * f - vector3d.x * f1;
        return new Vec3(d0, d1, d2);
    }

    @Override
    public void entityInside(Entity entity) {
        if(dimension != null){
            if(entity.getVehicle() == null){
                entity.changeDimension(((ServerLevel) entity.level).getServer().getLevel(dimension), new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);

                        repositionedEntity.teleportTo(0, 9000, 0);
                        repositionedEntity.setNoGravity(false);
                        return repositionedEntity;
                    }
                });
            } else {
                entity.getVehicle().changeDimension(((ServerLevel) entity.level).getServer().getLevel(dimension), new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        Entity repositionedEntity = repositionEntity.apply(false);

                        repositionedEntity.teleportTo(0, 9000, 0);
                        repositionedEntity.setNoGravity(false);
                        return repositionedEntity;
                    }
                });
            }
        }
    }

    @Override
    public AABB getHitbox() {
        return new AABB(getCoordinates().x - scale / 2, getCoordinates().y - scale / 2, getCoordinates().z - scale / 2, getCoordinates().x + scale / 2, getCoordinates().y + scale / 2, getCoordinates().z + scale / 2);
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public Vec3 getCoordinates() {
        return star.getCoordinates().add(coordinates);
    }

    public Vec3 getOutCoordinates() {
        return getCoordinates().add(new Vec3(0, scale / 2 + 3, 0));
    }

    public boolean hasOxygen() {
        return this == CelestialBodies.EARTH;
    }
}
