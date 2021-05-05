package xyz.heroesunited.heroesunited.common.planets;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class CelestialBody extends ForgeRegistryEntry<CelestialBody> {
    public static IForgeRegistry<CelestialBody> CELESTIAL_BODIES;

    private Vector3d coordinates;


    public CelestialBody(Vector3d coordinates) {
        this.coordinates = coordinates;
    }


    public Vector3d getCoordinates() {
        return coordinates;
    }
}
