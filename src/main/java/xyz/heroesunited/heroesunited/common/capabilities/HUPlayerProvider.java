package xyz.heroesunited.heroesunited.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class HUPlayerProvider implements ICapabilitySerializable<Tag> {

    public static final Capability<IHUPlayer> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private final LazyOptional<IHUPlayer> instance;

    public HUPlayerProvider(Player player) {
        instance = LazyOptional.of(() -> new HUPlayer(player));
    }

    @Override
    public Tag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty")).serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag) {
            instance.orElseThrow(() -> new IllegalArgumentException("HUPlayer must not be empty!")).deserializeNBT(((CompoundTag) nbt));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

}
