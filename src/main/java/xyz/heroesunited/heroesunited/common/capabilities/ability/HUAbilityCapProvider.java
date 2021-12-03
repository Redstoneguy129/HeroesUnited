package xyz.heroesunited.heroesunited.common.capabilities.ability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static xyz.heroesunited.heroesunited.common.capabilities.ability.HUAbilityCap.CAPABILITY;

public class HUAbilityCapProvider implements ICapabilitySerializable<CompoundTag> {

    private final LazyOptional<IHUAbilityCap> instance;

    public HUAbilityCapProvider(Player player) {
        instance = LazyOptional.of(() -> new HUAbilityCap(player));
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("HUAbilityCap must not be empty")).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.orElseThrow(() -> new IllegalArgumentException("HUAbilityCap must not be empty!")).deserializeNBT(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
