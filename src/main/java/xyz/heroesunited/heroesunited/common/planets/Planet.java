package xyz.heroesunited.heroesunited.common.planets;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;

public class Planet extends CelestialBody {

    public static final HashMap<RegistryKey<World>, Planet> PLANETS_MAP = new HashMap<>();

    private RegistryKey<World> dimension;

    private float scale;

    private Vector3d outCoordinates;

    public Planet(RegistryKey<World> dimension, Vector3d coordinates, float scale, Vector3d outCoordinates) {
        super(coordinates);
        this.dimension = dimension;
        this.scale = scale;
        this.outCoordinates = outCoordinates;
        PLANETS_MAP.put(dimension, this);
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
