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

    private float speed = 1.0E-5F;

    private Vector3d outCoordinates;

    public Planet(RegistryKey<World> dimension, Vector3d coordinates, float scale, Vector3d outCoordinates) {
        super(coordinates);
        this.dimension = dimension;
        this.scale = scale;
        this.outCoordinates = outCoordinates;
        PLANETS_MAP.put(dimension, this);
    }

    public void tick(){
        coordinates = this.coordinates.yRot(speed);
    }

    @Override
    public void entityInside(Entity entity) {
        if (entity.level.getEntities(null, this.getHitbox()).contains(entity) && !entity.level.isClientSide) {
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
        return outCoordinates;
    }
}
