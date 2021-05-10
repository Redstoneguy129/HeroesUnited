package xyz.heroesunited.heroesunited.common.capabilities.hudata;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import xyz.heroesunited.heroesunited.common.capabilities.IHUPlayer;

import javax.annotation.Nonnull;

public class HUDataProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IHUPlayer.class)
    public static Capability<IHUDataCap> CAPABILITY = null;
    private LazyOptional<IHUDataCap> instance;

    public HUDataProvider(Entity entity) {
        instance = LazyOptional.of(() -> new HUDataCap(entity));
    }

    @Override
    public INBT serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty!")), null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
