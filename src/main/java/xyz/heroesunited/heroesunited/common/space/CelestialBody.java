package xyz.heroesunited.heroesunited.common.space;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class CelestialBody extends ForgeRegistryEntry<CelestialBody> {
    public static IForgeRegistry<CelestialBody> CELESTIAL_BODIES;

    protected Vector3d coordinates;


    public CelestialBody(Vector3d coordinates) {
        this.coordinates = coordinates;
    }


    public Vector3d getCoordinates() {
        return coordinates;
    }

    public void tick(){}

    public void entityInside(Entity entity){}

    public CompoundNBT writeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putDouble("x", coordinates.x);
        compound.putDouble("y", coordinates.y);
        compound.putDouble("z", coordinates.z);
        return compound;
    }

    public AxisAlignedBB getHitbox(){
        return AxisAlignedBB.ofSize(0, 0, 0);
    }

    public void readNBT(CompoundNBT nbt) {
        coordinates = new Vector3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }
}
