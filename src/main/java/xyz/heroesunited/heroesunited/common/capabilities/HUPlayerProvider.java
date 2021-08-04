package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class HUPlayerProvider implements ICapabilitySerializable<NbtElement> {

    @CapabilityInject(IHUPlayer.class)
    public static Capability<IHUPlayer> CAPABILITY = null;
    private final LazyOptional<IHUPlayer> instance;

    public HUPlayerProvider(PlayerEntity player) {
        instance = LazyOptional.of(() -> new HUPlayer(player));
    }

    @Override
    public NbtElement serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty")), null);
    }

    @Override
    public void deserializeNBT(NbtElement nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty!")), null, nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
