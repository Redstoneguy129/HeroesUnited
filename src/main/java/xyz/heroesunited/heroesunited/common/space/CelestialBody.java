package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class CelestialBody extends ForgeRegistryEntry<CelestialBody> {
    public static IForgeRegistry<CelestialBody> CELESTIAL_BODIES;

    protected Vec3d coordinates;


    public CelestialBody(Vec3d coordinates) {
        this.coordinates = coordinates;
    }


    public Vec3d getCoordinates() {
        return coordinates;
    }

    public void tick(){}

    public void entityInside(Entity entity){}

    public NbtCompound writeNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putDouble("x", coordinates.x);
        compound.putDouble("y", coordinates.y);
        compound.putDouble("z", coordinates.z);
        return compound;
    }

    public Box getHitbox(){
        return null;
    }

    public void readNBT(NbtCompound nbt) {
        coordinates = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
