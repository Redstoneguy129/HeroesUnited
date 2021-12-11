package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class HUPlayerProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<IHUPlayer> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<IHUPlayer> instance;

    public HUPlayerProvider(LivingEntity entity) {
        instance = LazyOptional.of(() -> new HUPlayer(entity));
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty")).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty!")).deserializeNBT(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
