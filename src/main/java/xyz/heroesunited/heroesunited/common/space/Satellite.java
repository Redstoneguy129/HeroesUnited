package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class Satellite extends CelestialBody {

    private final ResourceKey<Level> dimension;

    private final Planet planet;

    private final float scale;

    private final float speed;

    public Satellite(ResourceKey<Level> dimension, Vec3 coordinates, float scale, float speed, Planet planet) {
        super(coordinates);
        this.dimension = dimension;
        this.scale = scale;
        this.speed = speed;
        this.planet = planet;
    }

    public void tick() {
        coordinates = this.coordinates.yRot(speed);
    }

    @Override
    public void entityInside(Entity entity) {
        if (dimension != null) {
            entity.changeDimension(((ServerLevel) entity.level).getServer().getLevel(dimension), new ITeleporter() {
                @Override
                public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                    Entity repositionedEntity = repositionEntity.apply(false);

                    repositionedEntity.teleportTo(0, 10000, 0);
                    repositionedEntity.setNoGravity(false);
                    return repositionedEntity;
                }
            });
        }
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    @Override
    public Vec3 getCoordinates() {
        return planet.getCoordinates().add(coordinates);
    }

    @Override
    public AABB getHitbox() {
        return new AABB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }
}
