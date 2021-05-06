package xyz.heroesunited.heroesunited.common.planets;

import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.function.Function;

public class Planet extends CelestialBody {

    public static final HashMap<RegistryKey<World>, Planet> PLANETS_MAP = new HashMap<>();

    private RegistryKey<World> dimension;

    private float scale;

    private float speed;

    public Planet(RegistryKey<World> dimension, Vector3d coordinates, float scale, float speed) {
        super(coordinates);
        this.dimension = dimension;
        this.speed = speed;
        this.scale = scale;
        if(dimension != null)
            PLANETS_MAP.put(dimension, this);
    }

    public void tick(){
        coordinates = this.coordinates.yRot(speed);
    }

    @Override
    public void entityInside(Entity entity) {
        if (dimension != null && entity.level.getEntities(null, this.getHitbox()).contains(entity) && !entity.level.isClientSide) {
            entity.changeDimension(((ServerWorld) entity.level).getServer().getLevel( this.getDimension()), new ITeleporter() {
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

    public AxisAlignedBB getHitbox() {
        return new AxisAlignedBB(coordinates.x - scale / 2, coordinates.y - scale / 2, coordinates.z - scale / 2, coordinates.x + scale / 2, coordinates.y + scale / 2, coordinates.z + scale / 2);
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public Vector3d getOutCoordinates() {
        return coordinates.add(new Vector3d(0,scale/2+3,0));
    }

    public boolean hasOxygen() {
        return this == CelestialBodies.EARTH;
    }
}
