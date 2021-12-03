package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static xyz.heroesunited.heroesunited.common.capabilities.hudata.HUDataCap.CAPABILITY;

public class HUDataProvider implements ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<IHUDataCap> instance;

    public HUDataProvider(Entity entity) {
        instance = LazyOptional.of(() -> new HUDataCap(entity));
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("HUDataCap must not be empty")).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.orElseThrow(() -> new IllegalArgumentException("HUDataCap must not be empty!")).deserializeNBT(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }
}
