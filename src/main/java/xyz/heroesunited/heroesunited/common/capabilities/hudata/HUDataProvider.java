package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap.CAPABILITY;

public class HUDataProvider implements ICapabilitySerializable<NbtElement> {

    private final LazyOptional<IHUDataCap> instance;

    public HUDataProvider(Entity entity) {
        instance = LazyOptional.of(() -> new HUDataCap(entity));
    }

    @Override
    public NbtElement serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUDataCap must not be empty")), null);
    }

    @Override
    public void deserializeNBT(NbtElement nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUDataCap must not be empty!")), null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
